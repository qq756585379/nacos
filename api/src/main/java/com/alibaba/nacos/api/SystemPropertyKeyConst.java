
package com.alibaba.nacos.api;

public interface SystemPropertyKeyConst {

    String NAMING_SERVER_PORT = "nacos.naming.exposed.port";

    String NAMING_WEB_CONTEXT = "nacos.naming.web.context";

    String IS_USE_CLOUD_NAMESPACE_PARSING = "nacos.use.cloud.namespace.parsing";

    String ANS_NAMESPACE = "ans.namespace";

    /**
     * It is also supported by the -D parameter.
     */
    String IS_USE_ENDPOINT_PARSING_RULE = "nacos.use.endpoint.parsing.rule";
}
