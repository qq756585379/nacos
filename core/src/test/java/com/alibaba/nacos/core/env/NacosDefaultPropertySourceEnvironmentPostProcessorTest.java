
package com.alibaba.nacos.core.env;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

import static com.alibaba.nacos.core.env.NacosDefaultPropertySourceEnvironmentPostProcessor.PROPERTY_SOURCE_NAME;
import static com.alibaba.nacos.core.env.NacosDefaultPropertySourceEnvironmentPostProcessor.RESOURCE_LOCATION_PATTERN;
import static java.util.Arrays.asList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NacosDefaultPropertySourceEnvironmentPostProcessorTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class NacosDefaultPropertySourceEnvironmentPostProcessorTest {

    @Autowired
    private ConfigurableEnvironment environment;

    @Test
    public void testNacosDefaultPropertySourcePresent() {
        MutablePropertySources propertySources = environment.getPropertySources();
        // "nacos-default" must be present
        Assert.assertTrue(propertySources.contains("nacos-default"));
        // Get PropertySource via PROPERTY_SOURCE_NAME
        PropertySource propertySource = getNacosDefaultPropertySource();
        // "nacos-default" must be present
        Assert.assertNotNull(propertySource);
        // make sure propertySource is last one
        Assert.assertEquals(propertySources.size() - 1, propertySources.precedenceOf(propertySource));
    }

    @Test
    public void testDefaultProperties() {

        // Web Server
        assertPropertyEquals("server.port", "8848");
        assertPropertyEquals("server.tomcat.uri-encoding", "UTF-8");

        // HTTP Encoding
        assertPropertyEquals("spring.http.encoding.force", "true");
        assertPropertyEquals("spring.http.encoding.enabled", "true");

        // i18n
        assertPropertyEquals("spring.messages.encoding", "UTF-8");
    }

    @Test
    public void testDefaultPropertyNames() {

        assertPropertyNames("nacos.version", "server.servlet.contextPath", "server.port", "server.tomcat.uri-encoding",
                "spring.http.encoding.force", "spring.http.encoding.enabled", "spring.messages.encoding",
                "spring.autoconfigure.exclude");
    }

    private void assertPropertyNames(String... propertyNames) {

        CompositePropertySource propertySource = getNacosDefaultPropertySource();

        Assert.assertEquals("Please Properties from resources[" + RESOURCE_LOCATION_PATTERN + "]",
                new HashSet<String>(asList(propertyNames)),
                new HashSet<String>(asList(propertySource.getPropertyNames())));
    }

    private void assertPropertyEquals(String propertyName, String expectedValue) {
        PropertySource propertySource = getNacosDefaultPropertySource();
        Assert.assertEquals(expectedValue, propertySource.getProperty(propertyName));
    }

    private CompositePropertySource getNacosDefaultPropertySource() {
        MutablePropertySources propertySources = environment.getPropertySources();
        // Get PropertySource via PROPERTY_SOURCE_NAME
        CompositePropertySource propertySource = (CompositePropertySource) propertySources.get(PROPERTY_SOURCE_NAME);
        return propertySource;
    }
}
