
package com.alibaba.nacos.api.config;

import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;

public interface ConfigService {

    String getConfig(String dataId, String group, long timeoutMs) throws NacosException;

    String getConfigAndSignListener(String dataId, String group, long timeoutMs, Listener listener) throws NacosException;

    void addListener(String dataId, String group, Listener listener) throws NacosException;

    boolean publishConfig(String dataId, String group, String content) throws NacosException;

    boolean removeConfig(String dataId, String group) throws NacosException;

    void removeListener(String dataId, String group, Listener listener);

    String getServerStatus();

    void shutDown() throws NacosException;
}
