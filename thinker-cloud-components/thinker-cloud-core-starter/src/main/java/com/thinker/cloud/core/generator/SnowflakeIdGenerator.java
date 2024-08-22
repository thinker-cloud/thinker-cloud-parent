package com.thinker.cloud.core.generator;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 雪花id生产器
 *
 * @author admin
 */
@Slf4j
@SuppressWarnings("all")
public class SnowflakeIdGenerator {

    /**
     * 开始时间戳（2023-09-01 00:00）
     */
    private final long twepoch = 1725120000000L;

    /**
     * 机器ID所占的位数
     */
    private final long workerIdBits = 5L;

    /**
     * 数据标识ID所占的位数
     */
    private final long datacenterIdBits = 5L;

    /**
     * 支持的最大机器ID，结果是31（二进制：11111）
     */
    private final long maxWorkerId = ~(-1L << workerIdBits);

    /**
     * 支持的最大数据标识ID，结果是31（二进制：11111）
     */
    private final long maxDataCenterId = ~(-1L << datacenterIdBits);

    /**
     * 序列在ID中占的位数
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据标识ID向左移17位（12+5）
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间戳向左移22位（5+5+12）
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 生成序列的掩码，这里为4095（二进制：111111111111）
     */
    private final long sequenceMask = ~(-1L << sequenceBits);

    /**
     * 工作机器ID（0~31）
     */
    private long workerId;

    /**
     * 数据中心ID（0~31）
     */
    private long dataCenterId;

    /**
     * 毫秒内序列（0~4095）
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = -1L;


    private static final SnowflakeIdGenerator ID_GENERATOR;

    static {
        Random random = new Random();
        long workerId = Long.getLong("id-worker", random.nextInt(31));
        long dataCenterId = Long.getLong("id-datacenter", random.nextInt(31));
        ID_GENERATOR = new SnowflakeIdGenerator(workerId, dataCenterId);
    }

    public static SnowflakeIdGenerator getInstance() {
        return ID_GENERATOR;
    }

    /**
     * 构造器
     *
     * @param workerId     workerId
     * @param dataCenterId dataCenterId
     */
    public SnowflakeIdGenerator(long workerId, long dataCenterId) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        log.info("worker starting. timestamp left shift {}, datacenter id bits {}, worker id bits {}, sequence bits {}, workerid {}", timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId);
    }

    /**
     * Generate a ID
     *
     * @return long
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            log.error("clock is moving backwards.  Rejecting requests until {}.", lastTimestamp);
            throw new UnsupportedOperationException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (dataCenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳
     *
     * @return long
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
