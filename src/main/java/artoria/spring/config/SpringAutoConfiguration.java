package artoria.spring.config;

import artoria.spring.ApplicationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import static artoria.common.constant.Charsets.STR_DEFAULT_CHARSET;
import static artoria.common.constant.Numbers.SIXTEEN;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * The spring auto configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureOrder(HIGHEST_PRECEDENCE + SIXTEEN)
public class SpringAutoConfiguration implements ApplicationContextAware, InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(SpringAutoConfiguration.class);

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.setContext(applicationContext);
        log.info("The application context tools was initialized success. ");
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        log.info("The default charset for the current run environment is {}. ", STR_DEFAULT_CHARSET);
    }

    @Override
    public void destroy() throws Exception {

    }

}
