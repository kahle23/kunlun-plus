package artoria.config;

import artoria.cache.CacheManager;
import artoria.cache.SimpleCacheManager;
import artoria.redis.RedisCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;

@Configuration
@AutoConfigureAfter(org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
public class RedisAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(RedisAutoConfiguration.class);
    private RedisConnectionFactory redisConnectionFactory;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public RedisAutoConfiguration(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<Serializable, Object> serializableRedisTemplate() {
        RedisTemplate<Serializable, Object> serializableRedisTemplate = new RedisTemplate<Serializable, Object>();
        serializableRedisTemplate.setConnectionFactory(redisConnectionFactory);
        RedisSerializer<?> serializer = new GenericToStringSerializer<Serializable>(Serializable.class);
        serializableRedisTemplate.setKeySerializer(serializer);
        serializableRedisTemplate.setValueSerializer(serializer);
        RedisCacheManager.setDefaultRedisTemplate(serializableRedisTemplate);
        return serializableRedisTemplate;
    }

    @Bean
    public CacheManager simpleCacheManager() {
        return new SimpleCacheManager();
    }

    @Bean
    public CacheManager redisCacheManager() {
        return new RedisCacheManager();
    }

}
