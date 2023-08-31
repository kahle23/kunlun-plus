package artoria.cache;

import artoria.cache.support.SpringSimpleCache;
import artoria.data.Dict;
import artoria.data.ReferenceType;
import artoria.util.ThreadUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SpringSimpleCacheTest {
    private static Logger log = LoggerFactory.getLogger(SpringSimpleCacheTest.class);

    @Ignore
    @Test
    public void testConcurrentModificationException() {
        final SpringSimpleCache cache = new SpringSimpleCache("TEST"
                , Dict.of("referenceType", ReferenceType.SOFT));
//        cache.setRecordLog(true);
        long start = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000000; i++) {
                    cache.put(i, i, 100, TimeUnit.MILLISECONDS);
                    ThreadUtils.sleepQuietly(1);
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadUtils.sleepQuietly(100);
                for (int i = 0; i < 1000000; i++) {
                    cache.get(i);
                }
            }
        }).start();
        for (int i = 0; i < 1000000; i++) {
            cache.put(">> "+i, i, 10, TimeUnit.MILLISECONDS);
        }
        long end = System.currentTimeMillis();
        log.info("Time spent: {}", (end - start) / 1000);
    }

}
