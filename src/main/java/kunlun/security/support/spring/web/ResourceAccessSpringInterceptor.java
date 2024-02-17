/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.security.support.spring.web;

import kunlun.core.handler.ResourceAccessPreHandler;
import kunlun.util.Assert;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Support for resource access pre handler based on spring interceptor.
 * @author Kahle
 */
public class ResourceAccessSpringInterceptor extends HandlerInterceptorAdapter {
    private static final String ACCESS_TYPE = "spring-servlet-interceptor";
    private static final String DEF_TOKEN_NAME = "authorization";
    private final ResourceAccessPreHandler accessPreHandler;
    private final String tokenName;

    public ResourceAccessSpringInterceptor(ResourceAccessPreHandler accessPreHandler, String tokenName) {
        Assert.notNull(accessPreHandler, "Parameter \"accessPreHandler\" must not null. ");
        Assert.notBlank(tokenName, "Parameter \"tokenName\" must not blank. ");
        this.accessPreHandler = accessPreHandler;
        this.tokenName = tokenName;
    }

    public ResourceAccessSpringInterceptor(ResourceAccessPreHandler accessPreHandler) {

        this(accessPreHandler, DEF_TOKEN_NAME);
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        String token = request.getHeader(tokenName);
        String path = request.getServletPath();
        return (Boolean) accessPreHandler.handle(ACCESS_TYPE, path, token, request, response);
    }

}
