/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.cache.support.config;

import java.util.concurrent.TimeUnit;

public class RedisCacheConfig {
    private String name;
    private String lockManager;
    private Long timeToLive;
    private TimeUnit timeToLiveUnit;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getLockManager() {

        return lockManager;
    }

    public void setLockManager(String lockManager) {

        this.lockManager = lockManager;
    }

    public Long getTimeToLive() {

        return timeToLive;
    }

    public void setTimeToLive(Long timeToLive) {

        this.timeToLive = timeToLive;
    }

    public TimeUnit getTimeToLiveUnit() {

        return timeToLiveUnit;
    }

    public void setTimeToLiveUnit(TimeUnit timeToLiveUnit) {

        this.timeToLiveUnit = timeToLiveUnit;
    }

}
