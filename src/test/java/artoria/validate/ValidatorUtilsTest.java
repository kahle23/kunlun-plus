package artoria.validate;

import artoria.data.validation.support.IsNumericValidator;
import artoria.data.validation.support.RegexValidator;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

public class ValidatorUtilsTest {
    private static Logger log = LoggerFactory.getLogger(ValidatorUtilsTest.class);

    @Test
    public void testNumeric() {
        ValidatorUtils.register("numeric", new IsNumericValidator());
        log.info("{}", ValidatorUtils.validate("numeric", "888.666"));
        log.info("{}", ValidatorUtils.validate("numeric", "-888.666"));
        log.info("{}", ValidatorUtils.validate("numeric", "+888.666"));
        log.info("{}", ValidatorUtils.validate("numeric", "888.666w"));
        log.info("{}", ValidatorUtils.validate("numeric", "hello, world! "));
    }

    @Test
    public void testEmail() {
        String emailRegex = "^[0-9A-Za-z][\\.-_0-9A-Za-z]*@[0-9A-Za-z]+(?:\\.[0-9A-Za-z]+)+$";
        ValidatorUtils.register("regex:email", new RegexValidator(emailRegex));
        log.info("{}", ValidatorUtils.validate("regex:email", "hello@email.com"));
        log.info("{}", ValidatorUtils.validate("regex:email", "hello@vip.email.com"));
        log.info("{}", ValidatorUtils.validate("regex:email", "$hello@email.com"));
        log.info("{}", ValidatorUtils.validate("regex:email", "hello@email"));
        log.info("{}", ValidatorUtils.validate("regex:email", "hello@.com"));
    }

    @Test
    public void testPhoneNumber() {
//        String phoneNumberRegex = "^1[3|4|5|6|7|8|9]\\d{9}$";
        String phoneNumberRegex = "^1\\d{10}$";
        ValidatorUtils.register("regex:phone_number", new RegexValidator(phoneNumberRegex));
        log.info("{}", ValidatorUtils.validate("regex:phone_number", "12000000000"));
        log.info("{}", ValidatorUtils.validate("regex:phone_number", "18000000000"));
        log.info("{}", ValidatorUtils.validate("regex:phone_number", "19999999999"));
    }

}
