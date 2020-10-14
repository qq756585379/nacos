
package com.alibaba.nacos.common.http.handler;

import com.alibaba.nacos.common.utils.JacksonUtils;

public final class RequestHandler {

    public static String parse(Object object) throws Exception {
        return JacksonUtils.toJson(object);
    }
}
