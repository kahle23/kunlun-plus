/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.cache.support;

import kunlun.data.ReferenceType;
import kunlun.util.ThreadUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SpringSimpleCacheTest {
    private static final Logger log = LoggerFactory.getLogger(SpringSimpleCacheTest.class);

    @Ignore
    @Test
    public void testConcurrentModificationException() {
        SimpleCacheConfig cacheConfig = new SimpleCacheConfig();
        cacheConfig.setReferenceType(ReferenceType.SOFT);
        final SpringSimpleCache cache = new SpringSimpleCache(cacheConfig);
//        cache.setRecordLog(true);
        long start = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000000; i++) {
                    cache.put(i, i, 100, TimeUnit.MILLISECONDS);
                    ThreadUtils.sleepQuietly(1);
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadUtils.sleepQuietly(100);
                for (int i = 0; i < 1000000; i++) {
                    cache.get(i);
                }
            }
        }).start();
        for (int i = 0; i < 1000000; i++) {
            cache.put(">> "+i, i, 10, TimeUnit.MILLISECONDS);
        }
        long end = System.currentTimeMillis();
        log.info("Time spent: {}", (end - start) / 1000);
    }

}
