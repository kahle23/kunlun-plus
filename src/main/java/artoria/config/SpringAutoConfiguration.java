package artoria.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import artoria.spring.ApplicationContextUtils;
import artoria.spring.InitializingDisposableBean;

/**
 * Spring auto configuration.
 * @author Kahle
 */
@Configuration
public class SpringAutoConfiguration implements InitializingDisposableBean {
    private static Logger log = LoggerFactory.getLogger(SpringAutoConfiguration.class);
    private ApplicationContext applicationContext;

    @Autowired
    public SpringAutoConfiguration(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.applicationContext != null) {
            ApplicationContextUtils.setContext(this.applicationContext);
            log.info("Initialize \"ApplicationContextUtils\" success. ");
        }
    }

    @Override
    public void destroy() throws Exception {
    }

}
