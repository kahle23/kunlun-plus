/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.config.codec;

import kunlun.codec.CodecUtils;
import kunlun.codec.support.ApacheBase64;
import kunlun.codec.support.Java8Base64;
import kunlun.util.ClassLoaderUtils;
import kunlun.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import static java.lang.Boolean.TRUE;
import static kunlun.codec.CodecUtils.BASE64;
import static kunlun.common.constant.Numbers.MINUS_ONE;

/**
 * The base64 auto-configuration.
 * @see org.apache.commons.codec.binary.Base64
 * @author Kahle
 */
@Configuration
public class Base64AutoConfiguration implements InitializingBean, DisposableBean {
    private static final String APACHE_BASE64 = "org.apache.commons.codec.binary.Base64";
    private static final String JAVA_BASE64 = "java.util.Base64";
    private static final String BASE64_URL_SAFE = "base64-url-safe";
    private static final String BASE64_MIME = "base64-mime";
    private static final Logger log = LoggerFactory.getLogger(Base64AutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        if (ClassUtils.isPresent(APACHE_BASE64, classLoader)) {
            // If have Apache Commons Codec, to use it.
            ApacheBase64 base64 = new ApacheBase64();
            CodecUtils.registerEncoder(BASE64, base64);
            CodecUtils.registerDecoder(BASE64, base64);
            base64 = new ApacheBase64(TRUE);
            CodecUtils.registerEncoder(BASE64_URL_SAFE, base64);
            CodecUtils.registerDecoder(BASE64_URL_SAFE, base64);
            base64 = new ApacheBase64(TRUE, MINUS_ONE, null);
            CodecUtils.registerEncoder(BASE64_MIME, base64);
            CodecUtils.registerDecoder(BASE64_MIME, base64);
        }
        else if (ClassUtils.isPresent(JAVA_BASE64, classLoader)) {
            // If have "java.util.Base64", to use it.
            Java8Base64 base64 = new Java8Base64();
            CodecUtils.registerEncoder(BASE64, base64);
            CodecUtils.registerDecoder(BASE64, base64);
            base64 = new Java8Base64(TRUE);
            CodecUtils.registerEncoder(BASE64_URL_SAFE, base64);
            CodecUtils.registerDecoder(BASE64_URL_SAFE, base64);
            base64 = new Java8Base64(TRUE, MINUS_ONE, null);
            CodecUtils.registerEncoder(BASE64_MIME, base64);
            CodecUtils.registerDecoder(BASE64_MIME, base64);
        }
        log.info("The base64 tools was initialized success. ");
    }

    @Override
    public void destroy() throws Exception {
    }

}
