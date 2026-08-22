package kunlun.spring.security;

import kunlun.core.handler.ResourceAccessPreHandler;
import kunlun.spring.context.ContextServletAutoConfiguration;
import kunlun.spring.security.support.ResourceAccessSpringInterceptor;
import kunlun.util.CollUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableConfigurationProperties({SecurityProperties.class})
@AutoConfigureAfter(ContextServletAutoConfiguration.class)
@ConditionalOnProperty(name = {"kunlun.context.servlet.enabled",
        "kunlun.security.servlet.access.enabled"}, havingValue = "true")
public class ResourceAccessPreHandleConfiguration implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(ResourceAccessPreHandleConfiguration.class);
    private final ResourceAccessPreHandler resourceAccessPreHandler;
    private final SecurityProperties securityProperties;

    @Autowired
    public ResourceAccessPreHandleConfiguration(SecurityProperties securityProperties,
                                                ResourceAccessPreHandler resourceAccessPreHandler) {
        this.resourceAccessPreHandler = resourceAccessPreHandler;
        this.securityProperties = securityProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 对于这个拦截器来说，应该所有的URL都是需要拦截的
        // 因为它是上下文工具的支持，即使是免登录的URL也需要维护上下文
        // 不过还是提供一个可以排除某些 URL 的口子吧

        SecurityProperties.AccessPermissionConfig accessPermissionConfig =
                securityProperties.getAccess();
        List<String> excludePathPatterns = accessPermissionConfig.getExcludePathPatterns();
        List<String> pathPatterns = accessPermissionConfig.getPathPatterns();
        if (CollUtil.isEmpty(pathPatterns)) {
            pathPatterns = Collections.singletonList("/**");
        }

        InterceptorRegistration registration = registry
                .addInterceptor(new ResourceAccessSpringInterceptor(resourceAccessPreHandler))
                .addPathPatterns(pathPatterns);
        if (CollUtil.isNotEmpty(excludePathPatterns)) {
            registration.excludePathPatterns(excludePathPatterns);
        }
        log.info("The resource access tools was initialized success. ");
    }

}
