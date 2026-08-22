package kunlun.spring.security;

import cn.hutool.core.collection.CollUtil;
import kunlun.bean.BeanHolder;
import kunlun.core.AccessController;
import kunlun.core.DataController;
import kunlun.core.handler.ResourceAccessPreHandler;
import kunlun.security.SecurityContext;
import kunlun.security.TokenManager;
import kunlun.security.UserService;
import kunlun.security.support.*;
import kunlun.spring.security.support.SpringSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({SecurityProperties.class})
@ConditionalOnProperty(name = {"kunlun.context.servlet.enabled",
        "kunlun.security.servlet.enabled"}, havingValue = "true")
public class SecurityAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(SecurityAutoConfiguration.class);
    private final SecurityProperties securityProperties;
    private final ApplicationContext appContext;

    @Autowired
    public SecurityAutoConfiguration(SecurityProperties securityProperties,
                                     ApplicationContext appContext) {
        this.securityProperties = securityProperties;
        this.appContext = appContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceAccessPreHandler defaultResourceAccessPreHandler() {
        SecurityProperties.AccessPermissionConfig config = securityProperties.getAccess();
        if (config.getEnabled() != null && config.getEnabled()) {
            List<String> ignoredUrls = config.getIgnoredUrls();
            Boolean showLog = config.getShowLog();
            return new SimpleResourceAccessPreHandler(showLog, ignoredUrls);
        } else {
            return new EmptyResourceAccessPreHandler();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenManager defaultTokenManager() {
        Long tokenTimeToLive = securityProperties.getTokenTimeToLive();
        if (tokenTimeToLive == null || tokenTimeToLive<= 0) {
            tokenTimeToLive = 30*24*60*60L;
        }
        Map<String, StringRedisTemplate> beansOfType = appContext.getBeansOfType(StringRedisTemplate.class);
        StringRedisTemplate redisTemplate = CollUtil.getFirst(beansOfType.values());
        if (redisTemplate != null) {
            return new RedisJwtTokenManager(redisTemplate, tokenTimeToLive);
        } else {
            return new JwtTokenManager(null, tokenTimeToLive);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessController defaultAccessController(UserService userService) {

        return new SimpleAccessController(userService);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataController defaultDataController() {

        return new EmptyDataController();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityContext defaultSecurityContext(TokenManager tokenManager,
                                                  UserService userService,
                                                  AccessController accessController,
                                                  DataController dataController) {
        SpringSecurityContext context = new SpringSecurityContext(
                appContext, tokenManager, userService, accessController, dataController);
        BeanHolder.put("springSecurityContext", context);
        return context;
    }

}
