package artoria.redis;

import artoria.cache.Cache;
import artoria.cache.CacheLoader;
import artoria.util.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static artoria.common.Constants.*;

public class RedisCache<K, V> implements Cache<K, V> {
    private final RedisTemplate<Serializable, Object> serializableRedisTemplate;
    private static final String CACHE_KEY_PREFIX = "REDIS_CACHE_";
    private final ValueOperations<Serializable, Object> opsForValue;
    private final CacheLoader<K, V> cacheLoader;
    private TimeUnit timeoutUnit;
    private long timeout;
    private final String name;

    @SuppressWarnings("unchecked")
    <C extends RedisCacheConfiguration<K, V>> RedisCache(String name, C configuration) {
        Assert.notNull(configuration, "Parameter \"configuration\" must not null. ");
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        this.serializableRedisTemplate = configuration.getSerializableRedisTemplate();
        Assert.notNull(serializableRedisTemplate,
                "Object \"serializableRedisTemplate\" in parameter \"configuration\" must not null. ");
        this.opsForValue = serializableRedisTemplate.opsForValue();
        this.cacheLoader = configuration.getCacheLoader();
        this.timeoutUnit = configuration.getTimeoutUnit();
        this.timeout = configuration.getTimeout();
        this.name = name;
    }

    @Override
    public String getName() {

        return this.name;
    }

    private String handleKey(K key) {

        return CACHE_KEY_PREFIX + this.getName() + UNDERLINE + key;
    }

    private V takeValue(K key) {
        if (key == null) { return null; }
        return (V) opsForValue.get(this.handleKey(key));
    }

    @Override
    public V get(K key) {
        V val;
        if ((val = this.takeValue(key)) == null) {
            this.load(key, false);
            val = this.takeValue(key);
        }
        return val;
    }

    @Override
    public void put(K key, V value) {
        String realKey = this.handleKey(key);
        opsForValue.set(realKey, value, timeout, timeoutUnit);
    }

    @Override
    public V putAndGet(K key, V value) {
        V oldVal = this.takeValue(key);
        this.put(key, value);
        return oldVal;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V val;
        if ((val = this.takeValue(key)) == null) {
            val = this.putAndGet(key, value);
        }
        return val;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        if (MapUtils.isEmpty(map)) {
            return;
        }
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean remove(K key) {
        if (key == null) { return false; }
        String realKey = this.handleKey(key);
        Boolean flag = serializableRedisTemplate.delete(realKey);
        return flag != null ? flag : false;
    }

    @Override
    public boolean remove(K key, V oldValue) {
        if (key == null) { return false; }
        Object curValue = this.takeValue(key);
        boolean flag = !ObjectUtils.equals(curValue, oldValue);
        flag = flag || (curValue == null && !this.containsKey(key));
        if (flag) { return false; }
        this.remove(key);
        return true;
    }

    @Override
    public void remove(Collection<? extends K> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        for (K key : keys) {
            this.remove(key);
        }
    }

    @Override
    public V removeAndGet(K key) {
        V oldVal = this.takeValue(key);
        this.remove(key);
        return oldVal;
    }

    @Override
    public boolean containsKey(K key) {
        String realKey = this.handleKey(key);
        Boolean flag = serializableRedisTemplate.hasKey(realKey);
        return flag != null ? flag : false;
    }

    @Override
    public void clear() {
        String pattern = CACHE_KEY_PREFIX + this.getName() + ASTERISK;
        Set<Serializable> keys = serializableRedisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        for (Serializable key : keys) {
            if (key == null) { continue; }
            serializableRedisTemplate.delete(key);
        }
    }

    @Override
    public int size() {
        String pattern = CACHE_KEY_PREFIX + this.getName() + ASTERISK;
        Set<Serializable> keys = serializableRedisTemplate.keys(pattern);
        return keys != null ? keys.size() : 0;
    }

    @Override
    public void load(K key, boolean coverExisted) {
        if (cacheLoader == null) { return; }
        if (this.containsKey(key) && !coverExisted) {
            return;
        }
        V val = cacheLoader.load(key);
        this.put(key, val);
    }

    @Override
    public void load(Iterable<? extends K> keys, boolean coverExisted) {
        if (cacheLoader == null) { return; }
        Map<K, V> loadMap = cacheLoader.loadAll(keys);
        for (Map.Entry<K, V> entry : loadMap.entrySet()) {
            if (entry == null) { continue; }
            K key = entry.getKey();
            V val = entry.getValue();
            if (key == null) { continue; }
            String keyStr = this.handleKey(key);
            if (this.containsKey((K) keyStr) && !coverExisted) {
                continue;
            }
            this.put(key, val);
        }
    }

    @Override
    public void refresh() {
        if (cacheLoader == null) { return; }
        if (serializableRedisTemplate == null) {
            return;
        }
        String prefix = CACHE_KEY_PREFIX + this.getName() + UNDERLINE;
        String pattern = prefix + ASTERISK;
        Set<Serializable> keys = serializableRedisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        for (Serializable key : keys) {
            if (key == null) { continue; }
            String keyStr = (String) key;
            if (!keyStr.startsWith(prefix)) {
                continue;
            }
            keyStr = StringUtils.replace(keyStr, prefix, EMPTY_STRING);
            this.load((K) keyStr, true);
        }
    }

    @Override
    public Object getOriginal() {

        return serializableRedisTemplate;
    }

}
