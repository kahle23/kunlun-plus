/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support;

import kunlun.util.Assert;

/**
 * The abstract automated classic action handler.
 * @author Kahle
 */
public abstract class AbstractAutoClassicActionHandler
        extends AbstractClassicActionHandler implements AutoActionHandler {
    private final HandlerConfigImpl handlerConfig;
    private final String actionName;

    @Deprecated
    public AbstractAutoClassicActionHandler(String actionName, Class<?> registeredClass) {
        this(actionName);
        Assert.notNull(registeredClass, "Parameter \"registeredClass\" must not null. ");
        getConfig().put("registeredClass", registeredClass.getName());
    }

    public AbstractAutoClassicActionHandler(String actionName) {

        this(actionName, new HandlerConfigImpl());
    }

    public AbstractAutoClassicActionHandler(String actionName, HandlerConfigImpl handlerConfig) {
        Assert.notNull(handlerConfig, "Parameter \"handlerConfig\" must not null. ");
        Assert.notBlank(actionName, "Parameter \"actionName\" must not blank. ");
        this.handlerConfig = handlerConfig;
        this.actionName = actionName;
    }

    @Override
    public String getName() {

        return actionName;
    }

    @Override
    public HandlerConfigImpl getConfig() {

        return handlerConfig;
    }

}
