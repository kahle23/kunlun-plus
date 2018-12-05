package artoria.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import artoria.exception.InternalErrorController;
import artoria.exception.InternalExceptionHandler;
import artoria.spring.InitializingDisposableBean;

/**
 * Exception auto configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@EnableConfigurationProperties({ExceptionProperties.class})
@Import({InternalExceptionHandler.class, InternalErrorController.class})
public class ExceptionAutoConfiguration implements InitializingDisposableBean {
    private static Logger log = LoggerFactory.getLogger(ExceptionAutoConfiguration.class);
    private InternalExceptionHandler inbuiltExceptionHandler;
    private InternalErrorController inbuiltErrorController;

    @Autowired
    public ExceptionAutoConfiguration(InternalExceptionHandler inbuiltExceptionHandler
            , InternalErrorController inbuiltErrorController) {
        this.inbuiltExceptionHandler = inbuiltExceptionHandler;
        this.inbuiltErrorController = inbuiltErrorController;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (inbuiltExceptionHandler != null) {
            log.info("Initialize inbuilt exception handler success: " + inbuiltExceptionHandler);
        }
        if (inbuiltErrorController != null) {
            log.info("Initialize inbuilt error controller success: " + inbuiltErrorController);
        }
    }

    @Override
    public void destroy() throws Exception {
    }

}
