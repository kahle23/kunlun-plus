package artoria.cache;

import artoria.cache.support.CaffeineCache;
import artoria.data.ReferenceType;
import artoria.util.ThreadUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static artoria.common.constant.Numbers.*;

public class CaffeineCacheTest {
    private static Logger log = LoggerFactory.getLogger(CaffeineCacheTest.class);
    private static Cache cache1;
    private static Cache cache2;
    private static Cache cache3;

    static {
        cache1 = new CaffeineCache("cache1", ZERO, ONE_THOUSAND, ZERO, ReferenceType.WEAK);
        cache2 = new CaffeineCache("cache2", ZERO, ZERO, ONE_THOUSAND, ReferenceType.WEAK);
        cache3 = new CaffeineCache("cache3", TWO, ZERO, ZERO, ReferenceType.WEAK);
    }

    @Test
    public void test1() {
        cache1.put("test1", "test1-value");
        log.info("{}", cache1.get("test1"));
        ThreadUtils.sleepQuietly(ONE_THOUSAND);
        log.info("{}", cache1.get("test1"));
    }

    @Test
    public void test2() {
        cache2.put("test2", "test2-value");
        ThreadUtils.sleepQuietly(EIGHT_HUNDRED);
        log.info("{}", cache2.get("test2"));
        ThreadUtils.sleepQuietly(EIGHT_HUNDRED);
        log.info("{}", cache2.get("test2"));
        ThreadUtils.sleepQuietly(ONE_THOUSAND);
        log.info("{}", cache2.get("test2"));
    }

    @Test
    public void test3() {
        cache3.put("test3-1", "test3-1-value");
        cache3.put("test3-2", "test3-2-value");
        log.info("{}", cache3.get("test3-1"));
        log.info("{}", cache3.get("test3-2"));
        cache3.put("test3-3", "test3-3-value");
        log.info("----");
        ThreadUtils.sleepQuietly(ONE_THOUSAND);
        log.info("{}", cache3.get("test3-3"));
        log.info("{}", cache3.get("test3-2"));
        log.info("{}", cache3.get("test3-1"));
    }

}
