package artoria.config;

import artoria.codec.Base64Utils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import artoria.spring.InitializingDisposableBean;

/**
 * Base64 auto configuration.
 * @see org.apache.commons.codec.binary.Base64
 * @author Kahle
 */
@Configuration
public class Base64AutoConfiguration implements InitializingDisposableBean {
    private static Logger log = LoggerFactory.getLogger(Base64AutoConfiguration.class);
    private static final String COMMONS_CODEC_BASE64 = "org.apache.commons.codec.binary.Base64";

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        // If have Apache Commons Codec, to use it.
        if (ClassUtils.isPresent(COMMONS_CODEC_BASE64, classLoader)) {
            log.info("Set base64 delegate: " + COMMONS_CODEC_BASE64);
            CommonsCodecBase64Delegate delegate = new CommonsCodecBase64Delegate();
            Base64Utils.setDelegate(delegate);
        }
    }

    @Override
    public void destroy() throws Exception {
    }

    private static class CommonsCodecBase64Delegate implements Base64Utils.Base64Delegate {
        private final org.apache.commons.codec.binary.Base64 base64 =
                new org.apache.commons.codec.binary.Base64();
        private final org.apache.commons.codec.binary.Base64 base64UrlSafe =
                new org.apache.commons.codec.binary.Base64(0, null, true);

        @Override
        public byte[] encode(byte[] source) {

            return this.base64.encode(source);
        }

        @Override
        public byte[] decode(byte[] source) {

            return this.base64.decode(source);
        }

        @Override
        public byte[] encodeUrlSafe(byte[] source) {

            return this.base64UrlSafe.encode(source);
        }

        @Override
        public byte[] decodeUrlSafe(byte[] source) {

            return this.base64UrlSafe.decode(source);
        }

    }

}
