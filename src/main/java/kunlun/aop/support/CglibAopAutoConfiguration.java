/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.aop.support;

import kunlun.aop.ProxyHandler;
import kunlun.aop.ProxyUtils;
import kunlun.common.constant.Words;
import kunlun.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * The cglib aop auto configuration.
 * @author Kahle
 */
@Configuration
public class CglibAopAutoConfiguration implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(CglibAopAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ProxyHandler proxyHandler;
        if (ClassUtils.isPresent("net.sf.cglib.proxy.MethodInterceptor")) {
            ProxyUtils.registerHandler("cglib", proxyHandler = new CglibProxyHandler());
            ProxyUtils.registerHandler(Words.DEFAULT, proxyHandler);
            log.info("The cglib proxy handler was initialized success. ");
        }
        if (ClassUtils.isPresent("org.springframework.cglib.proxy.MethodInterceptor")) {
            ProxyUtils.registerHandler("spring-cglib", proxyHandler = new SpringCglibProxyHandler());
            ProxyUtils.registerHandler(Words.DEFAULT, proxyHandler);
            log.info("The spring cglib proxy factory was initialized success. ");
        }
    }

}
