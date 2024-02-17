/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support;

import kunlun.data.json.JsonUtils;
import kunlun.util.ClassLoaderUtils;
import kunlun.util.ClassUtils;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * The json tools auto-configuration.
 * @author Kahle
 */
@Configuration
public class JsonAutoConfiguration implements InitializingBean, DisposableBean {
    private static final String JACKSON_CLASS = "com.fasterxml.jackson.databind.ObjectMapper";
    private static final String FASTJSON_CLASS = "com.alibaba.fastjson.JSON";
    private static final String GSON_CLASS = "com.google.gson.Gson";
    private static final Logger log = LoggerFactory.getLogger(JsonAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        String defaultHandler = null;
        if (ClassUtils.isPresent(JACKSON_CLASS, classLoader)) {
            JsonUtils.registerHandler("jackson", new JacksonHandler());
            defaultHandler = "jackson";
        }
        if (ClassUtils.isPresent(GSON_CLASS, classLoader)) {
            JsonUtils.registerHandler("gson", new GsonHandler());
            if (StringUtils.isBlank(defaultHandler)) {
                defaultHandler = "gson";
            }
        }
        if (ClassUtils.isPresent(FASTJSON_CLASS, classLoader)) {
            JsonUtils.registerHandler("fastjson", new FastJsonHandler());
            if (StringUtils.isBlank(defaultHandler)) {
                defaultHandler = "fastjson";
            }
        }
        if (StringUtils.isNotBlank(defaultHandler)) {
            JsonUtils.setDefaultHandlerName(defaultHandler);
        }
        else {
            log.warn("Can not found \"jackson\" or \"gson\" or \"fastjson\", will keep default. ");
        }
    }

    @Override
    public void destroy() throws Exception {

    }

}
