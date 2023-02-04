package artoria.lock;

import artoria.util.Assert;
import io.seata.core.context.RootContext;
import io.seata.tm.api.transaction.TransactionHookAdapter;
import io.seata.tm.api.transaction.TransactionHookManager;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The zookeeper reentrant lock manager with seata transaction.
 * @see InterProcessMutex
 * @author Kahle
 */
public class ZookeeperWithSeataTxLockManager extends ZookeeperLockManager {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperWithSeataTxLockManager.class);

    public ZookeeperWithSeataTxLockManager(String connectString) {

        super(connectString);
    }

    public ZookeeperWithSeataTxLockManager(Map<String, InterProcessMutex> storage, String connectString) {

        super(storage, connectString);
    }

    public ZookeeperWithSeataTxLockManager(CuratorFramework curator) {

        super(curator);
    }

    public ZookeeperWithSeataTxLockManager(Map<String, InterProcessMutex> storage, CuratorFramework curator) {

        super(storage, curator);
    }

    @Override
    public void unlock(final String lockName) {
        Assert.notBlank(lockName, "Parameter \"lockName\" must not blank. ");
        // No active transaction.
        if (!RootContext.inGlobalTransaction()) {
            log.info("Unlock \"{}\" without seata tx.", lockName);
            super.unlock(lockName); return;
        }
        // Have active transaction.
        TransactionHookManager.registerHook(new TransactionHookAdapter() {
            @Override
            public void afterCompletion() {
            log.info("Unlock \"{}\" with seata tx.", lockName);
            ZookeeperWithSeataTxLockManager.super.unlock(lockName);
            }
        });
    }

}
