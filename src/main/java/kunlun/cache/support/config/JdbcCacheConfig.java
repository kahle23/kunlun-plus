/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.cache.support.config;

import java.util.concurrent.TimeUnit;

public class JdbcCacheConfig {
    private String name;
    private String lockManager;
    private String tableName;
    private String fieldExpireTime;
    private String fieldCacheValue;
    private String fieldCacheName;
    private String fieldCacheKey;
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

    public String getTableName() {

        return tableName;
    }

    public void setTableName(String tableName) {

        this.tableName = tableName;
    }

    public String getFieldExpireTime() {

        return fieldExpireTime;
    }

    public void setFieldExpireTime(String fieldExpireTime) {

        this.fieldExpireTime = fieldExpireTime;
    }

    public String getFieldCacheValue() {

        return fieldCacheValue;
    }

    public void setFieldCacheValue(String fieldCacheValue) {

        this.fieldCacheValue = fieldCacheValue;
    }

    public String getFieldCacheName() {

        return fieldCacheName;
    }

    public void setFieldCacheName(String fieldCacheName) {

        this.fieldCacheName = fieldCacheName;
    }

    public String getFieldCacheKey() {

        return fieldCacheKey;
    }

    public void setFieldCacheKey(String fieldCacheKey) {

        this.fieldCacheKey = fieldCacheKey;
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
