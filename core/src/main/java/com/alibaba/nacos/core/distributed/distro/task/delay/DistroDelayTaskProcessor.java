
package com.alibaba.nacos.core.distributed.distro.task.delay;

import com.alibaba.nacos.common.task.AbstractDelayTask;
import com.alibaba.nacos.common.task.NacosTaskProcessor;
import com.alibaba.nacos.consistency.DataOperation;
import com.alibaba.nacos.core.distributed.distro.component.DistroComponentHolder;
import com.alibaba.nacos.core.distributed.distro.entity.DistroKey;
import com.alibaba.nacos.core.distributed.distro.task.DistroTaskEngineHolder;
import com.alibaba.nacos.core.distributed.distro.task.execute.DistroSyncChangeTask;

public class DistroDelayTaskProcessor implements NacosTaskProcessor {

    private final DistroTaskEngineHolder distroTaskEngineHolder;

    private final DistroComponentHolder distroComponentHolder;

    public DistroDelayTaskProcessor(DistroTaskEngineHolder distroTaskEngineHolder, DistroComponentHolder distroComponentHolder) {
        this.distroTaskEngineHolder = distroTaskEngineHolder;
        this.distroComponentHolder = distroComponentHolder;
    }

    @Override
    public boolean process(AbstractDelayTask task) {
        if (!(task instanceof DistroDelayTask)) {
            return true;
        }
        DistroDelayTask distroDelayTask = (DistroDelayTask) task;
        DistroKey distroKey = distroDelayTask.getDistroKey();
        if (DataOperation.CHANGE.equals(distroDelayTask.getAction())) {
            DistroSyncChangeTask syncChangeTask = new DistroSyncChangeTask(distroKey, distroComponentHolder);
            distroTaskEngineHolder.getExecuteWorkersManager().dispatch(distroKey, syncChangeTask);
            return true;
        }
        return false;
    }
}
