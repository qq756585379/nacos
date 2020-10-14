
package com.alibaba.nacos.common.http.handler;

import com.alibaba.nacos.common.utils.JacksonUtils;

import java.io.InputStream;
import java.lang.reflect.Type;

public final class ResponseHandler {

    public static <T> T convert(String s, Class<T> cls) throws Exception {
        return JacksonUtils.toObj(s, cls);
    }

    public static <T> T convert(String s, Type type) throws Exception {
        return JacksonUtils.toObj(s, type);
    }

    public static <T> T convert(InputStream inputStream, Type type) throws Exception {
        return JacksonUtils.toObj(inputStream, type);
    }
}
