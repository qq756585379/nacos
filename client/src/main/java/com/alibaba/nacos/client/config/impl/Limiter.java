
package com.alibaba.nacos.client.config.impl;

import com.alibaba.nacos.client.utils.LogUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Limiter {

    private static final Logger LOGGER = LogUtils.logger(Limiter.class);

    private static final int CAPACITY_SIZE = 1000;

    private static final int LIMIT_TIME = 1000;

    private static final Cache<String, RateLimiter> CACHE = CacheBuilder.newBuilder().initialCapacity(CAPACITY_SIZE).expireAfterAccess(1, TimeUnit.MINUTES).build();

    /**
     * qps 5.
     */
    private static double limit = 5;

    static {
        try {
            String limitTimeStr = System.getProperty("limitTime", String.valueOf(limit));
            limit = Double.parseDouble(limitTimeStr);
            LOGGER.info("limitTime:{}", limit);
        } catch (Exception e) {
            LOGGER.error("init limitTime fail", e);
        }
    }

    static boolean isLimit(String accessKeyID) {
        RateLimiter rateLimiter = null;
        try {
            rateLimiter = CACHE.get(accessKeyID, new Callable<RateLimiter>() {
                @Override
                public RateLimiter call() {
                    return RateLimiter.create(limit);
                }
            });
        } catch (ExecutionException e) {
            LOGGER.error("create limit fail", e);
        }
        if (rateLimiter != null && !rateLimiter.tryAcquire(LIMIT_TIME, TimeUnit.MILLISECONDS)) {
            LOGGER.error("access_key_id:{} limited", accessKeyID);
            return true;
        }
        return false;
    }
}
