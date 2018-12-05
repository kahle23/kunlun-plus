package artoria.aop;

import artoria.util.Assert;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Proxy factory implements by spring cglib.
 * @author Kahle
 */
public class SpringCglibProxyFactory implements ProxyFactory {

    private static class MethodInterceptorAdapter implements MethodInterceptor {
        private Interceptor interceptor;

        public Interceptor getInterceptor() {

            return this.interceptor;
        }

        public void setInterceptor(Interceptor interceptor) {

            this.interceptor = interceptor;
        }

        public MethodInterceptorAdapter(Interceptor interceptor) {

            this.interceptor = interceptor;
        }

        @Override
        public Object intercept(Object proxyObject, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

            return this.interceptor.intercept(proxyObject, method, args);
        }

    }

    @Override
    public Object getInstance(Class<?> originalClass, Interceptor interceptor) {
        Assert.notNull(originalClass, "Parameter \"targetClass\" must not null. ");
        Assert.notNull(interceptor, "Parameter \"interceptor\" must not null. ");
        MethodInterceptor methodInterceptor = new MethodInterceptorAdapter(interceptor);
        return Enhancer.create(originalClass, methodInterceptor);
    }

}
