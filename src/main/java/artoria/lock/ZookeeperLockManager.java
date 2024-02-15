package artoria.lock;

import artoria.common.constant.Numbers;
import artoria.exception.ExceptionUtils;
import artoria.util.Assert;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static artoria.common.constant.Symbols.SLASH;

/**
 * The zookeeper reentrant lock manager.
 * @see org.apache.curator.framework.recipes.locks.InterProcessMutex
 * @author Kahle
 */
public class ZookeeperLockManager implements LockManager {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperLockManager.class);
    private static final String PATH_PREFIX = "/lock/reentrant";
    private final Map<String, InterProcessMutex> storage;
    private final CuratorFramework curator;

    private static CuratorFramework createCurator(String connectString) {
        Assert.notBlank(connectString, "Parameter \"connectString\" must not blank. ");
        CuratorFramework curator = CuratorFrameworkFactory.builder()
                .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
                .sessionTimeoutMs(30000)
                .connectionTimeoutMs(30000)
                .connectString(connectString)
                .build();
        //ConnectionStateListener listener = new SimpleConnectionStateListener("zk-" + connectString);
        //curator.getConnectionStateListenable().addListener(listener);
        curator.start();
        return curator;
    }

    public ZookeeperLockManager(String connectString) {

        this(createCurator(connectString));
    }

    public ZookeeperLockManager(Map<String, InterProcessMutex> storage,
                                String connectString) {

        this(storage, createCurator(connectString));
    }

    public ZookeeperLockManager(CuratorFramework curator) {

        this(new ConcurrentHashMap<String, InterProcessMutex>(), curator);
    }

    public ZookeeperLockManager(Map<String, InterProcessMutex> storage,
                                CuratorFramework curator) {
        Assert.notNull(storage, "Parameter \"storage\" must not null. ");
        Assert.notNull(curator, "Parameter \"curator\" must not null. ");
        this.storage = storage;
        this.curator = curator;
    }

    @Override
    public InterProcessMutex getLock(String lockName) {
        Assert.notBlank(lockName, "Parameter \"lockName\" must not blank. ");
        InterProcessMutex lock = storage.get(lockName);
        if (lock != null) { return lock; }
        synchronized (this) {
            if ((lock = storage.get(lockName)) != null) {
                return lock;
            }
            String path = PATH_PREFIX + SLASH + lockName;
            storage.put(lockName, lock = new InterProcessMutex(curator, path));
        }
        return lock;
    }

    @Override
    public Object removeLock(String lockName) {

        return storage.remove(lockName);
    }

    @Override
    public void lock(String lockName) {
        try {
            getLock(lockName).acquire();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public void unlock(String lockName) {
        try {
            getLock(lockName).release();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public void lockInterruptibly(String lockName) throws InterruptedException {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(String lockName) {

        return tryLock(lockName, Numbers.FIVE_HUNDRED, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryLock(String lockName, long time, TimeUnit unit) {
        try {
            return getLock(lockName).acquire(time, unit);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}
