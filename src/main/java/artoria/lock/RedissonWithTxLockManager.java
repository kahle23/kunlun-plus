package artoria.lock;

import artoria.util.Assert;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * The redisson reentrant lock manager with transaction.
 * @see org.redisson.api.RLock
 * @author Kahle
 */
public class RedissonWithTxLockManager extends RedissonLockManager {
    private static final Logger log = LoggerFactory.getLogger(RedissonWithTxLockManager.class);

    public RedissonWithTxLockManager(RedissonClient redisson) {

        super(redisson);
    }

    protected RedissonWithTxLockManager(Map<String, Lock> storage, RedissonClient redisson) {

        super(storage, redisson);
    }

    @Override
    public void unlock(final String lockName) {
        Assert.notBlank(lockName, "Parameter \"lockName\" must not blank. ");
        // No active transaction.
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            log.info("Unlock \"{}\" without tx.", lockName);
            super.unlock(lockName); return;
        }
        // Have active transaction.
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                log.info("Unlock \"{}\" with tx.", lockName);
                RedissonWithTxLockManager.super.unlock(lockName);
            }
        });
    }

}
