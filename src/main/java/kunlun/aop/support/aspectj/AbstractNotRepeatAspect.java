/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.aop.support.aspectj;

import kunlun.core.Handler;
import kunlun.data.Dict;
import kunlun.lock.LockUtils;
import kunlun.util.Assert;
import kunlun.util.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.common.constant.Symbols.*;

/**
 * The abstract aspect for @NotRepeat base on aspectj.
 * @see kunlun.core.annotation.NotRepeat
 * @author Kahle
 */
public abstract class AbstractNotRepeatAspect extends AbstractAspect {
    private static final Logger log = LoggerFactory.getLogger(AbstractNotRepeatAspect.class);
    private final Map<String, StrategyHandler> handlers;

    public AbstractNotRepeatAspect(Map<String, StrategyHandler> handlers) {
        Assert.notNull(handlers, "Parameter \"handlers\" must not null. ");
        this.handlers = handlers;
    }

    public void registerHandler(String strategy, StrategyHandler strategyHandler) {
        Assert.notNull(strategyHandler, "Parameter \"strategyHandler\" must not null. ");
        Assert.notBlank(strategy, "Parameter \"strategy\" must not blank. ");
        String className = strategyHandler.getClass().getName();
        handlers.put(strategy, strategyHandler);
        log.info("Register the strategy handler \"{}\" to \"{}\" in @NotRepeat. ", className, strategy);
    }

    public void deregisterHandler(String strategy) {
        Assert.notBlank(strategy, "Parameter \"strategy\" must not blank. ");
        StrategyHandler remove = handlers.remove(strategy);
        if (remove != null) {
            String className = remove.getClass().getName();
            log.info("Deregister the strategy handler \"{}\" from \"{}\" in @NotRepeat. ", className, strategy);
        }
    }

    public StrategyHandler getHandler(String strategy) {
        Assert.notBlank(strategy, "Parameter \"strategy\" must not blank. ");
        StrategyHandler handler = handlers.get(strategy);
        Assert.notNull(handler
                , "The corresponding strategy handler could not be found by strategy. ");
        return handler;
    }

    /**
     * The strategy handler for @NotRepeat.
     * @author Kahle
     */
    public interface StrategyHandler extends Handler {

        /**
         * Intercept repeated invokes.
         * @param joinPoint The proceeding join point
         * @param config The config String
         * @param arguments The method arguments
         * @return The aop around invoke result
         * @throws Throwable The aop error
         */
        Object handle(ProceedingJoinPoint joinPoint, String config, Object[] arguments) throws Throwable;

    }

    /**
     * The abstract strategy handler.
     * @author Kahle
     */
    public static abstract class AbstractStrategyHandler implements StrategyHandler {

        /**
         * Parse the config.
         * @param config The config string
         * @return The config dict
         */
        protected Dict parseConfig(String config) {
            Dict result = Dict.of();
            if (StringUtils.isBlank(config)) { return result; }
            // If value contains &, escape it.
            String[] split = config.split(AMPERSAND);
            for (String str : split) {
                if (StringUtils.isBlank(str)) { continue; }
                int indexOf = str.indexOf(EQUAL), length = str.length();
                if (indexOf >= ZERO) {
                    int beginIdx = indexOf + ONE;
                    String value = beginIdx >= length ? EMPTY_STRING : str.substring(beginIdx, length);
                    String key = str.substring(ZERO, indexOf);
                    result.set(key, value);
                }
                else { result.set(str, null); }
            }
            return result;
        }

        /**
         * Pre handle config information.
         * @param config The config to be handled
         */
        protected abstract void handleConfig(Dict config);

        /**
         * Build the lock name based on the arguments.
         * @param config The config
         * @param arguments The arguments
         * @return The lock name
         */
        protected abstract String buildLockName(Dict config, Object[] arguments);

        /**
         * Handle try lock failure.
         * @param tryLock The try lock status
         * @param message The failure message
         */
        protected abstract void tryLockFailure(boolean tryLock, String message);

        @Override
        public Object handle(ProceedingJoinPoint joinPoint, String config, Object[] arguments) throws Throwable {
            // Validate arguments.
            Assert.notEmpty(arguments, "Parameter \"arguments\" must not empty. ");
            boolean allIsNull = true;
            for (Object arg : arguments) { if (arg != null) { allIsNull = false; break; } }
            Assert.isFalse(allIsNull, "Parameter \"arguments\" all elements is null. ");
            // Parse config.
            Dict parseConfig = parseConfig(config);
            // Pre handle config.
            handleConfig(parseConfig);
            // Get necessary parameters.
            String manager = parseConfig.getString("lock");
            String message = parseConfig.getString("message");
            // Build lock name by arguments.
            String lockName = buildLockName(parseConfig, arguments);
            // Try lock or throw.
            boolean tryLock = LockUtils.tryLock(manager, lockName);
            tryLockFailure(tryLock, message);
            // Do business.
            try {
                return joinPoint.proceed();
            }
            finally {
                LockUtils.unlock(manager, lockName);
            }
        }

    }

    /**
     * The abstract auto strategy handler.
     * @author Kahle
     */
    public static abstract class AbstractAutoStrategyHandler extends AbstractStrategyHandler {

        /**
         * Get the strategy.
         * @return The strategy
         */
        public abstract String getStrategy();

    }

}
