package artoria.cache;

import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class RedisCacheConfiguration<K, V> extends SimpleConfiguration<K, V> {
    private RedisTemplate<Serializable, Object> serializableRedisTemplate;
    private TimeUnit timeoutUnit;
    private Long timeout;

    public RedisCacheConfiguration(Class<K> keyType, Class<V> valueType) {
        super(keyType, valueType);
    }

    public RedisTemplate<Serializable, Object> getSerializableRedisTemplate() {
        return serializableRedisTemplate;
    }

    public void setSerializableRedisTemplate(RedisTemplate<Serializable, Object> serializableRedisTemplate) {
        this.serializableRedisTemplate = serializableRedisTemplate;
    }

    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    public void setTimeoutUnit(TimeUnit timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

}
