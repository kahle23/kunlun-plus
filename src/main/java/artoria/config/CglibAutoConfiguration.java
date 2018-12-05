package artoria.config;

import artoria.aop.Enhancer;
import artoria.aop.ProxyFactory;
import artoria.beans.BeanCopier;
import artoria.beans.BeanMap;
import artoria.beans.BeanUtils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import artoria.aop.CglibProxyFactory;
import artoria.aop.SpringCglibProxyFactory;
import artoria.beans.CglibBeanCopier;
import artoria.beans.CglibBeanMap;
import artoria.beans.SpringCglibBeanCopier;
import artoria.beans.SpringCglibBeanMap;
import artoria.spring.InitializingDisposableBean;

/**
 * Cglib auto configuration.
 * @author Kahle
 */
@Configuration
public class CglibAutoConfiguration implements InitializingDisposableBean {
    private static final String SPRING_CGLIB_CLASS = "org.springframework.cglib.proxy.MethodInterceptor";
    private static final String CGLIB_CLASS = "net.sf.cglib.proxy.MethodInterceptor";
    private static Logger log = LoggerFactory.getLogger(CglibAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        Class<? extends BeanMap> beanMapClass = null;
        ProxyFactory proxyFactory = null;
        BeanCopier beanCopier = null;
        if (ClassUtils.isPresent(SPRING_CGLIB_CLASS, classLoader)) {
            beanMapClass = SpringCglibBeanMap.class;
            proxyFactory = new SpringCglibProxyFactory();
            beanCopier = new SpringCglibBeanCopier();
        }
        else if (ClassUtils.isPresent(CGLIB_CLASS, classLoader)) {
            beanMapClass = CglibBeanMap.class;
            proxyFactory = new CglibProxyFactory();
            beanCopier = new CglibBeanCopier();
        }
        if (beanMapClass != null) { BeanUtils.setBeanMapClass(beanMapClass); }
        if (proxyFactory != null) { Enhancer.setProxyFactory(proxyFactory); }
        if (beanCopier != null) { BeanUtils.setBeanCopier(beanCopier); }
    }

    @Override
    public void destroy() throws Exception {
    }

}
