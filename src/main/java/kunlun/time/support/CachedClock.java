/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.time.support;

import kunlun.thread.SimpleThreadFactory;
import kunlun.time.SimpleClock;
import kunlun.util.Assert;
import kunlun.util.ShutdownHookUtils;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import static java.lang.Boolean.TRUE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Numbers.ZERO;

/**
 * Use a separate thread to get the timestamp of the current time and cache it.
 * @see System#currentTimeMillis()
 * @author Kahle
 */
public class CachedClock extends SimpleClock {
    private static final String THREAD_NAME = "cached-clock-executor";

    public static long currentTimeMillis() {

        return Holder.INSTANCE.getTime();
    }

    public static SimpleClock getInstance() {

        return Holder.INSTANCE;
    }

    private static class Holder {

        private static final CachedClock INSTANCE = new CachedClock(ONE);
    }

    private volatile long nowTime;

    private CachedClock(long period) {
        Assert.isTrue(period > ZERO, "Parameter \"period\" must greater than 0. ");
        ThreadFactory threadFactory = new SimpleThreadFactory(THREAD_NAME, TRUE);
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(ONE, threadFactory);
        threadPool.scheduleAtFixedRate(new TimeUpdater(), period, period, MILLISECONDS);
        ShutdownHookUtils.addExecutorService(threadPool);
        this.nowTime = System.currentTimeMillis();
    }

    @Override
    public Long getTime() {

        return nowTime;
    }

    private class TimeUpdater implements Runnable {
        @Override
        public void run() {
            // Update time.
            nowTime = System.currentTimeMillis();
        }
    }

}
