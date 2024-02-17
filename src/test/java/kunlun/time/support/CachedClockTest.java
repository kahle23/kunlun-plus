/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.time.support;

import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import kunlun.time.DateUtils;
import kunlun.time.SimpleClock;
import org.junit.Test;

public class CachedClockTest {
    private static Logger log = LoggerFactory.getLogger(CachedClockTest.class);

    @Test
    public void test1() {
        SimpleClock clock = CachedClock.getInstance();
        log.info("{}", DateUtils.format(clock.getTime()));
        log.info("{}", DateUtils.format(clock.getTime()));
        log.info("{}", DateUtils.format(clock.getTime()));
        log.info("{}", DateUtils.format(clock.getTime()));
        log.info("{}", DateUtils.format(clock.getTime()));
    }

}
