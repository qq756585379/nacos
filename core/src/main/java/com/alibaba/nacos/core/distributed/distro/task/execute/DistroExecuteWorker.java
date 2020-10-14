
package com.alibaba.nacos.core.distributed.distro.task.execute;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.lifecycle.Closeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DistroExecuteWorker implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistroExecuteWorker.class);

    private static final int QUEUE_CAPACITY = 50000;

    private final BlockingQueue<Runnable> queue;

    private final String name;

    private final AtomicBoolean closed;

    public DistroExecuteWorker(final int mod, final int total) {
        name = getClass().getName() + "_" + mod + "%" + total;
        queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        closed = new AtomicBoolean(false);
        new InnerWorker(name).start();
    }

    public String getName() {
        return name;
    }

    /**
     * Execute task without result.
     */
    public void execute(Runnable task) {
        putTask(task);
    }

    /**
     * Execute task with a result.
     */
    public <V> Future<V> execute(Callable<V> task) {
        FutureTask<V> future = new FutureTask(task);
        putTask(future);
        return future;
    }

    private void putTask(Runnable task) {
        try {
            queue.put(task);
        } catch (InterruptedException ire) {
            LOGGER.error(ire.toString(), ire);
        }
    }

    public int pendingTaskCount() {
        return queue.size();
    }

    /**
     * Worker status.
     */
    public String status() {
        return name + ", pending tasks: " + pendingTaskCount();
    }

    @Override
    public void shutdown() throws NacosException {
        queue.clear();
        closed.compareAndSet(false, true);
    }

    /**
     * Inner execute worker.
     */
    private class InnerWorker extends Thread {
        InnerWorker(String name) {
            setDaemon(false);
            setName(name);
        }

        @Override
        public void run() {
            while (!closed.get()) {
                try {
                    Runnable task = queue.take();
                    long begin = System.currentTimeMillis();
                    task.run();
                    long duration = System.currentTimeMillis() - begin;
                    if (duration > 1000L) {
                        LOGGER.warn("distro task {} takes {}ms", task, duration);
                    }
                } catch (Throwable e) {
                    LOGGER.error("[DISTRO-FAILED] " + e.toString(), e);
                }
            }
        }
    }
}
