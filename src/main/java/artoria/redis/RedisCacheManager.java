package artoria.redis;

import artoria.cache.Cache;
import artoria.cache.Configuration;
import artoria.cache.SimpleCacheManager;
import artoria.lifecycle.LifecycleUtils;
import artoria.util.Assert;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class RedisCacheManager extends SimpleCacheManager {
    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MILLISECONDS;
    private static final long DEFAULT_TIMEOUT = 60*60*1000;
    private static RedisTemplate<Serializable, Object> defaultRedisTemplate;

    public static RedisTemplate<Serializable, Object> getDefaultRedisTemplate() {
        return defaultRedisTemplate;
    }

    public static void setDefaultRedisTemplate(RedisTemplate<Serializable, Object> defaultRedisTemplate) {
        RedisCacheManager.defaultRedisTemplate = defaultRedisTemplate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> Cache<K, V> createCache(String name, Configuration<K, V> configuration) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        if (configuration != null) {
            Assert.isInstanceOf(RedisCacheConfiguration.class, configuration
                    , "Parameter \"configuration\" must instance of \""
                            + RedisCacheConfiguration.class.getName() + "\". ");
        }
        else {
            configuration = new RedisCacheConfiguration(String.class, Object.class);
        }
        Assert.isAssignable(String.class, configuration.getKeyType()
                , "Parameter \"keyType\" in configuration must assignable from \"java.lang.String\". ");
        Cache<K, V> cache = manager.get(name);
        Assert.isNull(cache, "Parameter \"name\" already exist in this cache manager. ");
        RedisCacheConfiguration<K, V> config = (RedisCacheConfiguration) configuration;
        if (config.getSerializableRedisTemplate() == null) {
            config.setSerializableRedisTemplate(defaultRedisTemplate);
        }
        if (config.getTimeoutUnit() == null) {
            config.setTimeoutUnit(DEFAULT_TIMEOUT_UNIT);
        }
        if (config.getTimeout() == null) {
            config.setTimeout(DEFAULT_TIMEOUT);
        }
        cache = new RedisCache<K, V>(name, config);
        LifecycleUtils.initialize(cache);
        manager.put(name, cache);
        return cache;
    }

}
