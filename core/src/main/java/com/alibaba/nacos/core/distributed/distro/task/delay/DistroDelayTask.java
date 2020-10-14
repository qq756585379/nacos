
package com.alibaba.nacos.core.distributed.distro.task.delay;

import com.alibaba.nacos.common.task.AbstractDelayTask;
import com.alibaba.nacos.consistency.DataOperation;
import com.alibaba.nacos.core.distributed.distro.entity.DistroKey;

public class DistroDelayTask extends AbstractDelayTask {

    private final DistroKey distroKey;

    private DataOperation action;

    private long createTime;

    public DistroDelayTask(DistroKey distroKey, long delayTime) {
        this(distroKey, DataOperation.CHANGE, delayTime);
    }

    public DistroDelayTask(DistroKey distroKey, DataOperation action, long delayTime) {
        this.distroKey = distroKey;
        this.action = action;
        this.createTime = System.currentTimeMillis();
        setLastProcessTime(createTime);
        setTaskInterval(delayTime);
    }

    public DistroKey getDistroKey() {
        return distroKey;
    }

    public DataOperation getAction() {
        return action;
    }

    public long getCreateTime() {
        return createTime;
    }

    @Override
    public void merge(AbstractDelayTask task) {
        if (!(task instanceof DistroDelayTask)) {
            return;
        }
        DistroDelayTask newTask = (DistroDelayTask) task;
        if (!action.equals(newTask.getAction()) && createTime < newTask.getCreateTime()) {
            action = newTask.getAction();
            createTime = newTask.getCreateTime();
        }
        setLastProcessTime(newTask.getLastProcessTime());
    }
}
