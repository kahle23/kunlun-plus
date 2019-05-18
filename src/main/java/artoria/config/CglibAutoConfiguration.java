package artoria.config;

import artoria.aop.CglibProxyFactory;
import artoria.aop.Enhancer;
import artoria.aop.ProxyFactory;
import artoria.aop.SpringCglibProxyFactory;
import artoria.beans.*;
import artoria.util.ClassLoaderUtils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Cglib auto configuration.
 * @author Kahle
 */
@Configuration
public class CglibAutoConfiguration implements InitializingBean, DisposableBean {
    private static final String SPRING_CGLIB_CLASS = "org.springframework.cglib.proxy.MethodInterceptor";
    private static final String CGLIB_CLASS = "net.sf.cglib.proxy.MethodInterceptor";
    private static Logger log = LoggerFactory.getLogger(CglibAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        Class<? extends BeanMap> mapType = null;
        ProxyFactory proxyFactory = null;
        BeanCopier beanCopier = null;
        if (ClassUtils.isPresent(SPRING_CGLIB_CLASS, classLoader)) {
            mapType = SpringCglibBeanMap.class;
            beanCopier = new SpringCglibBeanCopier();
            proxyFactory = new SpringCglibProxyFactory();
            log.info("The spring cglib was initialized success. ");
        }
        else if (ClassUtils.isPresent(CGLIB_CLASS, classLoader)) {
            mapType = CglibBeanMap.class;
            beanCopier = new CglibBeanCopier();
            proxyFactory = new CglibProxyFactory();
            log.info("The cglib was initialized success. ");
        }
        if (mapType != null) { BeanUtils.setMapType(mapType); }
        if (beanCopier != null) { BeanUtils.setBeanCopier(beanCopier); }
        if (proxyFactory != null) { Enhancer.setProxyFactory(proxyFactory); }
    }

    @Override
    public void destroy() throws Exception {
    }

}
