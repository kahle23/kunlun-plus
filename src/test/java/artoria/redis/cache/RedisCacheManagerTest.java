package artoria.redis.cache;

import artoria.cache.Cache;
import artoria.cache.CacheManager;
import artoria.config.RedisAutoConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RedisAutoConfiguration.class, org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class})
@Import({RedisAutoConfiguration.class})
public class RedisCacheManagerTest {

    @Autowired
    private CacheManager redisCacheManager;

    @Test
    public void test1() {
//        CacheManager manager = new RedisCacheManager();
//        manager.createCache("RedisCacheManagerTest.test1()");
        redisCacheManager.createCache("RedisCacheManagerTest.test1()");
        Cache<String, Object> cache = redisCacheManager.getCache("RedisCacheManagerTest.test1()");
        for (int i = 0; i < 100; i++) {
            cache.put("test1-cache-key-" + i, "test1-cache-value" + i);
        }
        for (int i = 0; i < 100; i++) {
            System.out.println(cache.get("test1-cache-key-" + i));
        }
    }

    @Test
    public void test2() {
//        CacheManager manager = new RedisCacheManager();
        redisCacheManager.createCache("RedisCacheManagerTest.test1()");
        Cache<String, Object> cache = redisCacheManager.getCache("RedisCacheManagerTest.test1()");
        System.out.println(cache.size());
        cache.clear();
        System.out.println(cache.size());
    }

}
