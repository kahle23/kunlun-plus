/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.flow.approval.biz;

import kunlun.flow.approval.biz.BizProcessor;
import kunlun.flow.approval.biz.ProcessorUtil;
import kunlun.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * The business processor autoconfiguration.
 * @author Kahle
 */
@Configuration
public class BizProcessorAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(BizProcessorAutoConfiguration.class);

    public BizProcessorAutoConfiguration(ApplicationContext appContext) {
        // If not have beans, processorMap is empty map, not is null.
        Map<String, BizProcessor> processorMap = appContext.getBeansOfType(BizProcessor.class);
        for (BizProcessor processor : processorMap.values()) {
            if (processor == null) { continue; }
            if (StrUtil.isBlank(processor.getBusinessKey())) {
                log.warn("The business processor \"{}\"'s " +
                        "business key is blank, it will be ignored. ", processor.getClass());
                continue;
            }
            ProcessorUtil.register(processor);
        }
    }

}
