package artoria.config;

import artoria.spring.ApplicationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Spring auto configuration.
 * @author Kahle
 */
@Configuration
public class SpringAutoConfiguration implements InitializingBean, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(SpringAutoConfiguration.class);
    private ApplicationContext applicationContext;

    @Autowired
    public SpringAutoConfiguration(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (applicationContext != null) {
            ApplicationContextUtils.setContext(this.applicationContext);
            log.info(">> The application context tools was initialized success. ");
        }
    }

    @Override
    public void destroy() throws Exception {
    }

}
