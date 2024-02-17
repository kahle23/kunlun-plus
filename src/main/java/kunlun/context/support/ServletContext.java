/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.context.support;

import kunlun.context.support.spring.ContextSupportRequestBodyAdvice;
import kunlun.context.support.spring.ContextSupportResponseBodyAdvice;
import kunlun.core.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The servlet context.
 * @author Kahle
 */
public interface ServletContext extends Context {

    /**
     * When the user request end, the internal holding of the context object is destroyed.
     * @param request The current HTTP request
     * @param response The current HTTP response
     * @param ex The exception thrown on handler execution, if any
     */
    void destroy(HttpServletRequest request, HttpServletResponse response, Exception ex);

    /**
     * Initialize the internal holding of the context object when the user request in.
     * @param request The current HTTP request
     * @param response The current HTTP response
     */
    void init(HttpServletRequest request, HttpServletResponse response);

    /**
     * Get the current http servlet response.
     * @return The current http servlet response
     */
    HttpServletResponse getResponse();

    /**
     * Get the current http servlet request.
     * @return The current http servlet request
     */
    HttpServletRequest getRequest();

    /**
     * Get the http response body.
     * @return The http response body
     */
    Object getResponseBody();

    /**
     * Set the http response body.
     * @param responseBody The http response body
     * @see org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
     * @see ContextSupportResponseBodyAdvice
     */
    void setResponseBody(Object responseBody);

    /**
     * Get the http request body.
     * @return The http request body
     */
    Object getRequestBody();

    /**
     * Set the http request body.
     * @param requestBody The http request body
     * @see org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
     * @see ContextSupportRequestBodyAdvice
     */
    void setRequestBody(Object requestBody);

}
