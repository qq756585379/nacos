
package com.alibaba.nacos.core.distributed.distro.task.execute;

import com.alibaba.nacos.common.task.AbstractExecuteTask;
import com.alibaba.nacos.core.distributed.distro.entity.DistroKey;

public abstract class AbstractDistroExecuteTask extends AbstractExecuteTask implements Runnable {

    private final DistroKey distroKey;

    protected AbstractDistroExecuteTask(DistroKey distroKey) {
        this.distroKey = distroKey;
    }

    protected DistroKey getDistroKey() {
        return distroKey;
    }
}
