package artoria.action.handler;

import artoria.util.Assert;

/**
 * The abstract automated operate action handler.
 * @author Kahle
 */
public abstract class AbstractAutoOperateActionHandler
        extends AbstractOperateActionHandler implements AutoActionHandler {
    private final HandlerConfigImpl handlerConfig;
    private final String actionName;

    @Deprecated
    public AbstractAutoOperateActionHandler(String actionName, Class<?> registeredClass) {
        this(actionName);
        Assert.notNull(registeredClass, "Parameter \"registeredClass\" must not null. ");
        getConfig().put("registeredClass", registeredClass.getName());
    }

    public AbstractAutoOperateActionHandler(String actionName) {

        this(actionName, new HandlerConfigImpl());
    }

    public AbstractAutoOperateActionHandler(String actionName, HandlerConfigImpl handlerConfig) {
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
