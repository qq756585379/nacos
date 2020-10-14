
package com.alibaba.nacos.core.distributed.distro.task.delay;

import com.alibaba.nacos.common.task.NacosTaskProcessor;
import com.alibaba.nacos.common.task.engine.NacosDelayTaskExecuteEngine;
import com.alibaba.nacos.core.distributed.distro.entity.DistroKey;
import com.alibaba.nacos.core.utils.Loggers;

public class DistroDelayTaskExecuteEngine extends NacosDelayTaskExecuteEngine {

    public DistroDelayTaskExecuteEngine() {
        super(DistroDelayTaskExecuteEngine.class.getName(), Loggers.DISTRO);
    }

    @Override
    public void addProcessor(Object key, NacosTaskProcessor taskProcessor) {
        Object actualKey = getActualKey(key);
        super.addProcessor(actualKey, taskProcessor);
    }

    @Override
    public NacosTaskProcessor getProcessor(Object key) {
        Object actualKey = getActualKey(key);
        return super.getProcessor(actualKey);
    }

    private Object getActualKey(Object key) {
        return key instanceof DistroKey ? ((DistroKey) key).getResourceType() : key;
    }
}
