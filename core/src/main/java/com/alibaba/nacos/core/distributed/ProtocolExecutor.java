
package com.alibaba.nacos.core.distributed;

import com.alibaba.nacos.common.executor.ExecutorFactory;
import com.alibaba.nacos.core.utils.ClassUtils;

import java.util.concurrent.ExecutorService;

public final class ProtocolExecutor {

    private static final ExecutorService CP_MEMBER_CHANGE_EXECUTOR = ExecutorFactory.Managed.newSingleExecutorService(ClassUtils.getCanonicalName(ProtocolManager.class));

    private static final ExecutorService AP_MEMBER_CHANGE_EXECUTOR = ExecutorFactory.Managed.newSingleExecutorService(ClassUtils.getCanonicalName(ProtocolManager.class));

    public static void cpMemberChange(Runnable runnable) {
        CP_MEMBER_CHANGE_EXECUTOR.execute(runnable);
    }

    public static void apMemberChange(Runnable runnable) {
        AP_MEMBER_CHANGE_EXECUTOR.execute(runnable);
    }

}
