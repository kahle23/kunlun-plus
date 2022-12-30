package artoria.action.handler;

import artoria.action.ActionHandler;
import artoria.core.handler.ConfigSupportHandler;

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
