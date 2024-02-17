/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.desensitize.support;

import kunlun.data.desensitize.Desensitizer;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import org.junit.Test;

public class BankCardNumberDesensitizerTest {
    private static final Logger log = LoggerFactory.getLogger(BankCardNumberDesensitizerTest.class);
    private static final Desensitizer bankCardNumberMasker = new BankCardNumberDesensitizer();

    @Test
    public void test1() {
        log.info("{}", bankCardNumberMasker.desensitize("6228482898203884775"));
        log.info("{}", bankCardNumberMasker.desensitize("6228480010594620212"));
        log.info("{}", bankCardNumberMasker.desensitize("9559980210373015416"));
    }

}
