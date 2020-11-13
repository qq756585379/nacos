
package com.alibaba.nacos.core.distributed.id;

import com.alibaba.nacos.consistency.IdGenerator;
import com.alibaba.nacos.core.utils.ApplicationUtils;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class SnowFlowerIdGenerator implements IdGenerator {

    /**
     * Start time intercept (2018-08-05 08:34)
     */
    public static final long EPOCH = 1533429240000L;

    private static final Logger logger = LoggerFactory.getLogger(SnowFlowerIdGenerator.class);

    // 序列所占位数
    private static final long SEQUENCE_BITS = 12L;

    // workerId所占位数
    private static final long WORKER_ID_BITS = 10L;

    // 序列掩码（111111111111B = 4095）
    private static final long SEQUENCE_MASK = 4095L;

    // workerId左边共12位（序列号）
    private static final long WORKER_ID_LEFT_SHIFT_BITS = 12L;

    // 时间戳左边共22位（序列号+workerId）
    private static final long TIMESTAMP_LEFT_SHIFT_BITS = 22L;

    // 工作机器ID最大值1024
    private static final long WORKER_ID_MAX_VALUE = 1024L;

    private long workerId;

    private long sequence;

    private long lastTime;

    private long currentId;

    {
        long workerId = ApplicationUtils.getProperty("nacos.core.snowflake.worker-id", Integer.class, -1);

        if (workerId != -1) {
            this.workerId = workerId;
        } else {
            InetAddress address;
            try {
                address = InetAddress.getLocalHost();
            } catch (final UnknownHostException e) {
                throw new IllegalStateException("Cannot get LocalHost InetAddress, please check your network!", e);
            }
            byte[] ipAddressByteArray = address.getAddress();
            this.workerId = (((ipAddressByteArray[ipAddressByteArray.length - 2] & 0B11) << Byte.SIZE) + (ipAddressByteArray[ipAddressByteArray.length - 1] & 0xFF));
        }
    }

    @Override
    public void init() {
        initialize(workerId);
    }

    @Override
    public long currentId() {
        return currentId;
    }

    @Override
    public synchronized long nextId() {
        long currentMillis = System.currentTimeMillis();
        Preconditions.checkState(this.lastTime <= currentMillis, "Clock is moving backwards, last time is %d milliseconds, current time is %d milliseconds", new Object[]{this.lastTime, currentMillis});
        if (this.lastTime == currentMillis) {
            if (0L == (this.sequence = ++this.sequence & 4095L)) {
                currentMillis = this.waitUntilNextTime(currentMillis);
            }
        } else {
            this.sequence = 0L;
        }

        this.lastTime = currentMillis;
        //logger.debug("{}-{}-{}", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date(this.lastTime)), workerId, this.sequence);
        currentId = currentMillis - EPOCH << 22 | workerId << 12 | this.sequence;
        return currentId;
    }

    @Override
    public Map<Object, Object> info() {
        Map<Object, Object> info = new HashMap<>(4);
        info.put("currentId", currentId);
        info.put("workerId", workerId);
        return info;
    }

    public void initialize(long workerId) {
        if (workerId > WORKER_ID_MAX_VALUE || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0, current workId %d", WORKER_ID_MAX_VALUE, workerId));
        }
        this.workerId = workerId;
    }

    private long waitUntilNextTime(long lastTimestamp) {
        long time;
        time = System.currentTimeMillis();
        while (time <= lastTimestamp) {
            ;
            time = System.currentTimeMillis();
        }
        return time;
    }
}
