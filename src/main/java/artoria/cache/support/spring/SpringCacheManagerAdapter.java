package artoria.cache.support.spring;

import artoria.cache.CacheUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static artoria.common.Constants.TWENTY;

public class SpringCacheManagerAdapter implements CacheManager {
    private final Map<String, Cache> cacheMap;

    public SpringCacheManagerAdapter() {

        this.cacheMap = new ConcurrentHashMap<String, Cache>(TWENTY);
    }

    @Override
    public Cache getCache(@Nullable String name) {
        // Resolves whether the name contains a configuration.
        // Like "test-cache:test1[type=0&timeToLive=86000&timeToIdle=5000]".
        // Multi-level cache configuration is complex, so annotations are not recommended.
        Cache cache = cacheMap.get(name);
        if (cache != null) {
            return cache;
        }
        else {
            // Fully synchronize now for missing cache creation...
            synchronized (cacheMap) {
                cache = cacheMap.get(name);
                if (cache != null) {
                    return cache;
                }
                //
                artoria.cache.Cache cache1 = CacheUtils.getCache(name);
                cache = new SpringCacheAdapter(cache1);
                cacheMap.put(name, cache);
                return cache;
            }
        }
    }

    @NonNull
    @Override
    public Collection<String> getCacheNames() {

        return CacheUtils.getCacheNames();
    }

}
