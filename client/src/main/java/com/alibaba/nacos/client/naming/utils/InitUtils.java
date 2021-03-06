package com.alibaba.nacos.client.naming.utils;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.SystemPropertyKeyConst;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.selector.ExpressionSelector;
import com.alibaba.nacos.api.selector.NoneSelector;
import com.alibaba.nacos.api.selector.SelectorType;
import com.alibaba.nacos.client.utils.LogUtils;
import com.alibaba.nacos.client.utils.ParamUtil;
import com.alibaba.nacos.client.utils.TemplateUtils;
import com.alibaba.nacos.client.utils.TenantUtil;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;

import java.util.Properties;
import java.util.concurrent.Callable;

public class InitUtils {

    public static String initNamespaceForNaming(Properties properties) {
        String tmpNamespace = null;

        String isUseCloudNamespaceParsing = properties.getProperty(PropertyKeyConst.IS_USE_CLOUD_NAMESPACE_PARSING,
            System.getProperty(SystemPropertyKeyConst.IS_USE_CLOUD_NAMESPACE_PARSING,
                String.valueOf(Constants.DEFAULT_USE_CLOUD_NAMESPACE_PARSING)));

        if (Boolean.parseBoolean(isUseCloudNamespaceParsing)) {

            tmpNamespace = TenantUtil.getUserTenantForAns();
            tmpNamespace = TemplateUtils.stringEmptyAndThenExecute(tmpNamespace, new Callable<String>() {
                @Override
                public String call() {
                    String namespace = System.getProperty(SystemPropertyKeyConst.ANS_NAMESPACE);
                    LogUtils.NAMING_LOGGER.info("initializer namespace from System Property :" + namespace);
                    return namespace;
                }
            });

            tmpNamespace = TemplateUtils.stringEmptyAndThenExecute(tmpNamespace, new Callable<String>() {
                @Override
                public String call() {
                    String namespace = System.getenv(PropertyKeyConst.SystemEnv.ALIBABA_ALIWARE_NAMESPACE);
                    LogUtils.NAMING_LOGGER.info("initializer namespace from System Environment :" + namespace);
                    return namespace;
                }
            });
        }

        tmpNamespace = TemplateUtils.stringEmptyAndThenExecute(tmpNamespace, new Callable<String>() {
            @Override
            public String call() {
                String namespace = System.getProperty(PropertyKeyConst.NAMESPACE);
                LogUtils.NAMING_LOGGER.info("initializer namespace from System Property :" + namespace);
                return namespace;
            }
        });

        if (StringUtils.isEmpty(tmpNamespace) && properties != null) {
            tmpNamespace = properties.getProperty(PropertyKeyConst.NAMESPACE);
        }

        tmpNamespace = TemplateUtils.stringEmptyAndThenExecute(tmpNamespace, new Callable<String>() {
            @Override
            public String call() {
                return UtilAndComs.DEFAULT_NAMESPACE_ID;
            }
        });
        return tmpNamespace;
    }

    /**
     * Init web root context.
     */
    public static void initWebRootContext() {
        // support the web context with ali-yun if the app deploy by EDAS
        final String webContext = System.getProperty(SystemPropertyKeyConst.NAMING_WEB_CONTEXT);
        TemplateUtils.stringNotEmptyAndThenExecute(webContext, new Runnable() {
            @Override
            public void run() {
                UtilAndComs.webContext = webContext.indexOf("/") > -1 ? webContext : "/" + webContext;
                UtilAndComs.nacosUrlBase = UtilAndComs.webContext + "/v1/ns";
                UtilAndComs.nacosUrlInstance = UtilAndComs.nacosUrlBase + "/instance";
            }
        });
    }

    /**
     * Init end point.
     *
     * @param properties properties
     * @return end point
     */
    public static String initEndpoint(final Properties properties) {
        if (properties == null) {

            return "";
        }
        // Whether to enable domain name resolution rules
        String isUseEndpointRuleParsing = properties.getProperty(PropertyKeyConst.IS_USE_ENDPOINT_PARSING_RULE,
            System.getProperty(SystemPropertyKeyConst.IS_USE_ENDPOINT_PARSING_RULE,
                String.valueOf(ParamUtil.USE_ENDPOINT_PARSING_RULE_DEFAULT_VALUE)));

        boolean isUseEndpointParsingRule = Boolean.parseBoolean(isUseEndpointRuleParsing);
        String endpointUrl;
        if (isUseEndpointParsingRule) {
            // Get the set domain name information
            endpointUrl = ParamUtil.parsingEndpointRule(properties.getProperty(PropertyKeyConst.ENDPOINT));
            if (StringUtils.isBlank(endpointUrl)) {
                return "";
            }
        } else {
            endpointUrl = properties.getProperty(PropertyKeyConst.ENDPOINT);
        }

        if (StringUtils.isBlank(endpointUrl)) {
            return "";
        }

        String endpointPort = TemplateUtils
            .stringEmptyAndThenExecute(System.getenv(PropertyKeyConst.SystemEnv.ALIBABA_ALIWARE_ENDPOINT_PORT),
                new Callable<String>() {
                    @Override
                    public String call() {

                        return properties.getProperty(PropertyKeyConst.ENDPOINT_PORT);
                    }
                });

        endpointPort = TemplateUtils.stringEmptyAndThenExecute(endpointPort, new Callable<String>() {
            @Override
            public String call() {
                return "8080";
            }
        });

        return endpointUrl + ":" + endpointPort;
    }

    /**
     * Register subType for serialization.
     *
     * <p>
     * Now these subType implementation class has registered in static code. But there are some problem for classloader.
     * The implementation class will be loaded when they are used, which will make deserialize before register.
     * </p>
     *
     * <p>
     * 子类实现类中的静态代码串中已经向Jackson进行了注册，但是由于classloader的原因，只有当 该子类被使用的时候，才会加载该类。这可能会导致Jackson先进性反序列化，再注册子类，从而导致 反序列化失败。
     * </p>
     */
    public static void initSerialization() {
        // TODO register in implementation class or remove subType
        JacksonUtils.registerSubtype(NoneSelector.class, SelectorType.none.name());
        JacksonUtils.registerSubtype(ExpressionSelector.class, SelectorType.label.name());
    }
}
