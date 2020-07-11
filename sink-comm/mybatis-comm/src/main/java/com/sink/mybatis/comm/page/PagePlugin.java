package com.sink.mybatis.comm.page;

import com.sink.comm.vo.Pager;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import javax.xml.bind.PropertyException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @title:
 * @author: gongsd
 * @time: 2020/6/29.
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class PagePlugin implements Interceptor {

    private static String dialect = ""; // 数据库方言
    private static String pageSqlId = ""; // mapper.xml中需要拦截的ID(正则匹配)
    private static String DIALECT_STR = "dialect";

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object intercept(Invocation ivk) throws Throwable {
        if (ivk.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
            BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(statementHandler, "delegate");
            MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate, "mappedStatement");

            if (mappedStatement.getId().matches(pageSqlId)) { // 拦截需要分页的SQL
                BoundSql boundSql = delegate.getBoundSql();
                Object parameterObject = boundSql.getParameterObject();// 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
                if (parameterObject == null) {
                    throw new NullPointerException("parameterObject尚未实例化！");
                } else {
                    Connection connection = (Connection) ivk.getArgs()[0];
                    String sql = boundSql.getSql();
                    String countSql = "select count(0) as tmp_count from (" + sql + ") t"; // 记录统计
                    PreparedStatement countStmt = connection.prepareStatement(countSql);

                    BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), sql, boundSql.getParameterMappings(), parameterObject);
                    Field metaParamsField = ReflectHelper.getFieldByFieldName(boundSql, "metaParameters");
                    if (metaParamsField != null) {
                        MetaObject mo = (MetaObject) ReflectHelper.getValueByFieldName(boundSql, "metaParameters");
                        ReflectHelper.setValueByFieldName(countBS, "metaParameters", mo);
                    }
                    setParameters(countStmt, mappedStatement, countBS, parameterObject);
                    ResultSet rs = countStmt.executeQuery();
                    int count = 0;
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                    rs.close();
                    countStmt.close();

                    Pager page = null;
                    if (parameterObject instanceof Pager) { // 参数就是Page实体
                        page = (Pager) parameterObject;
                        page.setTotal(count);
                    } else if ((parameterObject instanceof Map) && !(parameterObject instanceof List)) {// 参数为map，Page为map中的一个对象
                        Map<Object, Object> map = (Map<Object, Object>) parameterObject;
                        if (map.containsKey("list")) {
                            List<Object> list = (List<Object>) map.get("list");
                            Object obj = list.get(0);
                            Field pageField = ReflectHelper.getFieldByFieldName(obj, "page");
                            if (pageField != null) {
                                page = (Pager) ReflectHelper.getValueByFieldName(obj, "page");
                                if (page == null) {
                                    page = new Pager();
                                }
                                page.setTotal(count);
                                ReflectHelper.setValueByFileNameSup(obj, "page", page); // 通过反射，对实体对象设置分页对象
                            } else {
                                throw new NoSuchFieldException(obj.getClass().getName() + "不存在 page 属性！");
                            }
                            page.setTotal(count);
                            list.remove(0);
                            list.add(0, obj);
                        } else {
                            if(map.containsKey("page")){
                                page = (Pager) map.get("page");
                                page.setTotal(count);
                                map.remove("page");
                                map.put("page", page);
                            }else{
                                for(Object entry:map.values()){
                                    if(entry instanceof Pager){
                                        page = (Pager)entry;
                                        page.setTotal(count);
                                        break;
                                    }
                                }

                                if(null==page){
                                    throw new NoSuchFieldException(parameterObject.getClass().getName() + "不存在 page 属性！");
                                }
                            }
                        }
                    } else { // 参数为某个实体，该实体拥有Page属性
                        Field pageField = ReflectHelper.getFieldByFieldName(parameterObject, "page");
                        if (pageField != null) {
                            page = (Pager) ReflectHelper.getValueByFieldName(parameterObject, "page");
                            if (page == null) {
                                page = new Pager();
                            }
                            page.setTotal(count);
                            ReflectHelper.setValueByFieldName(parameterObject, "page", page); // 通过反射，对实体对象设置分页对象
                        } else {

                            throw new NoSuchFieldException(parameterObject.getClass().getName() + "不存在 page 属性！");
                        }
                    }
                    String pageSql = generatePageSql(sql, page);
                    ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql); // 将分页sql语句反射回BoundSql.
                }
            }
        }
        return ivk.proceed();
    }

    /**
     * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
     *
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @SuppressWarnings("unchecked")
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {

                        value = metaObject == null ? null : metaObject.getValue(propertyName);

                    }
                    @SuppressWarnings("rawtypes")
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

    /**
     * 根据数据库方言，生成特定的分页sql
     *
     * @param sql
     * @param page
     * @return
     */
    @SuppressWarnings("rawtypes")
    private String generatePageSql(String sql, Pager page) {
        if (page != null && notEmpty(dialect)) {
            StringBuffer pageSql = new StringBuffer();
            if ("mysql".equals(dialect)) {
                pageSql.append(sql);
                pageSql.append(" limit " + page.getCurrentResult() + "," + page.getSize());
            } else if ("oracle".equals(dialect)) {
                pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
                pageSql.append(sql);
                pageSql.append(")tmp_tb where ROWNUM<=");
                pageSql.append(page.getCurrentResult() + page.getSize());
                pageSql.append(") where row_id>");
                pageSql.append(page.getCurrentResult());
            }
            return pageSql.toString();
        } else {
            return sql;
        }
    }

    @Override
    public void setProperties(Properties p) {
        dialect = p.getProperty(DIALECT_STR);
        if (isEmpty(dialect)) {
            try {
                throw new PropertyException("dialect property is not found!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }
        pageSqlId = p.getProperty("pageSqlId");
        if (isEmpty(pageSqlId)) {
            try {
                throw new PropertyException("pageSqlId property is not found!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean notEmpty(String s) {
        return s != null && !"".equals(s) && !"null".equals(s);
    }

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s) || "null".equals(s);
    }

    @Override
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }
}
