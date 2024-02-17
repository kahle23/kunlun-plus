/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.context.support.spring;

import kunlun.bean.BeanHolder;
import kunlun.context.support.ServletContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * The request body advice for the servlet context.
 * @author Kahle
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
 */
@ControllerAdvice
public class ContextSupportRequestBodyAdvice extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter,
                            @NonNull Type targetType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @NonNull
    @Override
    public Object afterBodyRead(@NonNull Object body,
                                @NonNull HttpInputMessage inputMessage,
                                @NonNull MethodParameter parameter,
                                @NonNull Type targetType,
                                @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        Map<String, ServletContext> beans = BeanHolder.getBeans(ServletContext.class);
        if (beans == null || beans.isEmpty()) { return body; }
        for (Map.Entry<String, ServletContext> entry : beans.entrySet()) {
            ServletContext context = entry.getValue();
            context.setRequestBody(body);
        }
        return body;
    }

}
