/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.action.support;

import kunlun.action.ActionHandler;
import kunlun.core.handler.ConfigSupportHandler;

import java.util.LinkedHashMap;

/**
 * The automated action handler.
 * @author Kahle
 */
public interface AutoActionHandler extends ActionHandler, ConfigSupportHandler {
    // TODO: ConfigSupportHandler will delete

    /**
     * Get the action handler name.
     * @return The action name
     */
    String getName();

    @Deprecated
    class HandlerConfigImpl extends LinkedHashMap<String, String> implements HandlerConfig {

        @Override
        public String getProperty(String name) {

            return get(name);
        }

    }

}
