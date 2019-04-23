package artoria.logging;

import artoria.user.UserAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Controller log print interceptor auto configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureAfter(UserAutoConfiguration.class)
@ConditionalOnProperty(name = "artoria.logging.printControllerLog", havingValue = "true")
public class ControllerLogAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        ControllerLogInterceptor interceptor = new ControllerLogInterceptor();
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }

}
