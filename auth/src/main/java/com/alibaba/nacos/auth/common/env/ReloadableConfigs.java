
package com.alibaba.nacos.auth.common.env;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class ReloadableConfigs {

    private static final String FILE_PREFIX = "file:";

    private Properties properties;

    @Value("${spring.config.location:}")
    private String path;

    @Scheduled(fixedRate = 5000)
    public void reload() throws IOException {
        final Properties properties = new Properties();
        InputStream inputStream = null;
        if (StringUtils.isNotBlank(path) && path.contains(FILE_PREFIX)) {
            String[] paths = path.split(",");
            path = paths[paths.length - 1].substring(FILE_PREFIX.length());
        }
        try {
            inputStream = new FileInputStream(new File(path + "application.properties"));
        } catch (Exception ignore) {
        }
        if (inputStream == null) {
            inputStream = getClass().getResourceAsStream("/application.properties");
        }
        properties.load(inputStream);
        inputStream.close();
        this.properties = properties;
    }

    public final Properties getProperties() {
        return properties;
    }
}
