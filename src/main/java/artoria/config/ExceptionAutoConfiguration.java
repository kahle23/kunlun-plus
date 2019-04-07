package artoria.config;

import artoria.exception.InternalErrorController;
import artoria.exception.InternalExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Exception auto configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@EnableConfigurationProperties({ExceptionProperties.class})
@Import({InternalExceptionHandler.class, InternalErrorController.class})
public class ExceptionAutoConfiguration implements InitializingBean, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(ExceptionAutoConfiguration.class);
    private InternalExceptionHandler internalExceptionHandler;
    private InternalErrorController internalErrorController;

    @Autowired
    public ExceptionAutoConfiguration(InternalExceptionHandler internalExceptionHandler
            , InternalErrorController internalErrorController) {
        this.internalExceptionHandler = internalExceptionHandler;
        this.internalErrorController = internalErrorController;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (internalExceptionHandler != null) {
            log.info(">> The internal exception handler was initialized success. ");
        }
        if (internalErrorController != null) {
            log.info(">> The internal error controller was initialized success. ");
        }
    }

    @Override
    public void destroy() throws Exception {
    }

}
