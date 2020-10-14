
package com.alibaba.nacos.core.distributed.distro.task;

import com.alibaba.nacos.common.task.NacosTaskProcessor;
import com.alibaba.nacos.core.distributed.distro.component.DistroComponentHolder;
import com.alibaba.nacos.core.distributed.distro.task.delay.DistroDelayTaskExecuteEngine;
import com.alibaba.nacos.core.distributed.distro.task.delay.DistroDelayTaskProcessor;
import com.alibaba.nacos.core.distributed.distro.task.execute.DistroExecuteWorkersManager;
import org.springframework.stereotype.Component;

@Component
public class DistroTaskEngineHolder {

    private final DistroDelayTaskExecuteEngine delayTaskExecuteEngine = new DistroDelayTaskExecuteEngine();

    private final DistroExecuteWorkersManager executeWorkersManager = new DistroExecuteWorkersManager();

    public DistroTaskEngineHolder(DistroComponentHolder distroComponentHolder) {
        DistroDelayTaskProcessor defaultDelayTaskProcessor = new DistroDelayTaskProcessor(this, distroComponentHolder);
        delayTaskExecuteEngine.setDefaultTaskProcessor(defaultDelayTaskProcessor);
    }

    public DistroDelayTaskExecuteEngine getDelayTaskExecuteEngine() {
        return delayTaskExecuteEngine;
    }

    public DistroExecuteWorkersManager getExecuteWorkersManager() {
        return executeWorkersManager;
    }

    public void registerNacosTaskProcessor(Object key, NacosTaskProcessor nacosTaskProcessor) {
        this.delayTaskExecuteEngine.addProcessor(key, nacosTaskProcessor);
    }
}
