/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.context.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The abstract servlet context.
 * @author Kahle
 */
public abstract class AbstractServletContext
        extends AbstractPropertyContext implements ServletContext, ThreadLocalContext {
    private static final String RESPONSE_NAME = "response";
    private static final String REQUEST_NAME  = "request";
    private static final String RESPONSE_BODY = "response-body";
    private static final String REQUEST_BODY  = "request-body";

    @Override
    public void destroy(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        // The cleanup of ThreadLocal is already invoked externally.
        // So there's no need to clean up ThreadLocal here.
        /* -------- */
        removeProperty(REQUEST_NAME);
        removeProperty(RESPONSE_NAME);
    }

    @Override
    public HttpServletResponse getResponse() {

        return getProperty(RESPONSE_NAME, HttpServletResponse.class);
    }

    public void setResponse(HttpServletResponse response) {

        setProperty(RESPONSE_NAME, response);
    }

    @Override
    public HttpServletRequest getRequest() {

        return getProperty(REQUEST_NAME, HttpServletRequest.class);
    }

    public void setRequest(HttpServletRequest request) {

        setProperty(REQUEST_NAME, request);
    }

    @Override
    public Object getResponseBody() {

        return getProperty(RESPONSE_BODY, Object.class);
    }

    @Override
    public void setResponseBody(Object responseBody) {

        setProperty(RESPONSE_BODY, responseBody);
    }

    @Override
    public Object getRequestBody() {

        return getProperty(REQUEST_BODY, Object.class);
    }

    @Override
    public void setRequestBody(Object requestBody) {

        setProperty(REQUEST_BODY, requestBody);
    }

}
