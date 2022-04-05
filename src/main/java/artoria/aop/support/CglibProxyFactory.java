package artoria.aop.support;

import artoria.aop.Interceptor;
import artoria.aop.ProxyFactory;
import artoria.util.Assert;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * The proxy factory implements by cglib.
 * @author Kahle
 */
public class CglibProxyFactory implements ProxyFactory {
    private static final Logger log = LoggerFactory.getLogger(CglibProxyFactory.class);

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
        public Object intercept(Object proxyObject, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

            return interceptor.intercept(proxyObject, method, args);
        }

    }

    @Override
    public Object getInstance(Class<?> originalClass, Interceptor interceptor) {
        Assert.notNull(originalClass, "Parameter \"originalClass\" must not null. ");
        Assert.notNull(interceptor, "Parameter \"interceptor\" must not null. ");
        MethodInterceptor methodInterceptor = new MethodInterceptorAdapter(interceptor);
        return Enhancer.create(originalClass, methodInterceptor);
    }

}
