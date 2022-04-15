package artoria.spring;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The request context holder tools.
 * @author Kahle
 * @see org.springframework.web.context.request.RequestContextHolder
 * @see org.springframework.web.context.request.RequestAttributes
 */
public class RequestContextUtils {

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) { return null; }
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        return attributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) { return null; }
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        return attributes.getResponse();
    }

}
