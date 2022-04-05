package artoria.aop;

import artoria.aop.support.CglibProxyFactory;
import artoria.aop.support.SpringCglibProxyFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class EnhancerTest {
    private static final Logger log = LoggerFactory.getLogger(EnhancerTest.class);
    private final String name = "zhangsan";

    public static class TestInterceptor implements Interceptor {
        private final Object proxiedObject;

        public TestInterceptor(Object proxiedObject) {

            this.proxiedObject = proxiedObject;
        }

        @Override
        public Object intercept(Object proxyObject, Method method, Object[] args) throws Throwable {
            log.info("Proxy object's class is " + proxyObject.getClass().getName());
            log.info("Hello, this is intercept. ");
            return method.invoke(this.proxiedObject, args);
        }
    }

    @Test
    public void testCglibEnhancer() {
        Enhancer.setProxyFactory(new CglibProxyFactory());
        RealSubject subject = new RealSubject();
        TestInterceptor interceptor = new TestInterceptor(subject);
        // RealSubject subjectProxy = (RealSubject) Enhancer.enhance(subject, interceptor);
        Subject subjectProxy = (Subject) Enhancer.enhance(Subject.class, interceptor);
        log.info(subjectProxy.sayHello(name));
        log.info(subjectProxy.sayGoodbye(name));
    }

    @Test
    public void testSpringCglibEnhancer() {
        Enhancer.setProxyFactory(new SpringCglibProxyFactory());
        RealSubject subject = new RealSubject();
        TestInterceptor interceptor = new TestInterceptor(subject);
        // RealSubject subjectProxy = (RealSubject) Enhancer.enhance(subject, interceptor);
        Subject subjectProxy = (Subject) Enhancer.enhance(Subject.class, interceptor);
        log.info(subjectProxy.sayHello(name));
        log.info(subjectProxy.sayGoodbye(name));
    }

}
