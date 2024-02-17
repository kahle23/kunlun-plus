/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.desensitize;

import kunlun.logging.Logger;
import kunlun.logging.LoggerFactory;
import kunlun.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data desensitizing tools.
 * @author Kahle
 */
public class DesensitizeUtils {
    private static final Map<String, Desensitizer> DESENSITIZER_MAP = new ConcurrentHashMap<String, Desensitizer>();
    private static final Logger log = LoggerFactory.getLogger(DesensitizeUtils.class);

    public static Desensitizer unregister(String name) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        Desensitizer remove = DESENSITIZER_MAP.remove(name);
        if (remove != null) {
            String removeClassName = remove.getClass().getName();
            log.info("Unregister \"{}\" to \"{}\". ", removeClassName, name);
        }
        return remove;
    }

    public static void register(String name, Desensitizer desensitizer) {
        Assert.notNull(desensitizer, "Parameter \"desensitizer\" must not null. ");
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        String dataMaskerClassName = desensitizer.getClass().getName();
        log.info("Register \"{}\" to \"{}\". ", dataMaskerClassName, name);
        DESENSITIZER_MAP.put(name, desensitizer);
    }

    public static String desensitize(String name, String data) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        Desensitizer desensitizer = DESENSITIZER_MAP.get(name);
        if (desensitizer == null) {
            throw new IllegalStateException(
                    "The data desensitizer named \"" + name + "\" could not be found. "
            );
        }
        return desensitizer.desensitize(data);
    }

}
