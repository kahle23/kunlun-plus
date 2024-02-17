/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support;

import kunlun.action.ActionUtils;
import kunlun.core.handler.ConfigSupportHandler;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * The action handler auto configuration.
 * @author Kahle
 */
@Configuration
public class ActionHandlerAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ActionHandlerAutoConfiguration.class);

    public ActionHandlerAutoConfiguration(ApplicationContext appContext) {
        // If not have beans, handlerMap is empty map, not is null.
        Map<String, AutoActionHandler> handlerMap = appContext.getBeansOfType(AutoActionHandler.class);
        for (AutoActionHandler actionHandler : handlerMap.values()) {
            if (actionHandler == null) { continue; }
            String actionName = actionHandler.getName();
            if (StringUtils.isBlank(actionName)) {
                log.warn("The action handler \"{}\"'s name is blank, it will be ignored. "
                        , actionHandler.getClass());
                continue;
            }
            ActionUtils.registerHandler(actionName, actionHandler);
            // ---- TODO: Will delete
            ConfigSupportHandler.HandlerConfig config = actionHandler.getConfig();
            String registeredClass = null;
            if (config != null) {
                registeredClass = config.getProperty("registeredClass");
            }
            if (StringUtils.isNotBlank(registeredClass)) {
                registeredClass = "class:" + registeredClass.trim();
                ActionUtils.registerHandler(registeredClass, actionHandler);
            }
            // ----
        }
    }

}
