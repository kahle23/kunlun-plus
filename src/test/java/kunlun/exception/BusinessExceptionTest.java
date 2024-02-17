/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.exception;

import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import org.junit.Test;

import static kunlun.exception.SystemErrorCode.PASSWORD_IS_REQUIRED;
import static kunlun.exception.SystemErrorCode.USERNAME_IS_REQUIRED;

public class BusinessExceptionTest {
    private static final Logger log = LoggerFactory.getLogger(BusinessExceptionTest.class);

    @Test
    public void testCreate() {
        try {
            throw new BusinessException(USERNAME_IS_REQUIRED);
        }
        catch (BusinessException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testWrap1() {
        try {
            this.throwException1();
        }
        catch (BusinessException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void testWrap2() {
        try {
            this.throwException2();
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void throwException1() {
        try {
            throw new BusinessException(PASSWORD_IS_REQUIRED);
        }
        catch (BusinessException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    private void throwException2() {
        try {
            throw new BusinessException("[BusinessException]: Just Test...");
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
