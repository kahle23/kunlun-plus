package artoria.user;

import artoria.config.SwaggerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties({UserProperties.class})
@ConditionalOnBean({TokenManager.class, UserManager.class})
@ConditionalOnProperty(name = "artoria.user.enabled", havingValue = "true")
public class UserAutoConfiguration implements WebMvcConfigurer {
    private static Logger log = LoggerFactory.getLogger(UserAutoConfiguration.class);
    private final SwaggerProperties swaggerProperties;
    private final UserProperties userProperties;
    private final TokenManager tokenManager;
    private final UserManager userManager;

    @Autowired
    public UserAutoConfiguration(
            TokenManager tokenManager,
            UserManager userManager,
            UserProperties userProperties,
            SwaggerProperties swaggerProperties
    ) {
        this.tokenManager = tokenManager;
        this.userManager = userManager;
        this.userProperties = userProperties;
        this.swaggerProperties = swaggerProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] wantExclude = userProperties.getExcludePathPatterns();
        UserInterceptor userInterceptor =
                new UserInterceptor(tokenManager, userManager, userProperties);
        List<String> willExclude = new ArrayList<String>(Arrays.asList(wantExclude));
        willExclude.add("/");
        willExclude.add("/error/**");
        if (swaggerProperties.getEnabled()) {
            willExclude.add("/webjars/springfox-swagger-ui/**");
            willExclude.add("/swagger-resources/**");
            willExclude.add("/swagger-ui.html");
            willExclude.add("/v2/api-docs");
            willExclude.add("/csrf");
        }
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(willExclude);
        log.info(">> The user tools was initialized success. ");
    }

}
