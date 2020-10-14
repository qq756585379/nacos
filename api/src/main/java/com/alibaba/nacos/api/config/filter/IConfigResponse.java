

package com.alibaba.nacos.api.config.filter;

public interface IConfigResponse {

    /**
     * get param.
     *
     * @param key key
     * @return value
     */
    Object getParameter(String key);

    /**
     * Get config context.
     *
     * @return configContext
     */
    IConfigContext getConfigContext();

}
