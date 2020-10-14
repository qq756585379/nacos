
package com.alibaba.nacos.client.utils;

import com.alibaba.nacos.common.utils.StringUtils;

import java.util.concurrent.Callable;

public class TemplateUtils {

    public static void stringNotEmptyAndThenExecute(String source, Runnable runnable) {
        if (StringUtils.isNotEmpty(source)) {
            try {
                runnable.run();
            } catch (Exception e) {
                LogUtils.NAMING_LOGGER.error("string empty and then execute cause an exception.", e);
            }
        }
    }

    public static String stringEmptyAndThenExecute(String source, Callable<String> callable) {
        if (StringUtils.isEmpty(source)) {
            try {
                return callable.call();
            } catch (Exception e) {
                LogUtils.NAMING_LOGGER.error("string empty and then execute cause an exception.", e);
            }
        }
        return source.trim();
    }

    public static String stringBlankAndThenExecute(String source, Callable<String> callable) {
        if (StringUtils.isBlank(source)) {
            try {
                return callable.call();
            } catch (Exception e) {
                LogUtils.NAMING_LOGGER.error("string empty and then execute cause an exception.", e);
            }
        }
        return source.trim();
    }
}
