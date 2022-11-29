package artoria.lock;

import artoria.util.Assert;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

/**
 * The zookeeper reentrant lock manager with transaction.
 * @see InterProcessMutex
 * @author Kahle
 */
public class ZookeeperWithTxLockManager extends ZookeeperLockManager {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperWithTxLockManager.class);

    public ZookeeperWithTxLockManager(String connectString) {

        super(connectString);
    }

    public ZookeeperWithTxLockManager(Map<String, InterProcessMutex> storage, String connectString) {

        super(storage, connectString);
    }

    public ZookeeperWithTxLockManager(CuratorFramework curator) {

        super(curator);
    }

    public ZookeeperWithTxLockManager(Map<String, InterProcessMutex> storage, CuratorFramework curator) {

        super(storage, curator);
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
                ZookeeperWithTxLockManager.super.unlock(lockName);
            }
        });
    }

}
