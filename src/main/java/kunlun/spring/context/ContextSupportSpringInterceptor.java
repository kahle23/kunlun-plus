/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.context;

import kunlun.bean.BeanHolder;
import kunlun.context.support.ServletContext;
import kunlun.context.support.ThreadLocalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Support for context capability based on spring interceptor.
 * @author Kahle
 * @see org.springframework.web.servlet.HandlerInterceptor
 */
public class ContextSupportSpringInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(ContextSupportSpringInterceptor.class);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        try {
            Map<String, ServletContext> beans = BeanHolder.getBeans(ServletContext.class);
            if (beans == null || beans.isEmpty()) { return true; }
            for (Map.Entry<String, ServletContext> entry : beans.entrySet()) {
                ServletContext context = entry.getValue();
                context.init(request, response);
            }
            return true;
        }
        catch (Exception e) {
            try {
                // If an error occurs, the request ends here.
                // So need to finish the servlet context.
                destroyContext(request, response, e);
            }
            catch (Exception e1) {
                // The exception that happens here, it can't be thrown out.
                log.error("An error occurred while destroying context. ", e1);
            }
            finally {
                // It is necessary to free up resources here.
                resetThreadLocals();
            }
            throw e;
        }
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) throws Exception {
        try {
            destroyContext(request, response, ex);
        }
        finally {
            resetThreadLocals();
        }
    }

    /**
     * Call the "destroy" method of all ServletContext.
     * @param request The current HTTP request
     * @param response The current HTTP response
     * @param ex The exception thrown on handler execution, if any
     */
    protected void destroyContext(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        Map<String, ServletContext> beans = BeanHolder.getBeans(ServletContext.class);
        if (beans == null || beans.isEmpty()) { return; }
        for (Map.Entry<String, ServletContext> entry : beans.entrySet()) {
            ServletContext context = entry.getValue();
            context.destroy(request, response, ex);
        }
    }

    /**
     * Call the "resetThreadLocals" method of all ThreadLocalContext.
     */
    protected void resetThreadLocals() {
        Map<String, ThreadLocalContext> beans = BeanHolder.getBeans(ThreadLocalContext.class);
        if (beans == null || beans.isEmpty()) { return; }
        for (Map.Entry<String, ThreadLocalContext> entry : beans.entrySet()) {
            // There should be no exceptions to this method.
            // If there is an exception, throw it out directly.
            ThreadLocalContext context = entry.getValue();
            context.resetThreadLocals();
        }
    }

}
