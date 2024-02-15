package artoria.cache.support;

import artoria.cache.AbstractCache;
import artoria.data.Dict;
import artoria.data.bean.BeanUtils;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.MapUtils;
import artoria.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static artoria.common.constant.Symbols.COLON;

public class RedisTemplateCache extends AbstractCache {
    private static final Logger log = LoggerFactory.getLogger(RedisTemplateCache.class);
    private static final String KEY_PREFIX = "_cache:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final String lockManager;
    private final String name;
    protected final TimeUnit timeToLiveUnit;
    protected final Long timeToLive;

    public RedisTemplateCache(String name, Object cacheConfig) {

        this(name, cacheConfig, null);
    }

    public RedisTemplateCache(String name, Object cacheConfig, RedisTemplate<String, Object> redisTemplate) {
        Assert.notBlank(name, "Parameter \"name\" must not null. ");
        this.name = name;
        Dict config = Dict.of(BeanUtils.beanToMap(cacheConfig));
        if (redisTemplate == null) {
            redisTemplate = ObjectUtils.cast(config.get("redisTemplate"));
        }
        Assert.notNull(redisTemplate, "Parameter \"redisTemplate\" must not null. ");
        this.redisTemplate = redisTemplate;
        this.lockManager = config.getString("lockManager");
        // Process the timeToLive and the timeToLiveUnit.
        this.timeToLiveUnit = config.get("timeToLiveUnit", TimeUnit.class);
        this.timeToLive = config.getLong("timeToLive");
    }

    public String getName() {

        return name;
    }

    @Override
    protected String getLockManager() {

        return lockManager;
    }

    protected String getRedisKey(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        return KEY_PREFIX + getName() + COLON + key;
    }

    @Override
    public Object getNative() {

        return redisTemplate;
    }

    @Override
    public Object get(Object key) {

        return redisTemplate.opsForValue().get(getRedisKey(key));
    }

    @Override
    public boolean containsKey(Object key) {
        Boolean hasKey = redisTemplate.hasKey(getRedisKey(key));
        return hasKey != null && hasKey;
    }

    @Override
    public Object put(Object key, Object value) {
        // if timeToLive is not null
        if (timeToLive != null && timeToLiveUnit != null) {
            return put(key, value, timeToLive, timeToLiveUnit);
        }
        // if timeToLive is null
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        return opsForValue.getAndSet(getRedisKey(key), value);
    }

    @Override
    public Object put(Object key, Object value, long timeToLive, TimeUnit timeUnit) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        String redisKey = getRedisKey(key);
        Object oldValue = opsForValue.get(redisKey);
        opsForValue.set(redisKey, value, timeToLive, timeUnit);
        return oldValue;
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        // if timeToLive is not null
        if (timeToLive != null && timeToLiveUnit != null) {
            return putIfAbsent(key, value, timeToLive, timeToLiveUnit);
        }
        // if timeToLive is null
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        opsForValue.setIfAbsent(getRedisKey(key), value);
        return null;
    }

    @Override
    public Object putIfAbsent(Object key, Object value, long timeToLive, TimeUnit timeUnit) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        String redisKey = getRedisKey(key);
        opsForValue.setIfAbsent(redisKey, value);
        redisTemplate.expire(redisKey, timeToLive, timeUnit);
        return null;
    }

    @Override
    public void putAll(Map<?, ?> map) {
        if (MapUtils.isEmpty(map)) { return; }
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        Map<String, Object> newMap = new LinkedHashMap<String, Object>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            Object key = entry.getKey();
            newMap.put(getRedisKey(key), value);
        }
        opsForValue.multiSet(newMap);
        // expire
        if (timeToLive != null && timeToLiveUnit != null) {
            for (Map.Entry<String, Object> entry : newMap.entrySet()) {
                String key = entry.getKey();
                expire(key, timeToLive, timeToLiveUnit);
            }
        }
    }

    @Override
    public boolean expire(Object key, long timeToLive, TimeUnit timeUnit) {
        Boolean expire = redisTemplate.expire(getRedisKey(key), timeToLive, timeUnit);
        return expire != null && expire;
    }

    @Override
    public boolean expireAt(Object key, Date date) {
        Boolean expireAt = redisTemplate.expireAt(getRedisKey(key), date);
        return expireAt != null && expireAt;
    }

    @Override
    public boolean persist(Object key) {
        Boolean persist = redisTemplate.persist(getRedisKey(key));
        return persist != null && persist;
    }

    @Override
    public Object remove(Object key) {
        redisTemplate.delete(getRedisKey(key));
        return null;
    }

    @Override
    public void removeAll(Collection<?> keys) {
        if (CollectionUtils.isEmpty(keys)) { return; }
        List<String> redisKeys = new ArrayList<String>();
        for (Object key : keys) {
            redisKeys.add(getRedisKey(key));
        }
        redisTemplate.delete(redisKeys);
    }

}
