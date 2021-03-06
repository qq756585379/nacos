
package com.alibaba.nacos.core.code;

import com.alibaba.nacos.core.utils.ApplicationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;

import static com.alibaba.nacos.core.utils.Constants.STANDALONE_MODE_PROPERTY_NAME;
import static com.alibaba.nacos.core.utils.Constants.STANDALONE_SPRING_PROFILE;

public class StandaloneProfileApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, PriorityOrdered {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneProfileApplicationListener.class);

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        ApplicationUtils.injectEnvironment(environment);
        if (environment.getProperty(STANDALONE_MODE_PROPERTY_NAME, boolean.class, false)) {
            environment.addActiveProfile(STANDALONE_SPRING_PROFILE);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Spring Environment's active profiles : {} in standalone mode : {}", Arrays.asList(environment.getActiveProfiles()), ApplicationUtils.getStandaloneMode());
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
