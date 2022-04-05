package artoria.aop.support;

import artoria.aop.Enhancer;
import artoria.aop.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The spring cglib aop auto configuration.
 * @author Kahle
 */
@Configuration
@ConditionalOnClass({MethodInterceptor.class})
public class SpringCglibAopAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(SpringCglibAopAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public ProxyFactory proxyFactory() {
        ProxyFactory proxyFactory = new SpringCglibProxyFactory();
        Enhancer.setProxyFactory(proxyFactory);
        log.info("The spring cglib proxy factory was initialized success. ");
        return proxyFactory;
    }

}
