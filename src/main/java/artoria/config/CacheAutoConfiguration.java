package artoria.config;

import artoria.cache.CacheManager;
import artoria.cache.RedisCacheManager;
import artoria.cache.SimpleCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

@Configuration
@AutoConfigureAfter(RedisTemplateAutoConfiguration.class)
@ConditionalOnClass(name = {"org.springframework.data.redis.core.RedisTemplate"})
public class CacheAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(CacheAutoConfiguration.class);
    private RedisTemplate<Serializable, Object> serializableRedisTemplate;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public CacheAutoConfiguration(RedisTemplate<Serializable, Object> serializableRedisTemplate) {
        this.serializableRedisTemplate = serializableRedisTemplate;
    }

    @Bean
    public CacheManager simpleCacheManager() {

        return new SimpleCacheManager();
    }

    @Bean
    public CacheManager redisCacheManager() {
        // TODO NOW
        return new RedisCacheManager();
    }

}
