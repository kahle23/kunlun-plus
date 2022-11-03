package artoria.spring.config.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import java.security.Provider;
import java.security.Security;

/**
 * The bouncy castle provider auto configuration.
 * @author Kahle
 */
@Configuration
@ConditionalOnClass(BouncyCastleProvider.class)
public class BouncyCastleAutoConfiguration implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(BouncyCastleAutoConfiguration.class);

    protected boolean initialized() {
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            if (provider == null) { continue; }
            if (provider instanceof BouncyCastleProvider) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (initialized()) { return; }
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        log.info("The bouncy castle provider {} was initialized success. ", provider.getVersion());
    }

}
