package artoria.config;

import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import artoria.mail.EmailClient;

/**
 * Email auto configuration.
 * @author Kahle
 */
@Configuration
public class EmailAutoConfiguration implements InitializingBean, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(EmailAutoConfiguration.class);
    private static final String MAIL_CLASS = "javax.mail.Message";

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void destroy() throws Exception {
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailClient emailClient() {
        ClassLoader loader = ClassUtils.getDefaultClassLoader();
        if (!ClassUtils.isPresent(MAIL_CLASS, loader)) {
            log.error("Can not find jar \"javax.mail\". ");
            return null;
        }
        return new EmailClient();
    }

}
