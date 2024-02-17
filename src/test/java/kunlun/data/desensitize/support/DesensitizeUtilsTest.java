/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.desensitize.support;

import kunlun.data.desensitize.DesensitizeUtils;
import kunlun.exception.ExceptionUtils;
import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import org.junit.Test;

public class DesensitizeUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(DesensitizeUtilsTest.class);

    static {
        try {
            new DesensitizeAutoConfiguration().afterPropertiesSet();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Test
    public void testPhoneNumber() {
        log.info("{}", DesensitizeUtils.desensitize("PhoneNumber", "13600006666"));
        log.info("{}", DesensitizeUtils.desensitize("PhoneNumber", "13888889999"));
    }

    @Test
    public void testWithPhoneNumber() {
        DesensitizeUtils.register("WithPhoneNumber", new WithPhoneNumberDesensitizer());
        log.info("{}", DesensitizeUtils.desensitize("WithPhoneNumber", "Hello13600006666Hel13888889999"));
        log.info("{}", DesensitizeUtils.desensitize("WithPhoneNumber", "1360000666613888889999"));
        log.info("{}", DesensitizeUtils.desensitize("WithPhoneNumber", "Hello1354385689476556"));
    }

}
