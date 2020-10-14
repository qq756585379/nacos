
package com.alibaba.nacos.core.distributed.distro.component;

public interface DistroCallback {

    /**
     * Callback when distro task execute successfully.
     */
    void onSuccess();

    /**
     * Callback when distro task execute failed.
     *
     * @param throwable throwable if execute failed caused by exception
     */
    void onFailed(Throwable throwable);
}
