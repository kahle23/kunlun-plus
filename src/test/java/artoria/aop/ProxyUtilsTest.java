package artoria.aop;

import artoria.aop.support.CglibProxyHandler;
import artoria.aop.support.SpringCglibProxyHandler;
import artoria.common.Constants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * The proxy tools Test.
 * @author Kahle
 */
public class ProxyUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(ProxyUtilsTest.class);
    private final String name = "zhangsan";

    public static class TestInterceptor extends AbstractInterceptor<RealSubject> {
        public TestInterceptor(RealSubject originalObject) {

            super(originalObject);
        }
        @Override
        public Object intercept(Object proxyObject, Method method, Object[] args) throws Throwable {
            log.info("Proxy object's class is " + proxyObject.getClass().getName());
            log.info("Hello, this is intercept. ");
            return method.invoke(getOriginalObject(), args);
        }
    }

    @Test
    public void testCglibProxy() {
        ProxyUtils.registerHandler(Constants.DEFAULT, new CglibProxyHandler());
        RealSubject subject = new RealSubject();
        Subject subjectProxy = ProxyUtils.proxy(new TestInterceptor(subject));
        log.info(subjectProxy.sayHello(name));
        log.info(subjectProxy.sayGoodbye(name));
    }

    @Test
    public void testSpringCglibProxy() {
        ProxyUtils.registerHandler(Constants.DEFAULT, new SpringCglibProxyHandler());
        RealSubject subject = new RealSubject();
        Subject subjectProxy = ProxyUtils.proxy(new TestInterceptor(subject));
        log.info(subjectProxy.sayHello(name));
        log.info(subjectProxy.sayGoodbye(name));
    }

}
