/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.lock.support;

import io.seata.core.context.RootContext;
import io.seata.tm.api.transaction.TransactionHookAdapter;
import io.seata.tm.api.transaction.TransactionHookManager;
import kunlun.util.Assert;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * The redisson reentrant lock manager with seata transaction.
 * @see org.redisson.api.RLock
 * @author Kahle
 */
public class RedissonWithSeataTxLockManager extends RedissonLockManager {
    private static final Logger log = LoggerFactory.getLogger(RedissonWithSeataTxLockManager.class);

    public RedissonWithSeataTxLockManager(RedissonClient redisson) {

        super(redisson);
    }

    protected RedissonWithSeataTxLockManager(Map<String, Lock> storage, RedissonClient redisson) {

        super(storage, redisson);
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
                RedissonWithSeataTxLockManager.super.unlock(lockName);
            }
        });
    }

}
