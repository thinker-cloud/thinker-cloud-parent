package com.thinker.cloud.rocketmq.trace.dispatch;

/**
 * 数据编码和发送模块
 */
public abstract class AsyncAppender {
    /**
     *编码数据上下文到缓冲区
     * @param context 上下文
     */
    public abstract void append(Object context);

    /**
     * 实际写数据操作
     */
    public abstract void flush();
}
