/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.bean.support;

import kunlun.data.bean.BeanCopier;
import kunlun.data.bean.BeanMapFactory;
import kunlun.data.bean.BeanUtils;
import kunlun.util.ClassLoaderUtils;
import kunlun.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * The bean tools auto-configuration.
 * @author Kahle
 */
@Configuration
public class BeanToolsAutoConfiguration implements InitializingBean, DisposableBean {
    private static final String APACHE_BEAN_TOOLS_CLASS = "org.apache.commons.beanutils.BeanUtils";
    private static final String SPRING_BEAN_TOOLS_CLASS = "org.springframework.beans.BeanUtils";
    private static final String SPRING_CGLIB_CLASS = "org.springframework.cglib.proxy.MethodInterceptor";
    private static final String CGLIB_CLASS = "net.sf.cglib.proxy.MethodInterceptor";
    private static final Logger log = LoggerFactory.getLogger(BeanToolsAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        BeanMapFactory beanMapFactory = null;
        BeanCopier beanCopier = null;
        if (ClassUtils.isPresent(SPRING_CGLIB_CLASS, classLoader)) {
            beanMapFactory = new SpringCglibBeanMapFactory();
            beanCopier = new SpringCglibBeanCopier();
            log.info("The spring cglib bean tools was initialized success. ");
        }
        else if (ClassUtils.isPresent(CGLIB_CLASS, classLoader)) {
            beanMapFactory = new CglibBeanMapFactory();
            beanCopier = new CglibBeanCopier();
            log.info("The cglib bean tools was initialized success. ");
        }
        else if (ClassUtils.isPresent(SPRING_BEAN_TOOLS_CLASS, classLoader)) {
            beanMapFactory = new SimpleBeanMapFactory();
            beanCopier = new SpringBeanCopier();
            log.info("The spring bean tools was initialized success. ");
        }
        else if (ClassUtils.isPresent(APACHE_BEAN_TOOLS_CLASS, classLoader)) {
            beanMapFactory = new SimpleBeanMapFactory();
            beanCopier = new ApacheBeanCopier();
            log.info("The apache bean tools was initialized success. ");
        }
        else {
            // Do nothing.
        }
        if (beanMapFactory != null) { BeanUtils.setBeanMapFactory(beanMapFactory); }
        if (beanCopier != null) { BeanUtils.setBeanCopier(beanCopier); }
    }

    @Override
    public void destroy() throws Exception {

    }

}
