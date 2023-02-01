package artoria.event.aspect;

import artoria.aspect.AbstractAspect;
import artoria.event.EventUtils;
import artoria.event.annotation.EventRecord;
import artoria.exception.ExceptionUtils;
import artoria.lang.Dict;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

@Deprecated // TODO: Deletable
@Aspect
public class EventRecordAspect extends AbstractAspect {
    private static final Logger log = LoggerFactory.getLogger(EventRecordAspect.class);

    @Override
    protected void handle(JoinPoint joinPoint, Long timeSpent, Object result, Throwable th) {
        try {
            Signature signature = joinPoint.getSignature();
            MethodSignature methodSign = (MethodSignature) signature;
            Method method = methodSign.getMethod();

            EventRecord eventRecord = method.getAnnotation(EventRecord.class);
            String eventCode = eventRecord.code();
            boolean input = eventRecord.input();
            boolean output = eventRecord.output();
            boolean record = eventRecord.record();

            List<Object> args = getArguments(joinPoint,
                    MultipartFile.class, HttpServletRequest.class, HttpServletResponse.class);


            Dict attrs = Dict.of()
                    .set("processTime", timeSpent);
            if (th != null) {
                attrs.set("errorMessage", ExceptionUtils.toString(th));
            }

            if (input) {
                attrs.set("input", args);
            }
            if (output) {
                attrs.set("output", result);
            }

            attrs.set("methodName", method.toString());

            if (record) {
                EventUtils.track(eventCode, null, null, attrs);
            }
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }
    }

    @Pointcut("@annotation(artoria.event.annotation.EventRecord)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 20220504 There is no calculation of processing time based on ThreadLocal.
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();

        handle(joinPoint, stopWatch.getLastTaskTimeMillis(), result, null);

        return result;
    }

    @AfterThrowing(pointcut = "pointcut()", throwing = "th")
    public void afterThrowing(JoinPoint joinPoint, Throwable th) {

        handle(joinPoint, null, null, th);
    }

}
