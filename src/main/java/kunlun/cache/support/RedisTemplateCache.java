/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.cache.support;

import kunlun.cache.AbstractCache;
import kunlun.data.bean.BeanUtils;
import kunlun.util.Assert;
import kunlun.util.CollectionUtils;
import kunlun.util.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static kunlun.common.constant.Symbols.COLON;
import static kunlun.util.ObjectUtils.cast;

public class RedisTemplateCache extends AbstractCache {
    private static final Logger log = LoggerFactory.getLogger(RedisTemplateCache.class);
    private static final String KEY_PREFIX = "_cache:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisCacheConfig cacheConfig;

    public RedisTemplateCache(RedisCacheConfig cacheConfig) {

        this(cacheConfig, null);
    }

    public RedisTemplateCache(RedisCacheConfig cacheConfig, RedisTemplate<String, Object> redisExecutor) {
        // Validate the cache config.
        Assert.notNull(cacheConfig, "Parameter \"cacheConfig\" must not null. ");
        Assert.notBlank(cacheConfig.getName(), "Parameter \"cacheConfig.name\" must not blank. ");
        this.cacheConfig = (cacheConfig = BeanUtils.beanToBean(cacheConfig, RedisCacheConfig.class));
        // Process redis executor.
        if (redisExecutor != null) { cacheConfig.setRedisExecutor(redisExecutor); }
        Assert.notNull(cacheConfig.getRedisExecutor(), "Parameter \"redisExecutor\" must not null. ");
        this.redisTemplate = cast(cacheConfig.getRedisExecutor());
    }

    public RedisCacheConfig getConfig() {

        return cacheConfig;
    }

    @Override
    protected String getLockManager() {

        return getConfig().getLockManager();
    }

    protected String getRedisKey(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        return KEY_PREFIX + getConfig().getName() + COLON + key;
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
        if (getConfig().getTimeToLive() != null && getConfig().getTimeToLiveUnit() != null) {
            return put(key, value, getConfig().getTimeToLive(), getConfig().getTimeToLiveUnit());
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
        if (getConfig().getTimeToLive() != null && getConfig().getTimeToLiveUnit() != null) {
            return putIfAbsent(key, value, getConfig().getTimeToLive(), getConfig().getTimeToLiveUnit());
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
        if (getConfig().getTimeToLive() != null && getConfig().getTimeToLiveUnit() != null) {
            for (Map.Entry<String, Object> entry : newMap.entrySet()) {
                String key = entry.getKey();
                expire(key, getConfig().getTimeToLive(), getConfig().getTimeToLiveUnit());
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
