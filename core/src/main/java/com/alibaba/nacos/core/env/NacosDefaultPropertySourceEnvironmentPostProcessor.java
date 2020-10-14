
package com.alibaba.nacos.core.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

public class NacosDefaultPropertySourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    public static final String PROPERTY_SOURCE_NAME = "nacos-default";

    public static final String RESOURCE_LOCATION_PATTERN = "classpath*:META-INF/nacos-default.properties";

    private static final String FILE_ENCODING = "UTF-8";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        ResourceLoader resourceLoader = getResourceLoader(application);
        processPropertySource(environment, resourceLoader);
    }

    private ResourceLoader getResourceLoader(SpringApplication application) {
        ResourceLoader resourceLoader = application.getResourceLoader();
        if (resourceLoader == null) {
            resourceLoader = new DefaultResourceLoader(application.getClassLoader());
        }
        return resourceLoader;
    }

    private void processPropertySource(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
        try {
            PropertySource nacosDefaultPropertySource = buildPropertySource(resourceLoader);
            MutablePropertySources propertySources = environment.getPropertySources();
            // append nacosDefaultPropertySource as last one in order to be overrided by higher order
            propertySources.addLast(nacosDefaultPropertySource);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private PropertySource buildPropertySource(ResourceLoader resourceLoader) throws IOException {
        CompositePropertySource propertySource = new CompositePropertySource(PROPERTY_SOURCE_NAME);
        appendPropertySource(propertySource, resourceLoader);
        return propertySource;
    }

    private void appendPropertySource(CompositePropertySource propertySource, ResourceLoader resourceLoader)
        throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resourcePatternResolver.getResources(RESOURCE_LOCATION_PATTERN);
        for (Resource resource : resources) {
            // Add if exists
            if (resource.exists()) {
                String internalName = String.valueOf(resource.getURL());
                propertySource.addPropertySource(new ResourcePropertySource(internalName, new EncodedResource(resource, FILE_ENCODING)));
            }
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
