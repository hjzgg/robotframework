package com.wmh.robotframework.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LoggerQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerQueue.class);

    //队列大小
    public static final int QUEUE_MAX_SIZE = 500;

    private static LoggerQueue collectMessageQueue = new LoggerQueue();

    //阻塞队列
    private BlockingQueue<LoggerMessage> blockingQueue = new LinkedBlockingQueue<>(QUEUE_MAX_SIZE);

    private LoggerQueue() {
    }

    public static LoggerQueue getInstance() {
        return collectMessageQueue;
    }

    /**
     * 消息入队
     *
     * @param log
     * @return
     */
    public boolean push(LoggerMessage log) {
        return this.blockingQueue.add(log);//队列满了就抛出异常，不阻塞
    }

    /**
     * 消息出队
     *
     * @return
     */
    public LoggerMessage poll() {
        LoggerMessage result = null;
        try {
            result = this.blockingQueue.take();
        } catch (InterruptedException e) {
            LOGGER.error("消息出队异常", e);
        }
        return result;
    }

    public boolean isEmpty() {
        return this.blockingQueue.isEmpty();
    }
}