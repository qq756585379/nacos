
package com.alibaba.nacos.client.utils;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidatorUtils {

    private static final Pattern CONTEXT_PATH_MATCH = Pattern.compile("(/)\\1+");

    public static void checkInitParam(Properties properties) throws NacosException {
        checkContextPath(properties.getProperty(PropertyKeyConst.CONTEXT_PATH));
    }

    static void checkContextPath(String contextPath) {
        if (contextPath == null) {
            return;
        }
        Matcher matcher = CONTEXT_PATH_MATCH.matcher(contextPath);
        if (matcher.find()) {
            throw new IllegalArgumentException("Illegal url path expression");
        }
    }
}
