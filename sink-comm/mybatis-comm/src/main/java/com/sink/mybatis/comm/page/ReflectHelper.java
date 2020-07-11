package com.sink.mybatis.comm.page;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @author: gongsd
 * @time: 2020/6/29.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReflectHelper {
    /**
     * 获取obj对象fieldName的Field
     *
     * @param obj
     * @param fieldName
     * @return
     */
    public static Field getFieldByFieldName(Object obj, String fieldName) {
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
            }
        }
        return null;
    }

    /**
     * 获取obj对象fieldName的属性值
     *
     * @param obj
     * @param fieldName
     * @return Object
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object getValueByFieldName(Object obj, String fieldName) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = getFieldByFieldName(obj, fieldName);
        Object value = null;
        if (field != null) {
            if (field.isAccessible()) {
                value = field.get(obj);
            } else {
                field.setAccessible(true);
                value = field.get(obj);
                field.setAccessible(false);
            }
        }
        return value;
    }

    /**
     * 设置obj对象fieldName的属性值
     *
     * @param obj
     * @param fieldName
     * @param value
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setValueByFieldName(Object obj, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        if (field.isAccessible()) {
            field.set(obj, value);
        } else {
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        }
    }

    /**
     * 设置obj父类对象fieldName的属性值
     *
     * @param obj
     * @param fieldName
     * @param value
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setValueByFileNameSup(Object obj, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = obj.getClass().getSuperclass().getDeclaredField(fieldName);
        if (field.isAccessible()) {
            field.set(obj, value);
        } else {
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        }
    }

    public static Object invoke(Class runClass, String method, Object[] params) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Object reObj = null;
        Object obj = runClass.newInstance();
        Method ms = runClass.getMethod(method, transformObjectParameter(params));
        ms.setAccessible(true);
        reObj = ms.invoke(obj, params);
        ms.setAccessible(false);
        return reObj;
    }

    public static Object getPropertyByProprtyName(Object object, String fieldName) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String getter = "get" + firstLetter + fieldName.substring(1);
        Method method = object.getClass().getMethod(getter, new Class[] {});
        // 关闭安全检查提高效率
        method.setAccessible(true);
        Object value = method.invoke(object, new Object[] {});
        method.setAccessible(false);
        return value;
    }

    public static Object setPropertyByProprtyName(Object object, String fieldName, Object[] params) {
        Object reObj = null;
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "set" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, transformObjectParameter(params));
            // 关闭安全检查提高效率
            method.setAccessible(true);
            reObj = method.invoke(object, params);
            method.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reObj;
    }

    /**
     * 反射方法参数转换
     *
     * @param m_gwParameter
     * @return
     */
    private static Class[] transformObjectParameter(Object[] m_gwParameter) {
        Class[] res = null;
        if ((m_gwParameter == null) || (m_gwParameter.length < 1)) {
            return res;
        }
        res = new Class[m_gwParameter.length];

        for (int i = 0; i < m_gwParameter.length; i++) {
            if (Integer.class.isInstance(m_gwParameter[i])) {
                res[i] = int.class;
            } else if (Byte.class.isInstance(m_gwParameter[i])) {
                res[i] = byte.class;
            } else if (Short.class.isInstance(m_gwParameter[i])) {
                res[i] = short.class;
            } else if (HashMap.class.isInstance(m_gwParameter[i])) {
                res[i] = Map.class;
            } else if (ArrayList.class.isInstance(m_gwParameter[i])) {
                res[i] = List.class;
            } else if (m_gwParameter[i] == null) {
                res[i] = null;
            } else {
                res[i] = m_gwParameter[i].getClass();
            }
        }
        return res;
    }
}
