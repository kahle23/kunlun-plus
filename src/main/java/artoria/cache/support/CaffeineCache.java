package artoria.cache.support;

import artoria.cache.AbstractCache;
import artoria.data.Dict;
import artoria.data.ReferenceType;
import artoria.data.bean.BeanUtils;
import artoria.exception.ExceptionUtils;
import artoria.util.Assert;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static artoria.common.constant.Numbers.FIFTY;
import static artoria.common.constant.Numbers.ZERO;
import static artoria.util.ObjectUtils.cast;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class CaffeineCache extends AbstractCache {
    private final Cache<Object, Object> cache;

    @Deprecated
    public CaffeineCache(String name, long capacity, long timeToLive, long timeToIdle, ReferenceType referenceType) {
        boolean flag = capacity <= ZERO && timeToLive <= ZERO && timeToIdle <= ZERO;
        Assert.isFalse(flag,
                "A parameter must have a value in \"capacity\", \"timeToLive\", \"timeToIdle\". "
        );
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        if (timeToIdle > ZERO) { builder.expireAfterAccess(timeToIdle, MILLISECONDS); }
        if (timeToLive > ZERO) { builder.expireAfterWrite(timeToLive, MILLISECONDS); }
        if (capacity > ZERO) { builder.maximumSize(capacity); }
        builder.initialCapacity(FIFTY);
        if (ReferenceType.WEAK.equals(referenceType)) {
            builder.weakValues();
        }
        if (ReferenceType.SOFT.equals(referenceType)) {
            builder.softValues();
        }
        this.cache = builder.build();
    }


    public CaffeineCache() {

        this((Object) null);
    }

    public CaffeineCache(Object cacheConfig) {
        // Process the cache config.
        Assert.notNull(cacheConfig, "Parameter \"cacheConfig\" must not null. ");
        Dict config = Dict.of(BeanUtils.beanToMap(cacheConfig));
        // Build caffeine object.
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        builder.initialCapacity(FIFTY);
        // Process the capacity.
        Long capacity = config.getLong("capacity");
        if (capacity != null && capacity > ZERO) { builder.maximumSize(capacity); }
        // Process the timeToLive and the timeToLiveUnit.
        TimeUnit timeToLiveUnit = config.get("timeToLiveUnit", TimeUnit.class);
        Long timeToLive = config.getLong("timeToLive");
        if (timeToLive != null) {
            Assert.notNull(timeToLiveUnit, "Parameter \"timeToLiveUnit\" must not null. ");
            Assert.isTrue(timeToLive > ZERO
                    , "Parameter \"timeToLive\" must to be greater than zero. ");
            builder.expireAfterWrite(timeToLive, timeToLiveUnit);
        }
        // Process the timeToIdle and the timeToIdleUnit.
        TimeUnit timeToIdleUnit = config.get("timeToIdleUnit", TimeUnit.class);
        Long timeToIdle = config.getLong("timeToIdle");
        if (timeToIdle != null) {
            Assert.notNull(timeToIdleUnit, "Parameter \"timeToIdleUnit\" must not null. ");
            Assert.isTrue(timeToIdle > ZERO
                    , "Parameter \"timeToIdle\" must to be greater than zero. ");
            builder.expireAfterAccess(timeToIdle, timeToIdleUnit);
        }
        // Process the reference type (default null).
        ReferenceType referenceType = config.get("referenceType", ReferenceType.class);
        if (ReferenceType.WEAK.equals(referenceType)) {
            builder.weakValues();
        }
        else if (ReferenceType.SOFT.equals(referenceType)) {
            builder.softValues();
        }
        else { /* Do nothing */ }
        // Process the result
        this.cache = builder.build();
    }

    public CaffeineCache(Cache<Object, Object> cache) {
        Assert.notNull(cache, "Parameter \"cache\" must not null. ");
        this.cache = cache;
    }

    @Override
    public Cache<Object, Object> getNative() {

        return cache;
    }

    @Override
    public <T> T get(Object key, Callable<T> callable) {
        Assert.notNull(callable, "Parameter \"callable\" must not null. ");
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        Object value = get(key);
        if (value != null) { return cast(value); }
        synchronized (String.valueOf(key).intern()) {
            // Try to get again.
            if ((value = get(key)) != null) {
                return cast(value);
            }
            // Try to call.
            try {
                value = callable.call();
            }
            catch (Exception e) {
                throw ExceptionUtils.wrap(e);
            }
            // Cache the result.
            if (value != null) {
                put(key, value);
            }
        }
        return cast(value);
    }

    @Override
    public Object get(Object key) {

        return cache.getIfPresent(key);
    }

    @Override
    public long size() {

        return cache.estimatedSize();
    }

    @Override
    public Object put(Object key, Object value) {
        cache.put(key, value);
        return null;
    }

    @Override
    public Object put(Object key, Object value, long timeToLive, TimeUnit timeUnit) {

        throw new UnsupportedOperationException();
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {

        throw new UnsupportedOperationException();
    }

    @Override
    public Object putIfAbsent(Object key, Object value, long timeToLive, TimeUnit timeUnit) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<?, ?> map) {

        cache.putAll(map);
    }

    @Override
    public boolean expire(Object key, long timeToLive, TimeUnit timeUnit) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean expireAt(Object key, Date date) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean persist(Object key) {

        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        cache.invalidate(key);
        return null;
    }

    @Override
    public void removeAll(Collection<?> keys) {

        cache.invalidateAll(keys);
    }

    @Override
    public void clear() {

        cache.invalidateAll();
    }

    @Override
    public Map<Object, Object> entries() {

        return cache.asMap();
    }

}
