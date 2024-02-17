/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.security.support.servlet;

import kunlun.core.handler.ResourceAccessPreHandler;
import kunlun.util.Assert;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * The resource access pre filter.
 * @author Kahle
 */
public class ResourceAccessFilter implements Filter {
    private static final String DEF_TOKEN_NAME = "authorization";
    private static final String ACCESS_TYPE = "servlet-filter";
    private final ResourceAccessPreHandler accessPreHandler;
    private final String tokenName;

    public ResourceAccessFilter(ResourceAccessPreHandler accessPreHandler, String tokenName) {
        Assert.notNull(accessPreHandler, "Parameter \"accessPreHandler\" must not null. ");
        Assert.notBlank(tokenName, "Parameter \"tokenName\" must not blank. ");
        this.accessPreHandler = accessPreHandler;
        this.tokenName = tokenName;
    }

    public ResourceAccessFilter(ResourceAccessPreHandler accessPreHandler) {

        this(accessPreHandler, DEF_TOKEN_NAME);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        String token = ((HttpServletRequest) request).getHeader(tokenName);
        String path = ((HttpServletRequest) request).getServletPath();
        if ((Boolean) accessPreHandler.handle(ACCESS_TYPE, path, token, request, response)) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

}
