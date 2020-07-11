package com.sink.comm.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

/**
 * @title:
 * @author: gongsd
 * @time: 2020/6/29.
 */
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 将一个简单对象转化成json
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {

        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将一个json转化成一个对象
     *
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将一个json转化成一个list
     *
     * @param jsonString
     * @param collectionClass
     * @param elementClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJsonToList(String jsonString, @SuppressWarnings("rawtypes") Class<? extends Collection> collectionClass, Class<?> elementClass) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(collectionClass, elementClass);
        try {
            return (T) OBJECT_MAPPER.readValue(jsonString, javaType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将一个json转化成一个map
     *
     * @param jsonString
     * @param mapClass
     * @param keyClass
     * @param valueClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJsonToMap(String jsonString, @SuppressWarnings("rawtypes") Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
        try {
            return (T) OBJECT_MAPPER.readValue(jsonString, javaType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
