
package com.alibaba.nacos.core.utils;

public interface Constants {

    /**
     * Spring Profile : "standalone".
     */
    String STANDALONE_SPRING_PROFILE = "standalone";

    /**
     * The System property name of  Standalone mode.
     */
    String STANDALONE_MODE_PROPERTY_NAME = "nacos.standalone";

    /**
     * The System property name of  Function mode.
     */
    String FUNCTION_MODE_PROPERTY_NAME = "nacos.functionMode";

    /**
     * The System property name of prefer hostname over ip.
     */
    String PREFER_HOSTNAME_OVER_IP_PROPERTY_NAME = "nacos.preferHostnameOverIp";

    /**
     * the root context path.
     */
    String ROOT_WEB_CONTEXT_PATH = "/";

    String NACOS_SERVER_IP = "nacos.server.ip";

    String USE_ONLY_SITE_INTERFACES = "nacos.inetutils.use-only-site-local-interfaces";
    String PREFERRED_NETWORKS = "nacos.inetutils.preferred-networks";
    String IGNORED_INTERFACES = "nacos.inetutils.ignored-interfaces";
    String IP_ADDRESS = "nacos.inetutils.ip-address";
    String PREFER_HOSTNAME_OVER_IP = "nacos.inetutils.prefer-hostname-over-ip";
    String SYSTEM_PREFER_HOSTNAME_OVER_IP = "nacos.preferHostnameOverIp";
    String WEB_CONTEXT_PATH = "server.servlet.context-path";
    String COMMA_DIVISION = ",";

    String NACOS_SERVER_HEADER = "Nacos-Server";

    String REQUEST_PATH_SEPARATOR = "-->";
}
