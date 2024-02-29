/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.exception;

import kunlun.common.SimpleCode;
import kunlun.data.CodeDefinition;
import kunlun.renderer.support.FormatTextRenderer;
import kunlun.renderer.support.PrintfTextRenderer;
import kunlun.util.ArrayUtils;
import kunlun.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The business exception.
 * @author Kahle
 */
public class BusinessException extends UncheckedException {
    private static final Logger log = LoggerFactory.getLogger(BusinessException.class);
    private static volatile FormatTextRenderer textRenderer;
    private static volatile Object defaultErrorCode;

    public static FormatTextRenderer getTextRenderer() {
        if (textRenderer != null) { return textRenderer; }
        synchronized (BusinessException.class) {
            if (textRenderer != null) { return textRenderer; }
            setTextRenderer(new PrintfTextRenderer());
        }
        return textRenderer;
    }

    public static void setTextRenderer(FormatTextRenderer textRenderer) {
        Assert.notNull(textRenderer, "Parameter \"textRenderer\" must not null. ");
        log.debug("Set format text renderer: {}", textRenderer.getClass().getName());
        BusinessException.textRenderer = textRenderer;
    }

    public static Object getDefaultErrorCode() {
        if (defaultErrorCode != null) { return defaultErrorCode; }
        synchronized (BusinessException.class) {
            if (defaultErrorCode != null) { return defaultErrorCode; }
            setDefaultErrorCode(500);
        }
        return defaultErrorCode;
    }

    public static void setDefaultErrorCode(Object defaultErrorCode) {
        Assert.notNull(defaultErrorCode, "Parameter \"defaultErrorCode\" must not null. ");
        log.debug("Set default error code: {}", defaultErrorCode);
        BusinessException.defaultErrorCode = defaultErrorCode;
    }

    protected static String render(CodeDefinition errorCode, Object... arguments) {
        Assert.notNull(errorCode, "Parameter \"errorCode\" must not null. ");
        if (ArrayUtils.isEmpty(arguments)) { return errorCode.getDescription(); }
        return getTextRenderer().render(errorCode.getDescription(), arguments);
    }

    protected static String render(String template, Object... arguments) {
        Assert.notBlank(template, "Parameter \"template\" must not blank. ");
        if (ArrayUtils.isEmpty(arguments)) { return template; }
        return getTextRenderer().render(template, arguments);
    }


    private final CodeDefinition errorCode;
    private final Object[] arguments;

    public BusinessException(String message, Object... arguments) {
        super(render(message, arguments));
        this.errorCode = new SimpleCode(getDefaultErrorCode(), message);
        this.arguments = arguments;
    }

    public BusinessException(String message, Throwable cause, Object... arguments) {
        super(render(message, arguments), cause);
        this.errorCode = new SimpleCode(getDefaultErrorCode(), message);
        this.arguments = arguments;
    }

    public BusinessException(CodeDefinition errorCode, Object... arguments) {
        super(render(errorCode, arguments));
        this.errorCode = errorCode;
        this.arguments = arguments;
    }

    public BusinessException(CodeDefinition errorCode, Throwable cause, Object... arguments) {
        super(render(errorCode, arguments), cause);
        this.errorCode = errorCode;
        this.arguments = arguments;
    }

    public CodeDefinition getErrorCode() {

        return errorCode;
    }

    public Object[] getArguments() {

        return arguments;
    }

}
