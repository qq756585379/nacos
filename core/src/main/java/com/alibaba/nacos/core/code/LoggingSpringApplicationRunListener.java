
package com.alibaba.nacos.core.code;

import com.alibaba.nacos.core.utils.ApplicationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.springframework.boot.context.logging.LoggingApplicationListener.CONFIG_PROPERTY;
import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

public class LoggingSpringApplicationRunListener implements SpringApplicationRunListener, Ordered {

    private static final String DEFAULT_NACOS_LOGBACK_LOCATION = "classpath:META-INF/logback/nacos.xml";

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingSpringApplicationRunListener.class);

    @Override
    public void starting() {
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        ApplicationUtils.injectEnvironment(environment);
        if (!environment.containsProperty("logging.config")) {
            System.setProperty("logging.config", "classpath:META-INF/logback/nacos.xml");
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("There is no property named \"{}\" in Spring Boot Environment, " + "and whose value is {} will be set into System's Properties", CONFIG_PROPERTY, DEFAULT_NACOS_LOGBACK_LOCATION);
            }
        }
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {

    }

    @Override
    public void started(ConfigurableApplicationContext context) {

    }

    @Override
    public void running(ConfigurableApplicationContext context) {

    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {

    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
