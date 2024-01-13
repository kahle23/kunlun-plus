package artoria.cache.support.spring;

import artoria.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

public class SpringCacheAdapter extends AbstractValueAdaptingCache {
    private final Cache cache;
    private final String name;

    public SpringCacheAdapter(String name, Cache cache) {

        this(name, cache, Boolean.TRUE);
    }

    public SpringCacheAdapter(String name, Cache cache, boolean allowNullValues) {
        super(allowNullValues);
        this.cache = cache;
        this.name = name;
    }

    @Override
    protected Object lookup(@Nullable Object key) {

        return cache.get(key);
    }

    @NonNull
    @Override
    public String getName() {

        return name;
    }

    @NonNull
    @Override
    public Object getNativeCache() {

        return cache;
    }

    @Override
    public <T> T get(@Nullable Object key, @Nullable Callable<T> valueLoader) {
        //fromStoreValue();toStoreValue();
        return cache.get(key, valueLoader);
    }

    @Override
    public void put(@Nullable Object key, Object value) {

        cache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(@Nullable Object key, Object value) {
        Object oldValue = cache.putIfAbsent(key, value);
        return toValueWrapper(oldValue);
    }

    @Override
    public void evict(@Nullable Object key) {

        cache.remove(key);
    }

    @Override
    public void clear() {

        cache.clear();
    }

}
