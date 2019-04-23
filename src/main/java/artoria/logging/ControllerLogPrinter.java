package artoria.logging;

import artoria.servlet.RequestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static artoria.common.Constants.COLON;
import static artoria.common.Constants.EMPTY_STRING;

/**
 * Controller log printer.
 * @author Kahle
 */
//@Aspect
//@Component
@Deprecated
public class ControllerLogPrinter {
    private static Logger log = LoggerFactory.getLogger(ControllerLogPrinter.class);
    private LoggingProperties loggerProperties;

    @Autowired
    public ControllerLogPrinter(LoggingProperties loggerProperties) {

        this.loggerProperties = loggerProperties;
    }

    @Pointcut("(execution(* *..controller..*(..))) || " +
            "(execution(* *..controllers..*(..))) || (execution(* *..*Controller.*(..)))")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if (!loggerProperties.getPrintControllerLog()) {
            return proceedingJoinPoint.proceed();
        }
        RequestAttributes reqAttr = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attr = (ServletRequestAttributes) reqAttr;
        String message = EMPTY_STRING;
        if (attr != null) {
            HttpServletRequest request = attr.getRequest();
            String proxyAddr = request.getRemoteAddr();
            String realAddr = RequestUtils.getRemoteAddr(request);
            boolean noProxy = proxyAddr != null && proxyAddr.equalsIgnoreCase(realAddr);
            proxyAddr = proxyAddr + COLON + request.getRemotePort();
            String method = request.getMethod();
            method = method != null ? method.toLowerCase() : EMPTY_STRING;
            message += "client"
                    + (noProxy ? "[\"" + proxyAddr + "\"]"
                        : "[proxy:\"" + proxyAddr + "\", real:\"" + realAddr + "\"]")
                    + " "
                    + method
                    + " \""
                    + request.getRequestURL().toString()
                    + "\" and ";
        }
        if (proceedingJoinPoint == null) { return null; }
        Object result;
        long beginTime = System.currentTimeMillis();
        try {
            result = proceedingJoinPoint.proceed();
        }
        catch (Throwable t) {
            long endTime = System.currentTimeMillis();
            message += "time expend "
                    + (endTime - beginTime)
                    + "ms and an error has occurred. ";
            log.info(message);
            throw t;
        }
        long endTime = System.currentTimeMillis();
        message += "time expend "
                + (endTime - beginTime)
                + "ms and result is \""
                + (result != null
                    ? result.getClass().getName() + "@" + Integer.toHexString(result.hashCode())
                    : null)
                + "\". ";
        log.info(message);
        return result;
    }

}
