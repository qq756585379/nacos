
package com.alibaba.nacos.core.monitor;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Timer;

public final class MetricsMonitor {

    private static final DistributionSummary RAFT_READ_INDEX_FAILED;

    private static final DistributionSummary RAFT_FROM_LEADER;

    private static final Timer RAFT_APPLY_LOG_TIMER;

    private static final Timer RAFT_APPLY_READ_TIMER;

    static {
        RAFT_READ_INDEX_FAILED = NacosMeterRegistry.summary("protocol", "raft_read_index_failed");
        RAFT_FROM_LEADER = NacosMeterRegistry.summary("protocol", "raft_read_from_leader");

        RAFT_APPLY_LOG_TIMER = NacosMeterRegistry.timer("protocol", "raft_apply_log_timer");
        RAFT_APPLY_READ_TIMER = NacosMeterRegistry.timer("protocol", "raft_apply_read_timer");
    }

    public static void raftReadIndexFailed() {
        RAFT_READ_INDEX_FAILED.record(1);
    }

    public static void raftReadFromLeader() {
        RAFT_FROM_LEADER.record(1);
    }

    public static Timer getRaftApplyLogTimer() {
        return RAFT_APPLY_LOG_TIMER;
    }

    public static Timer getRaftApplyReadTimer() {
        return RAFT_APPLY_READ_TIMER;
    }
}
