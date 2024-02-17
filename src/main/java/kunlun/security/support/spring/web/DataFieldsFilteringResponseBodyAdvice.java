/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.security.support.spring.web;

import kunlun.core.handler.DataFieldsFilteringHandler;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;

/**
 * The response body advice for the data fields filtering.
 * @author Kahle
 */
@RestControllerAdvice
public class DataFieldsFilteringResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private static final String FILTERING_TYPE = "response-body-advice-data-permission";

    @Resource
    private DataFieldsFilteringHandler filteringHandler;

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
        return filteringHandler.filter(FILTERING_TYPE, body
                , returnType, selectedContentType, selectedConverterType, request, response);
    }

}
