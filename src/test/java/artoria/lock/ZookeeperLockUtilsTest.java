package artoria.lock;

import artoria.util.ThreadUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import static artoria.common.constant.Numbers.ZERO;

@Ignore
public class ZookeeperLockUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperLockUtilsTest.class);
    private volatile Integer num = 100;
    private final Integer threadNum = 50;
    private ExecutorService threadPool;
    private String managerName;

    @Before
    public void init() {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(threadNum, threadNum, ZERO, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), threadFactory);
        managerName = "zk";
        LockUtils.registerManager(managerName, new ZookeeperLockManager("127.0.0.1:2181,127.0.0.2:2181,127.0.0.3:2181"));
    }

    @After
    public void destroy() {

        threadPool.shutdown();
    }

    private void bizCalc() {
        num = num + 20;
        ThreadUtils.sleepQuietly(50);
        num = num - 40;
        ThreadUtils.sleepQuietly(100);
        num = num + 20;
        num = num + 10;
        ThreadUtils.sleepQuietly(150);
        num = num - 20;
        num = num + 10;
        num = num - 1;
    }

    @Test
    public void testLock() {
        final String lockName = "testLock";
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                long millis = System.currentTimeMillis();
                for (int j = 0; j < 1000000; j++) {
                    if (num <= 0) { continue; }
                    LockUtils.lock(managerName, lockName);
                    try {
                        log.info(">> {} lock", threadName);
                        if (num > 0) {
                            bizCalc();
                            log.info("|| {} | {}", threadName, num);
                        }
                    }
                    finally {
                        log.info("<< {} unlock\n", threadName);
                        LockUtils.unlock(managerName, lockName);
                        ThreadUtils.sleepQuietly(100);
                    }
                }
                log.info("{}: {}ms", threadName, System.currentTimeMillis() - millis);
            }
        };
        for (int i = 0; i < threadNum; i++) {
            threadPool.submit(runnable);
        }
        ThreadUtils.sleepQuietly(100000);
    }

    @Test
    public void testTryLock() {
        final String lockName = "testTryLock";
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                long millis = System.currentTimeMillis();
                for (int j = 0; j < 1000000; j++) {
                    if (num <= 0) { continue; }
                    boolean tryLock = LockUtils.tryLock(managerName, lockName, 500, TimeUnit.MILLISECONDS);
                    if (!tryLock) { continue; }
                    try {
                        log.info(">> {} tryLock {}", threadName, tryLock);
                        if (num > 0) {
                            bizCalc();
                            log.info("|| {} | {}", threadName, num);
                        }
                    }
                    finally {
                        log.info("<< {} unlock\n", threadName);
                        LockUtils.unlock(managerName, lockName);
                        ThreadUtils.sleepQuietly(100);
                    }
                }
                log.info("{}: {}ms", threadName, System.currentTimeMillis() - millis);
            }
        };
        for (int i = 0; i < threadNum; i++) {
            threadPool.submit(runnable);
        }
        ThreadUtils.sleepQuietly(100000);
    }

}
