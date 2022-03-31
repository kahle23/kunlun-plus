package artoria.lock;

import artoria.util.Assert;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * The redisson reentrant lock manager.
 * @see org.redisson.api.RLock
 * @author Kahle
 */
public class RedissonLockManager extends AbstractJavaLockManager {
    private static final Logger log = LoggerFactory.getLogger(RedissonLockManager.class);
    private final RedissonClient redisson;

    public RedissonLockManager(RedissonClient redisson) {

        this(Collections.<String, Lock>emptyMap(), redisson);
    }

    protected RedissonLockManager(Map<String, Lock> storage, RedissonClient redisson) {
        super(storage);
        Assert.notNull(redisson, "Parameter \"redisson\" must not null. ");
        Assert.notNull(storage, "Parameter \"storage\" must not null. ");
        this.redisson = redisson;
    }

    @Override
    protected Lock createLock(String lockName) {

        throw new UnsupportedOperationException();
    }

    @Override
    public Lock getLock(String lockName) {

        return redisson.getLock(lockName);
    }

    @Override
    public Object removeLock(String lockName) {

        throw new UnsupportedOperationException();
    }

}
