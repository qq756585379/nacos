
package com.alibaba.nacos.client.utils;

import com.alibaba.nacos.client.logging.AbstractNacosLogging;
import com.alibaba.nacos.client.logging.log4j2.Log4J2NacosLogging;
import com.alibaba.nacos.client.logging.logback.LogbackNacosLogging;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class LogUtils {

    public static final Logger NAMING_LOGGER;

    static {
        try {
            boolean isLogback = false;
            AbstractNacosLogging nacosLogging;

            try {
                Class.forName("ch.qos.logback.classic.Logger");
                nacosLogging = new LogbackNacosLogging();
                isLogback = true;
            } catch (ClassNotFoundException e) {
                nacosLogging = new Log4J2NacosLogging();
            }

            try {
                nacosLogging.loadConfiguration();
            } catch (Throwable t) {
                if (isLogback) {
                    getLogger(LogUtils.class).warn("Load Logback Configuration of Nacos fail, message: {}", t.getMessage());
                } else {
                    getLogger(LogUtils.class).warn("Load Log4j Configuration of Nacos fail, message: {}", t.getMessage());
                }
            }
        } catch (Throwable ex) {
            getLogger(LogUtils.class).warn("Init Nacos Logging fail, message: {}", ex.getMessage());
        }

        NAMING_LOGGER = getLogger("com.alibaba.nacos.client.naming");
    }

    public static Logger logger(Class<?> clazz) {
        return getLogger(clazz);
    }
}
