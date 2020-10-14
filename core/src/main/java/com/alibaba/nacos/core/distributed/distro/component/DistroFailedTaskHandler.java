
package com.alibaba.nacos.core.distributed.distro.component;

import com.alibaba.nacos.consistency.DataOperation;
import com.alibaba.nacos.core.distributed.distro.entity.DistroKey;

public interface DistroFailedTaskHandler {

    /**
     * Build retry task when distro task execute failed.
     *
     * @param distroKey distro key of failed task
     * @param action action of task
     */
    void retry(DistroKey distroKey, DataOperation action);
}
