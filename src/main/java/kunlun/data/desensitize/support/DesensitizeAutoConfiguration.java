/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.desensitize.support;

import kunlun.data.desensitize.DesensitizeUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Data desensitizing auto configuration.
 * @author Kahle
 */
@Configuration
public class DesensitizeAutoConfiguration implements InitializingBean, DisposableBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        DesensitizeUtils.register("PhoneNumber", new PhoneNumberDesensitizer());
        DesensitizeUtils.register("WithPhoneNumber", new WithPhoneNumberDesensitizer());
        DesensitizeUtils.register("BankCardNumber", new BankCardNumberDesensitizer());
    }

    @Override
    public void destroy() throws Exception {
    }

}
