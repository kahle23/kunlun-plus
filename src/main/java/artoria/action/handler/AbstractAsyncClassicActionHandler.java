package artoria.action.handler;

import artoria.core.handler.AsyncSupportHandler;
import artoria.util.Assert;
import artoria.util.ObjectUtils;

/**
 * The abstract asynchronous classic action handler.
 * @author Kahle
 */
public abstract class AbstractAsyncClassicActionHandler
        extends AbstractAutoClassicActionHandler implements AsyncSupportHandler {

    @Deprecated
    public AbstractAsyncClassicActionHandler(String actionName, Class<?> registeredClass) {

        super(actionName, registeredClass);
    }

    public AbstractAsyncClassicActionHandler(String actionName) {

        super(actionName);
    }

    @Override
    public <T> T execute(Object input, Class<T> clazz) {
        // The input object cannot be null.
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        // Build async supported context.
        AsyncSupportContext context = buildContext(input, clazz);
        // The default is synchronous call.
        if (context.getAsync() == null || !context.getAsync()) {
            // Synchronous call.
            return ObjectUtils.cast(doExecute(context));
        }
        else {
            // Asynchronous call.
            AsyncExecuteTask task = new AsyncExecuteTask(this, context);
            context.setFuture(context.getThreadPool().submit(task));
            // Get the result.
            context.setFinish(false);
            return ObjectUtils.cast(getResult(context));
        }
    }

}
