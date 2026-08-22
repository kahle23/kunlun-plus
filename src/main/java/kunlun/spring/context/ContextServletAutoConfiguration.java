package kunlun.spring.context;

import kunlun.spring.context.support.ContextSupportRequestBodyAdvice;
import kunlun.spring.context.support.ContextSupportResponseBodyAdvice;
import kunlun.spring.context.support.ContextSupportSpringInterceptor;
import kunlun.util.Assert;
import kunlun.util.CollUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableConfigurationProperties({ContextServletProperties.class})
@ConditionalOnProperty(name = "kunlun.context.servlet.enabled", havingValue = "true")
@Import({ContextSupportRequestBodyAdvice.class, ContextSupportResponseBodyAdvice.class})
public class ContextServletAutoConfiguration implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(ContextServletAutoConfiguration.class);
    private final ContextServletProperties properties;

    public ContextServletAutoConfiguration(ContextServletProperties properties) {
        Assert.notNull(properties, "Parameter \"properties\" must not null. ");
        this.properties = properties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 对于这个拦截器来说，应该所有的URL都是需要拦截的
        // 因为它是上下文工具的支持，即使是免登录的URL也需要维护上下文
        // 不过还是提供一个可以排除某些 URL 的口子吧
        ContextSupportSpringInterceptor contextInterceptor = new ContextSupportSpringInterceptor();
        List<String> excludePathPatterns = properties.getExcludePathPatterns();
        List<String> pathPatterns = properties.getPathPatterns();
        if (CollUtil.isEmpty(pathPatterns)) {
            pathPatterns = Collections.singletonList("/**");
        }
        InterceptorRegistration registration = registry
                .addInterceptor(contextInterceptor)
                .addPathPatterns(pathPatterns);
        if (CollUtil.isNotEmpty(excludePathPatterns)) {
            registration.excludePathPatterns(excludePathPatterns);
        }
        log.info("The context tools was initialized success. ");
    }

}
