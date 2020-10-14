

package com.alibaba.nacos.api.config.filter;

public interface IConfigContext {

    /**
     * Get context param by key.
     *
     * @param key parameter key
     * @return context
     */
    Object getParameter(String key);

    /**
     * Set context param.
     *
     * @param key   key
     * @param value value
     */
    void setParameter(String key, Object value);
}
