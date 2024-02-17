/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.validate;

import kunlun.data.validation.support.BankCardNumberLuhnValidator;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import org.junit.Test;

public class BankCardNumberLuhnValidatorTest {
    private static Logger log = LoggerFactory.getLogger(BankCardNumberLuhnValidatorTest.class);
    private static BankCardNumberLuhnValidator bankCardNumberLuhnValidator = new BankCardNumberLuhnValidator();

    @Test
    public void test1() {
        log.info("{}", bankCardNumberLuhnValidator.validate("6228482898203884775"));
        log.info("{}", bankCardNumberLuhnValidator.validate("6228482898203884777"));
        log.info("{}", bankCardNumberLuhnValidator.validate("6228480010594620212"));
        log.info("{}", bankCardNumberLuhnValidator.validate("6228480010594620213"));
        log.info("{}", bankCardNumberLuhnValidator.validate("9559980210373015416"));
        log.info("{}", bankCardNumberLuhnValidator.validate("9559980210373015417"));
    }

}
