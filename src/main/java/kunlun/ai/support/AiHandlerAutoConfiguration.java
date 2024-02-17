/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.ai.support;

import kunlun.ai.AiUtils;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * The ai handler auto configuration.
 * @author Kahle
 */
@Configuration
public class AiHandlerAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(AiHandlerAutoConfiguration.class);

    public AiHandlerAutoConfiguration(ApplicationContext appContext) {
        // If not have beans, handlerMap is empty map, not is null.
        Map<String, AutoAiHandler> handlerMap = appContext.getBeansOfType(AutoAiHandler.class);
        for (AutoAiHandler aiHandler : handlerMap.values()) {
            if (aiHandler == null) { continue; }
            String handlerName = aiHandler.getName();
            if (StringUtils.isBlank(handlerName)) {
                log.warn("The ai handler \"{}\"'s name is blank, it will be ignored. "
                        , aiHandler.getClass());
                continue;
            }
            AiUtils.registerHandler(handlerName, aiHandler);
        }
    }

}
