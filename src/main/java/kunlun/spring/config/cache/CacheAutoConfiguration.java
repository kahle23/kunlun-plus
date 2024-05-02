package kunlun.spring.config.cache;

import cn.hutool.core.collection.CollUtil;
import kunlun.cache.CacheUtils;
import kunlun.cache.support.*;
import kunlun.data.bean.BeanUtils;
import kunlun.spring.config.cache.CacheProperties.JdbcConfig;
import kunlun.spring.config.cache.CacheProperties.RedisConfig;
import kunlun.spring.config.cache.CacheProperties.SimpleConfig;
import kunlun.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;

import static kunlun.util.ObjectUtils.cast;

/**
 * The cache tools auto-configuration.
 * @author Kahle
 */
@Configuration
@EnableConfigurationProperties({CacheProperties.class})
@ConditionalOnProperty(name = "kunlun.cache.enabled", havingValue = "true")
public class CacheAutoConfiguration implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(CacheAutoConfiguration.class);
    private final CacheProperties cacheProperties;

    @Resource
    private ApplicationContext applicationContext;

    @Autowired
    public CacheAutoConfiguration(CacheProperties cacheProperties) {
        Assert.notNull(cacheProperties, "Parameter \"cacheProperties\" must not null. ");
        this.cacheProperties = cacheProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // simple
        List<SimpleConfig> simple = cacheProperties.getSimple();
        if (CollUtil.isNotEmpty(simple)) {
            for (SimpleConfig config : simple) {
                String cacheName = config.getName();
                CacheUtils.registerCache(cacheName, new SpringSimpleCache(config));
            }
        }
        // redis
        List<RedisConfig> redis = cacheProperties.getRedis();
        if (CollUtil.isNotEmpty(redis)) {
            RedisTemplate<String, Object> redisTemplate =
                    cast(applicationContext.getBean("redisTemplate", RedisTemplate.class));
            for (RedisConfig config : redis) {
                RedisCacheConfig cacheConfig = BeanUtils.beanToBean(config, RedisCacheConfig.class);
                RedisTemplateCache redisCache = new RedisTemplateCache(cacheConfig, redisTemplate);
                CacheUtils.registerCache(config.getName(), redisCache);
            }
        }
        // jdbc
        List<JdbcConfig> jdbc = cacheProperties.getJdbc();
        if (CollUtil.isNotEmpty(jdbc)) {
            JdbcTemplate jdbcTemplate = applicationContext.getBean("jdbcTemplate", JdbcTemplate.class);
            for (JdbcConfig config : jdbc) {
                JdbcCacheConfig cacheConfig = BeanUtils.beanToBean(config, JdbcCacheConfig.class);
                JdbcTemplateCache jdbcCache = new JdbcTemplateCache(cacheConfig, jdbcTemplate);
                CacheUtils.registerCache(config.getName(), jdbcCache);
            }
        }
    }

}
