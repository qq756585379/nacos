
package com.alibaba.nacos.core.distributed.distro.component;

import com.alibaba.nacos.core.distributed.distro.entity.DistroData;
import com.alibaba.nacos.core.distributed.distro.entity.DistroKey;

public interface DistroDataStorage {

    /**
     * Get distro datum.
     *
     * @param distroKey key of distro datum
     * @return need to sync datum
     */
    DistroData getDistroData(DistroKey distroKey);

    /**
     * Get all distro datum snapshot.
     *
     * @return all datum
     */
    DistroData getDatumSnapshot();

    /**
     * Get verify datum.
     *
     * @return verify datum
     */
    DistroData getVerifyData();
}
