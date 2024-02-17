/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support.exchange;

import kunlun.action.support.AbstractAsyncClassicActionHandler;
import kunlun.data.Dict;
import kunlun.data.bean.BeanUtils;
import kunlun.util.Assert;

import java.util.Map;
import java.util.concurrent.Future;

import static kunlun.common.constant.Numbers.*;
import static kunlun.util.ObjectUtils.cast;

/**
 * The abstract import and export action handler.
 * @param <P> The type of the import or export parameter
 * @param <D> The type of data fetched or parsed
 * @author Kahle
 */
public abstract class AbstractImportExportActionHandler<P, D>
        extends AbstractAsyncClassicActionHandler implements ImportExportHandler<P, D> {

    public AbstractImportExportActionHandler(String actionName) {

        super(actionName);
    }

    /**
     * Create the context object from the arguments array.
     * @param contextClass The class of the context object
     * @param arguments The specific arguments array
     * @param <T> The generic type of the context object
     * @return The import or export context object
     */
    protected <T extends ImportExportContext<P>> T createContext(Class<?> contextClass, Object[] arguments) {
        // Check of parameters.
        Assert.notNull(contextClass, "Parameter \"contextClass\" must not null. ");
        Assert.notNull(arguments, "Parameter \"arguments\" must not null. ");
        Assert.isTrue(arguments.length >= TWO
                , "Parameter \"arguments\" length must >= 2. ");
        Assert.notNull(arguments[ZERO], "Parameter \"arguments[0]\" must not null. ");
        Assert.notNull(arguments[ONE], "Parameter \"arguments[1]\" must not null. ");
        // Extract parameters.
        Class<?> clazz = cast(arguments[ONE]);
        Object input = arguments[ZERO];
        // Build context.
        T context = cast(BeanUtils.beanToBean(input, contextClass));
        // Set some values.
        context.setArguments(arguments);
        context.setResultClass(clazz);
        P param = cast(input);
        context.setParam(param);
        // End.
        return context;
    }

    @Override
    public void pushTask(ImportExportContext<P> context) {
        // The import status: 0 unknown, 1 will import, 2 importing, 3 processing, 4 timeout(dead), 5 failure, 6 success
        // The export status: 0 unknown, 1 will export, 2 exporting, 3 processing, 4 timeout(dead), 5 failure, 6 success
        // Maybe implementation classes aren't necessary.
    }

    @Override
    public void preHandle(ImportExportContext<P> context) {

        // The empty implementation.
    }

    @Override
    public Object save(ImportExportContext<P> context) {
        // Maybe implementation classes aren't necessary.
        return null;
    }

    @Override
    public Object getResult(AsyncSupportContext context) {
        // Transform context object.
        ImportExportContext<P> nowContext = cast(context);
        // Get the result class.
        Class<?> resultClass = nowContext.getResultClass();
        Boolean async = nowContext.getAsync();
        // Result type is string.
        if (CharSequence.class.isAssignableFrom(resultClass)) {
            if (async == null || !async) {
                return nowContext.getResultAddress();
            }
            else {
                return nowContext.getResultMessage();
            }
        }
        // Result type is map.
        else if (Map.class.isAssignableFrom(resultClass)) {
            Dict dict = Dict.of("taskId", nowContext.getTaskId());
            dict.set("module", nowContext.getModule());
            dict.set("status", nowContext.getStatus());
            dict.set("beginTime", nowContext.getStatus());
            dict.set("endTime", nowContext.getStatus());
            dict.set("totalCount", nowContext.getTotalCount());
            dict.set("successCount", nowContext.getSuccessCount());
            dict.set("failureCount", nowContext.getFailureCount());
            dict.set("resultAddress", nowContext.getResultAddress());
            dict.set("resultMessage", nowContext.getResultMessage());
            return dict;
        }
        // Result type is future.
        else if (Future.class.isAssignableFrom(resultClass)) {
            return nowContext.getFuture();
        }
        // Others.
        else {
            // Convert bean to bean.
            return BeanUtils.beanToBean(context, resultClass);
        }
    }

}
