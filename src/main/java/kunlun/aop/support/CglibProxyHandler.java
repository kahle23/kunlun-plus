/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.aop.support;

import kunlun.aop.AbstractProxyHandler;
import kunlun.aop.Interceptor;
import kunlun.util.Assert;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * The proxy handler implements by cglib.
 * @author Kahle
 */
public class CglibProxyHandler extends AbstractProxyHandler {
    private static final Logger log = LoggerFactory.getLogger(CglibProxyHandler.class);

    /**
     * The cglib method interceptor adapter.
     * @author Kahle
     */
    private static class MethodInterceptorAdapter implements MethodInterceptor {
        private final Interceptor interceptor;

        public MethodInterceptorAdapter(Interceptor interceptor) {
            Assert.notNull(interceptor, "Parameter \"interceptor\" must not null. ");
            this.interceptor = interceptor;
        }

        @Override
        public Object intercept(Object proxyObject, Method method,
                                Object[] args, MethodProxy methodProxy) throws Throwable {

            return interceptor.intercept(proxyObject, method, args);
        }
    }

    @Override
    public Object proxy(Class<?> originalClass, Interceptor interceptor) {
        Assert.notNull(originalClass, "Parameter \"originalClass\" must not null. ");
        Assert.notNull(interceptor, "Parameter \"interceptor\" must not null. ");
        MethodInterceptor methodInterceptor = new MethodInterceptorAdapter(interceptor);
        return Enhancer.create(originalClass, methodInterceptor);
    }

}
