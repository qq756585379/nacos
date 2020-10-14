
package com.alibaba.nacos.core.cluster;

import com.alibaba.nacos.common.utils.ExceptionUtil;
import com.alibaba.nacos.core.utils.Loggers;

@SuppressWarnings("PMD.AbstractClassShouldStartWithAbstractNamingRule")
public abstract class Task implements Runnable {

    protected volatile boolean shutdown = false;

    @Override
    public void run() {
        if (shutdown) {
            return;
        }
        try {
            executeBody();
        } catch (Throwable t) {
            Loggers.CORE.error("this task execute has error : {}", ExceptionUtil.getStackTrace(t));
        } finally {
            if (!shutdown) {
                after();
            }
        }
    }

    /**
     * Task executive.
     */
    protected abstract void executeBody();

    /**
     * after executeBody should do.
     */
    protected void after() {

    }

    public void shutdown() {
        shutdown = true;
    }
}
