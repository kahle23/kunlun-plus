/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.logging.support;

import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

public class Log4jTest {

    @Before
    public void init() {

        LoggerFactory.setLoggerProvider(new Log4jProvider());
    }

    @Test
    public void test1() {
        Logger log = LoggerFactory.getLogger(Log4jTest.class);
        log.info("Hello, World! ");
        log.info("Hello, World! ");
        log.error("Hello, World! ");
        log.error("Hello, World! ");
    }

}
