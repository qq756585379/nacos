
package com.alibaba.nacos.core.distributed.distro.component;

import com.alibaba.nacos.core.distributed.distro.entity.DistroData;
import com.alibaba.nacos.core.distributed.distro.entity.DistroKey;

public interface DistroTransportAgent {

    /**
     * Sync data.
     *
     * @param data         data
     * @param targetServer target server
     * @return true is sync successfully, otherwise false
     */
    boolean syncData(DistroData data, String targetServer);

    /**
     * Sync data with callback.
     *
     * @param data         data
     * @param targetServer target server
     * @param callback     callback
     */
    void syncData(DistroData data, String targetServer, DistroCallback callback);

    /**
     * Sync verify data.
     *
     * @param verifyData   verify data
     * @param targetServer target server
     * @return true is verify successfully, otherwise false
     */
    boolean syncVerifyData(DistroData verifyData, String targetServer);

    /**
     * Sync verify data.
     *
     * @param verifyData   verify data
     * @param targetServer target server
     * @param callback     callback
     */
    void syncVerifyData(DistroData verifyData, String targetServer, DistroCallback callback);

    /**
     * get Data from target server.
     *
     * @param key          key of data
     * @param targetServer target server
     * @return distro data
     */
    DistroData getData(DistroKey key, String targetServer);

    /**
     * Get all datum snapshot from target server.
     *
     * @param targetServer target server.
     * @return distro data
     */
    DistroData getDatumSnapshot(String targetServer);
}
