package artoria.logging;

import artoria.servlet.RequestUtils;
import artoria.util.ThreadLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static artoria.common.Constants.COLON;
import static artoria.common.Constants.EMPTY_STRING;

/**
 * Controller log print interceptor.
 * @author Kahle
 */
public class ControllerLogInterceptor extends HandlerInterceptorAdapter {
    private static Logger log = LoggerFactory.getLogger(ControllerLogInterceptor.class);
    private static final String BEGIN_TIME_KEY = "BEGIN_TIME";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ThreadLocalUtils.setValue(BEGIN_TIME_KEY, System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long endTime = System.nanoTime();
        Long beginTime = (Long) ThreadLocalUtils.getValue(BEGIN_TIME_KEY);
        ThreadLocalUtils.remove(BEGIN_TIME_KEY);
        if (beginTime == null || beginTime <= 0) { return; }
        long expendTime = (endTime - beginTime) / 1000000;
        String proxyAddr = request.getRemoteAddr();
        String realAddr = RequestUtils.getRemoteAddr(request);
        boolean noProxy = proxyAddr != null && proxyAddr.equalsIgnoreCase(realAddr);
        proxyAddr = proxyAddr + COLON + request.getRemotePort();
        String method = request.getMethod();
        method = method != null ? method.toLowerCase() : EMPTY_STRING;
        String message = "client"
                + (noProxy ? "[\"" + proxyAddr + "\"]"
                : "[proxy:\"" + proxyAddr + "\", real:\"" + realAddr + "\"]")
                + " "
                + method
                + " \""
                + request.getRequestURL().toString()
                + "\" and ";
        if (ex != null) {
            message += "time expend " + expendTime
                    + "ms and an error has occurred. ";
            log.info(message);
            throw ex;
        }
        else {
            message += "time expend " + expendTime + "ms. ";
            log.info(message);
        }
    }

}
