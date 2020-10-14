
package com.alibaba.nacos.common.lifecycle;

import com.alibaba.nacos.api.exception.NacosException;

public interface Closeable {

    void shutdown() throws NacosException;

}
