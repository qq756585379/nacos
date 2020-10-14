
package com.alibaba.nacos.core.distributed.distro;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DistroConfig {

    @Value("${nacos.core.protocol.distro.data.sync_delay_ms:1000}")
    private long syncDelayMillis = 1000;

    @Value("${nacos.core.protocol.distro.data.sync_retry_delay_ms:3000}")
    private long syncRetryDelayMillis = 3000;

    @Value("${nacos.core.protocol.distro.data.verify_interval_ms:5000}")
    private long verifyIntervalMillis = 5000;

    @Value("${nacos.core.protocol.distro.data.load_retry_delay_ms:30000}")
    private long loadDataRetryDelayMillis = 30000;

    public long getSyncDelayMillis() {
        return syncDelayMillis;
    }

    public void setSyncDelayMillis(long syncDelayMillis) {
        this.syncDelayMillis = syncDelayMillis;
    }

    public long getSyncRetryDelayMillis() {
        return syncRetryDelayMillis;
    }

    public void setSyncRetryDelayMillis(long syncRetryDelayMillis) {
        this.syncRetryDelayMillis = syncRetryDelayMillis;
    }

    public long getVerifyIntervalMillis() {
        return verifyIntervalMillis;
    }

    public void setVerifyIntervalMillis(long verifyIntervalMillis) {
        this.verifyIntervalMillis = verifyIntervalMillis;
    }

    public long getLoadDataRetryDelayMillis() {
        return loadDataRetryDelayMillis;
    }

    public void setLoadDataRetryDelayMillis(long loadDataRetryDelayMillis) {
        this.loadDataRetryDelayMillis = loadDataRetryDelayMillis;
    }
}
