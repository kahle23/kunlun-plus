package artoria.config;

import artoria.cache.RedisCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnClass(name = {"org.springframework.data.redis.core.RedisTemplate"})
public class RedisTemplateAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(RedisTemplateAutoConfiguration.class);
    private RedisConnectionFactory redisConnectionFactory;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public RedisTemplateAutoConfiguration(RedisConnectionFactory redisConnectionFactory) {
        // TODO Now
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

}
