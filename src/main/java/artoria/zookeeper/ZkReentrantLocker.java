package artoria.zookeeper;

import artoria.exception.ExceptionUtils;
import artoria.lock.Locker;
import artoria.util.Assert;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static artoria.common.Constants.SLASH;

/**
 * Zookeeper reentrant mutex lock operator.
 * @see org.apache.curator.framework.recipes.locks.InterProcessMutex
 * @see artoria.lock.Locker
 * @author Kahle
 */
public class ZkReentrantLocker implements Locker {
    private static Logger log = LoggerFactory.getLogger(ZkReentrantLocker.class);
    private static final String REENTRANT_LOCK = "/lock/mutex/reentrant";
    private final CuratorFramework curator;
    private TimeUnit zkTimeUnit = TimeUnit.MILLISECONDS;
    private long zkTimeout = 500;

    public ZkReentrantLocker(String connectString) {
        Assert.notBlank(connectString, "Parameter \"connectString\" must not blank. ");
        this.curator = CuratorFrameworkFactory.builder()
                .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
                .sessionTimeoutMs(30000)
                .connectionTimeoutMs(30000)
                .connectString(connectString)
                .build();
        ConnectionStateListener listener = new ZkConnectionStateListener("ZK-" + connectString);
        this.curator.getConnectionStateListenable().addListener(listener);
        this.curator.start();
        log.info("Initialize ZkReentrantLockFactory success, and zookeeper connect address is \"{}\".", connectString);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (curator == null) { return; }
                curator.close();
                log.info("Release curator object \"{}\" success in shutdown hook. ", curator);
            }
        }));
    }

    public ZkReentrantLocker(CuratorFramework curator) {
        Assert.notNull(curator, "Parameter \"curator\" must not null. ");
        this.curator = curator;
    }

    public long getZkTimeout() {

        return this.zkTimeout;
    }

    public TimeUnit getZkTimeUnit() {

        return this.zkTimeUnit;
    }

    public void setZkTimeout(long zkTimeout, TimeUnit zkTimeUnit) {
        Assert.state(zkTimeout > 0, "Parameter \"zkTimeout\" must greater than 0. ");
        Assert.notNull(zkTimeUnit, "Parameter \"zkTimeUnit\" must not null. ");
        this.zkTimeout = zkTimeout;
        this.zkTimeUnit = zkTimeUnit;
    }

    private InterProcessMutex takeLock(String lockName) {
        Assert.notBlank(lockName, "Parameter \"lockName\" must not blank. ");
        String path = REENTRANT_LOCK + SLASH + lockName;
        return new InterProcessMutex(this.curator, path);
    }

    @Override
    public void lock(String lockName) {
        try {
            this.takeLock(lockName).acquire();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public void unlock(String lockName) {
        try {
            this.takeLock(lockName).release();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public void lockInterruptibly(String lockName) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(String lockName) {
        try {
            return this.tryLock(lockName, this.zkTimeout, this.zkTimeUnit);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public boolean tryLock(String lockName, long time, TimeUnit unit) {
        try {
            return this.takeLock(lockName).acquire(time, unit);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
