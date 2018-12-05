package artoria.config;

import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import artoria.logging.ControllerLogPrinter;
import artoria.spring.InitializingDisposableBean;

/**
 * Logger auto configuration.
 * @author Kahle
 */
@Configuration
@EnableConfigurationProperties({LoggingProperties.class})
@Import({ControllerLogPrinter.class})
public class LoggingAutoConfiguration implements InitializingDisposableBean {
    private static final String SLF4J_BRIDGE_HANDLER_CLASS = "org.slf4j.bridge.SLF4JBridgeHandler";
    private static Logger log = LoggerFactory.getLogger(LoggingAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {

        this.initJulToSlf4j();
    }

    @Override
    public void destroy() throws Exception {
    }

    private void initJulToSlf4j() {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        if (!ClassUtils.isPresent(SLF4J_BRIDGE_HANDLER_CLASS, classLoader)) {
            log.warn("Can not found \"{}\". ", SLF4J_BRIDGE_HANDLER_CLASS);
            return;
        }
        if (SLF4JBridgeHandler.isInstalled()) {
            return;
        }
        // Optionally remove existing handlers attached to j.u.l root logger
        // (since SLF4J 1.6.5)
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
        // the initialization phase of your application
        SLF4JBridgeHandler.install();
        log.info("Jul to slf4j initialize success.");
    }

}
