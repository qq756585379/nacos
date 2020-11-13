
package com.alibaba.nacos.core.utils;

import com.alibaba.nacos.common.utils.LoggerUtils;
import com.alibaba.nacos.common.utils.Pair;
import com.alibaba.nacos.common.utils.StringUtils;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TimerContext {

    private static final ThreadLocal<Pair<String, Long>> TIME_RECORD = new ThreadLocal<>();

    public static void start(final String name) {
        long startTime = System.currentTimeMillis();
        TIME_RECORD.set(Pair.with(name, startTime));
    }

    public static void end(final Logger logger) {
        end(logger, LoggerUtils.DEBUG);
    }

    public static void end(final Logger logger, final String level) {
        long endTime = System.currentTimeMillis();
        Pair<String, Long> record = TIME_RECORD.get();
        if (StringUtils.equals(level, LoggerUtils.DEBUG)) {
            LoggerUtils.printIfDebugEnabled(logger, "{} cost time : {} ms", record.getFirst(), (endTime - record.getSecond()));
        }
        if (StringUtils.equals(level, LoggerUtils.INFO)) {
            LoggerUtils.printIfInfoEnabled(logger, "{} cost time : {} ms", record.getFirst(), (endTime - record.getSecond()));
        }
        if (StringUtils.equals(level, LoggerUtils.TRACE)) {
            LoggerUtils.printIfTraceEnabled(logger, "{} cost time : {} ms", record.getFirst(), (endTime - record.getSecond()));
        }
        if (StringUtils.equals(level, LoggerUtils.ERROR)) {
            LoggerUtils.printIfErrorEnabled(logger, "{} cost time : {} ms", record.getFirst(), (endTime - record.getSecond()));
        }
        if (StringUtils.equals(level, LoggerUtils.WARN)) {
            LoggerUtils.printIfWarnEnabled(logger, "{} cost time : {} ms", record.getFirst(), (endTime - record.getSecond()));
        }
        TIME_RECORD.remove();
    }

    public static void run(final Runnable job, final String name, final Logger logger) {
        start(name);
        try {
            job.run();
        } finally {
            end(logger);
        }
    }

    public static <V> V run(final Supplier<V> job, final String name, final Logger logger) {
        start(name);
        try {
            return job.get();
        } finally {
            end(logger);
        }
    }

    public static <T, R> R run(final Function<T, R> job, T args, final String name, final Logger logger) {
        start(name);
        try {
            return job.apply(args);
        } finally {
            end(logger);
        }
    }

    public static <T> void run(final Consumer<T> job, T args, final String name, final Logger logger) {
        start(name);
        try {
            job.accept(args);
        } finally {
            end(logger);
        }
    }
}
