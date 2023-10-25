package artoria.data.desensitize.support;

import artoria.data.desensitize.DesensitizeUtils;
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
