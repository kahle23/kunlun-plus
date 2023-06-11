package artoria.aop.aspectj;

import artoria.util.ArrayUtils;
import artoria.util.Assert;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The abstract aspect base on aspectj.
 * @author Kahle
 */
public abstract class AbstractAspect {
    private static final Logger log = LoggerFactory.getLogger(AbstractAspect.class);

    protected Object[] getArguments(JoinPoint joinPoint, Class<?>... ignoreTypes) {
        Assert.notNull(joinPoint, "Parameter \"joinPoint\" must not null. ");
        Object[] args = joinPoint.getArgs();
        if (ArrayUtils.isEmpty(ignoreTypes)) {
            return args;
        }
        if (ArrayUtils.isEmpty(args)) {
            return new Object[]{};
        }
        List<Object> result = new ArrayList<Object>();
        for (Object arg : args) {
            boolean isIgnore = false;
            for (Class<?> ignoreType : ignoreTypes) {
                // if null, is not instance, no NPE.
                if (ignoreType.isInstance(arg)) {
                    isIgnore = true;
                    break;
                }
            }
            if (!isIgnore) { result.add(arg); }
        }
        return result.toArray();
    }

    protected Method getMethod(JoinPoint joinPoint) {
        Assert.notNull(joinPoint, "Parameter \"joinPoint\" must not null. ");
        Signature signature = joinPoint.getSignature();
        return ((MethodSignature) signature).getMethod();
    }


    /*@Aspect
    public class TestAspect {
        @Pointcut("@annotation(test.TestAnnotation)")
        public void pointcut() {
        }

        @Around("pointcut()")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
            return joinPoint.proceed();
        }

        @AfterThrowing(pointcut = "pointcut()", throwing = "th")
        public void afterThrowing(JoinPoint joinPoint, Throwable th) {
        }
    }*/

}
