
package com.alibaba.nacos.common.http;

import com.alibaba.nacos.common.model.RestResult;

public interface Callback<T> {

    /**
     * Callback after the request is responded.
     *
     * @param result {@link RestResult}
     */
    void onReceive(RestResult<T> result);

    /**
     * An error occurred during the request.
     *
     * @param throwable {@link Throwable}
     */
    void onError(Throwable throwable);

    /**
     * Callback when the request is cancelled.
     */
    void onCancel();
}
