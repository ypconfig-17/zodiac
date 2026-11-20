package com.tcsl.zodiac.cilent.cilent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @className: BeatTask
 * @description:
 * @author: yang pan
 */
public class BeatHeartTask extends TimerTask {
    private Logger logger = LoggerFactory.getLogger(BeatHeartTask.class);
    private  String name = "heartbeat";
    private final ScheduledExecutorService scheduler;
    private final ThreadPoolExecutor executor;
    private final long timeoutMillis;
    private final AtomicLong delay;
    private final long maxDelay;
    private Runnable task;

    public BeatHeartTask(ScheduledExecutorService scheduler, ThreadPoolExecutor executor, long timeoutMillis,int expBackOffBound,Runnable task) {
        this.scheduler = scheduler;
        this.executor = executor;
        this.timeoutMillis = timeoutMillis;
        this.delay = new AtomicLong(timeoutMillis);
        this.maxDelay = timeoutMillis * expBackOffBound;
        this.task = task;
    }

    @Override
    public void run() {
        Future<?> future = null;
        try {
//            logger.info("心跳执行开始。。。");
            future = executor.submit(task);
            future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            delay.set(timeoutMillis);
        } catch (Throwable throwable) {
            long currentDelay = delay.get();
            long newDelay = Math.min(maxDelay, currentDelay + currentDelay / 2);
            delay.compareAndSet(currentDelay,newDelay);
        }finally {
            if (future != null){
                future.cancel(true);
            }

            if (!scheduler.isShutdown()){
                scheduler.schedule(this,delay.get(),TimeUnit.MILLISECONDS);
            }
        }
    }
}
