/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.context.support.spring;

import kunlun.bean.BeanHolder;
import kunlun.context.support.ServletContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

/**
 * The response body advice for the servlet context.
 * @author Kahle
 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
 */
@ControllerAdvice
public class ContextSupportResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        Map<String, ServletContext> beans = BeanHolder.getBeans(ServletContext.class);
        if (beans == null || beans.isEmpty()) { return body; }
        for (Map.Entry<String, ServletContext> entry : beans.entrySet()) {
            ServletContext context = entry.getValue();
            context.setResponseBody(body);
        }
        return body;
    }

}
