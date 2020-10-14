
package com.alibaba.nacos.core.utils;

import com.alibaba.nacos.common.executor.ExecutorFactory;
import com.alibaba.nacos.common.executor.NameThreadFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class GlobalExecutor {

    public static void main(String[] args) {
        String a = ClassUtils.getCanonicalName(GlobalExecutor.class);
        System.out.println(a);
    }

    private static final ScheduledExecutorService COMMON_EXECUTOR = ExecutorFactory.Managed
        .newScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class), 4,
            new NameThreadFactory("com.alibaba.nacos.core.common"));

    private static final ScheduledExecutorService DISTRO_EXECUTOR = ExecutorFactory.Managed
        .newScheduledExecutorService(ClassUtils.getCanonicalName(GlobalExecutor.class),
            Runtime.getRuntime().availableProcessors() * 2,
            new NameThreadFactory("com.alibaba.nacos.core.protocal.distro"));

    public static void runWithoutThread(Runnable runnable) {
        runnable.run();
    }

    public static void executeByCommon(Runnable runnable) {
        if (COMMON_EXECUTOR.isShutdown()) {
            return;
        }
        COMMON_EXECUTOR.execute(runnable);
    }

    public static void scheduleByCommon(Runnable runnable, long delayMs) {
        if (COMMON_EXECUTOR.isShutdown()) {
            return;
        }
        COMMON_EXECUTOR.schedule(runnable, delayMs, TimeUnit.MILLISECONDS);
    }

    public static void submitLoadDataTask(Runnable runnable) {
        DISTRO_EXECUTOR.submit(runnable);
    }

    public static void submitLoadDataTask(Runnable runnable, long delay) {
        DISTRO_EXECUTOR.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public static void schedulePartitionDataTimedSync(Runnable runnable, long interval) {
        DISTRO_EXECUTOR.scheduleWithFixedDelay(runnable, interval, interval, TimeUnit.MILLISECONDS);
    }
}
