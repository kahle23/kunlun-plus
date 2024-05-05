/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.exception;

import kunlun.exception.ServletErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The simple spring exception handler.
 * @author Kahle
 */
@ControllerAdvice
public class ServletErrorHandlerExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ServletErrorHandlerExceptionHandler.class);
    private final ServletErrorHandler servletErrorHandler;

    @Autowired
    public ServletErrorHandlerExceptionHandler(ServletErrorHandler servletErrorHandler) {
        this.servletErrorHandler = servletErrorHandler;
        log.info("The exception handler based on servlet error handler was initialized success. ");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        log.error("Caught an unhandled exception. ", ex);
        return servletErrorHandler.handle(request, response, ex);
    }

}
