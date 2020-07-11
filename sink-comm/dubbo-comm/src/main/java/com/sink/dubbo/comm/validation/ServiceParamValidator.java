package com.sink.dubbo.comm.validation;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.bytecode.ClassGenerator;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ReflectUtils;
import com.alibaba.dubbo.validation.Validator;
import com.sink.dubbo.comm.exception.ParamValidationException;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;

import javax.validation.Constraint;
import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * @author Iven
 * @date 2015-02-11
 */
public class ServiceParamValidator implements Validator {
    private static final Logger logger = LoggerFactory.getLogger(ServiceParamValidator.class);

    private final Class<?> clazz;
    private final javax.validation.Validator validator;
    com.alibaba.dubbo.validation.Validation c;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public ServiceParamValidator(URL url) {
        this.clazz = ReflectUtils.forName(url.getServiceInterface());
        String jvalidation = url.getParameter("spvalidation");
        ValidatorFactory factory;
        if (jvalidation != null && jvalidation.length() > 0) {
            factory = javax.validation.Validation.byProvider((Class) ReflectUtils.forName(jvalidation)).configure().buildValidatorFactory();
        } else {
            factory = javax.validation.Validation.buildDefaultValidatorFactory();
        }
        this.validator = factory.getValidator();
    }

    @Override
    public void validate(String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Exception {
        String methodClassName = clazz.getName() + "$" + toUpperMethoName(methodName);
        Class<?> methodClass = null;
        try {
            methodClass = Class.forName(methodClassName, false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
        }
        Set<ConstraintViolation<?>> violations = new HashSet<ConstraintViolation<?>>();
        Method method = clazz.getMethod(methodName, parameterTypes);
        Object parameterBean = getMethodParameterBean(clazz, method, arguments);
        if (parameterBean != null) {
            if (methodClass != null) {
                violations.addAll(validator.validate(parameterBean, Default.class, clazz, methodClass));
            } else {
                violations.addAll(validator.validate(parameterBean, Default.class, clazz));
            }
        }
        for (Object arg : arguments) {
            validate(violations, arg, clazz, methodClass);
        }
        if (violations.size() > 0) {
            logger.error("Failed to validate service: " + clazz.getName() + ", method: " + methodName + ", cause: " + violations);
            Iterator<ConstraintViolation<?>> iterator = violations.iterator();
            ConstraintViolation<?> constraintViolation = iterator.next();
            String errorMsg = constraintViolation.getMessageTemplate();

            throw new ParamValidationException(errorMsg);
        }
    }

    private void validate(Set<ConstraintViolation<?>> violations, Object arg, Class<?> clazz, Class<?> methodClass) {
        if (arg != null && !isPrimitives(arg.getClass())) {
            if (Object[].class.isInstance(arg)) {
                for (Object item : (Object[]) arg) {
                    validate(violations, item, clazz, methodClass);
                }
            } else if (Collection.class.isInstance(arg)) {
                for (Object item : (Collection<?>) arg) {
                    validate(violations, item, clazz, methodClass);
                }
            } else if (Map.class.isInstance(arg)) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) arg).entrySet()) {
                    validate(violations, entry.getKey(), clazz, methodClass);
                    validate(violations, entry.getValue(), clazz, methodClass);
                }
            } else {
                if (methodClass != null) {
                    violations.addAll(validator.validate(arg, Default.class, clazz, methodClass));
                } else {
                    violations.addAll(validator.validate(arg, Default.class, clazz));
                }
            }
        }
    }

    private static boolean isPrimitives(Class<?> cls) {
        if (cls.isArray()) {
            return isPrimitive(cls.getComponentType());
        }
        return isPrimitive(cls);
    }

    private static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == String.class || cls == Boolean.class || cls == Character.class
                || Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls);
    }

    private static Object getMethodParameterBean(Class<?> clazz, Method method, Object[] args) {
        if (!hasConstraintParameter(method)) {
            return null;
        }
        try {
            String upperName = toUpperMethoName(method.getName());
            String parameterSimpleName = upperName + "Parameter";
            String parameterClassName = clazz.getName() + "$" + parameterSimpleName;
            Class<?> parameterClass;
            try {
                parameterClass = (Class<?>) Class.forName(parameterClassName, true, clazz.getClassLoader());
            } catch (ClassNotFoundException e) {
                ClassPool pool = ClassGenerator.getClassPool(clazz.getClassLoader());
                CtClass ctClass = pool.makeClass(parameterClassName);
                ClassFile classFile = ctClass.getClassFile();
                classFile.setVersionToJava5();
                ctClass.addConstructor(CtNewConstructor.defaultConstructor(pool.getCtClass(parameterClassName)));
                // parameter fields
                Class<?>[] parameterTypes = method.getParameterTypes();
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> type = parameterTypes[i];
                    Annotation[] annotations = parameterAnnotations[i];
                    AnnotationsAttribute attribute = new AnnotationsAttribute(classFile.getConstPool(), AnnotationsAttribute.visibleTag);
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().isAnnotationPresent(Constraint.class)) {
                            javassist.bytecode.annotation.Annotation ja = new javassist.bytecode.annotation.Annotation(
                                    classFile.getConstPool(), pool.getCtClass(annotation.annotationType().getName()));
                            Method[] members = annotation.annotationType().getMethods();
                            for (Method member : members) {
                                if (Modifier.isPublic(member.getModifiers())
                                        && member.getParameterTypes().length == 0
                                        && member.getDeclaringClass() == annotation.annotationType()) {
                                    Object value = member.invoke(annotation, new Object[0]);
                                    if (value != null && !value.equals(member.getDefaultValue())) {
                                        MemberValue memberValue = createMemberValue(
                                                classFile.getConstPool(), pool.get(member.getReturnType().getName()), value);
                                        ja.addMemberValue(member.getName(), memberValue);
                                    }
                                }
                            }
                            attribute.addAnnotation(ja);
                        }
                    }
                    String fieldName = method.getName() + "Argument" + i;
                    CtField ctField = CtField.make("public " + type.getCanonicalName() + " " + fieldName + ";", pool.getCtClass(parameterClassName));
                    ctField.getFieldInfo().addAttribute(attribute);
                    ctClass.addField(ctField);
                }
                parameterClass = ctClass.toClass();
            }
            Object parameterBean = parameterClass.newInstance();
            for (int i = 0; i < args.length; i++) {
                Field field = parameterClass.getField(method.getName() + "Argument" + i);
                field.set(parameterBean, args[i]);
            }
            return parameterBean;
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

    private static boolean hasConstraintParameter(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations != null && parameterAnnotations.length > 0) {
            for (Annotation[] annotations : parameterAnnotations) {
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().isAnnotationPresent(Constraint.class)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String toUpperMethoName(String methodName) {
        return methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
    }

    // Copy from javassist.bytecode.annotation.Annotation.createMemberValue(ConstPool, CtClass);
    private static MemberValue createMemberValue(ConstPool cp, CtClass type, Object value) throws NotFoundException {
        MemberValue memberValue = javassist.bytecode.annotation.Annotation.createMemberValue(cp, type);
        if (memberValue instanceof BooleanMemberValue)
            ((BooleanMemberValue) memberValue).setValue((Boolean) value);
        else if (memberValue instanceof ByteMemberValue)
            ((ByteMemberValue) memberValue).setValue((Byte) value);
        else if (memberValue instanceof CharMemberValue)
            ((CharMemberValue) memberValue).setValue((Character) value);
        else if (memberValue instanceof ShortMemberValue)
            ((ShortMemberValue) memberValue).setValue((Short) value);
        else if (memberValue instanceof IntegerMemberValue)
            ((IntegerMemberValue) memberValue).setValue((Integer) value);
        else if (memberValue instanceof LongMemberValue)
            ((LongMemberValue) memberValue).setValue((Long) value);
        else if (memberValue instanceof FloatMemberValue)
            ((FloatMemberValue) memberValue).setValue((Float) value);
        else if (memberValue instanceof DoubleMemberValue)
            ((DoubleMemberValue) memberValue).setValue((Double) value);
        else if (memberValue instanceof ClassMemberValue)
            ((ClassMemberValue) memberValue).setValue(((Class<?>) value).getName());
        else if (memberValue instanceof StringMemberValue)
            ((StringMemberValue) memberValue).setValue((String) value);
        else if (memberValue instanceof EnumMemberValue)
            ((EnumMemberValue) memberValue).setValue(((Enum<?>) value).name());
        /* else if (memberValue instanceof AnnotationMemberValue) */
        else if (memberValue instanceof ArrayMemberValue) {
            CtClass arrayType = type.getComponentType();
            int len = Array.getLength(value);
            MemberValue[] members = new MemberValue[len];
            for (int i = 0; i < len; i++) {
                members[i] = createMemberValue(cp, arrayType, Array.get(value, i));
            }
            ((ArrayMemberValue) memberValue).setValue(members);
        }
        return memberValue;
    }
}
