/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.cache.support;

import kunlun.cache.AbstractCache;
import kunlun.data.Dict;
import kunlun.data.bean.BeanUtils;
import kunlun.data.serialize.support.Base64TextSerializer;
import kunlun.util.Assert;
import kunlun.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static kunlun.common.constant.Numbers.ZERO;

public abstract class AbstractJdbcCache extends AbstractCache {
    private static final Logger log = LoggerFactory.getLogger(AbstractJdbcCache.class);
    private final JdbcCacheConfig cacheConfig;

    public AbstractJdbcCache(JdbcCacheConfig cacheConfig, Object jdbcExecutor) {
        // Validate the cache config.
        Assert.notNull(cacheConfig, "Parameter \"cacheConfig\" must not null. ");
        Assert.notBlank(cacheConfig.getName(), "Parameter \"cacheConfig.name\" must not blank. ");
        this.cacheConfig = (cacheConfig = BeanUtils.beanToBean(cacheConfig, JdbcCacheConfig.class));
        // Process jdbc executor and serializer.
        if (jdbcExecutor != null) { cacheConfig.setJdbcExecutor(jdbcExecutor); }
        Assert.notNull(cacheConfig.getJdbcExecutor(), "Parameter \"jdbcExecutor\" must not null. ");
        if (cacheConfig.getSerializer() == null) {
            cacheConfig.setSerializer(new Base64TextSerializer());
        }
        // Process the table fields.
        if (StringUtils.isBlank(cacheConfig.getTableName())) {
            cacheConfig.setTableName("t_cache");
        }
        if (StringUtils.isBlank(cacheConfig.getFieldCacheName())) {
            cacheConfig.setFieldCacheName("name");
        }
        if (StringUtils.isBlank(cacheConfig.getFieldCacheKey())) {
            cacheConfig.setFieldCacheKey("key");
        }
        if (StringUtils.isBlank(cacheConfig.getFieldCacheValue())) {
            cacheConfig.setFieldCacheValue("value");
        }
        if (StringUtils.isBlank(cacheConfig.getFieldExpireTime())) {
            cacheConfig.setFieldExpireTime("expire_time");
        }
    }

    public JdbcCacheConfig getConfig() {

        return cacheConfig;
    }

    @Override
    protected String getLockManager() {

        return getConfig().getLockManager();
    }

    protected abstract Map<String, Object> queryRecord(Object key);

    protected abstract boolean existRecord(Object key);

    protected abstract boolean saveRecord(Object key, Object value, Date expireTime);

    protected abstract boolean updateRecord(Object key, Object value, Date expireTime);

    protected abstract boolean updateExpireTime(Object key, Date expireTime);

    protected abstract boolean deleteRecord(Object key);

    @Override
    public Object getNative() {

        return getConfig().getJdbcExecutor();
    }

    @Override
    public Object get(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        Map<String, Object> recordMap = queryRecord(key);
        if (recordMap == null) { return null; }
        Dict record = Dict.of(recordMap);
        // expireTime
        Date expireTime = record.get(getConfig().getFieldExpireTime(), Date.class);
        if (expireTime != null && new Date().after(expireTime)) {
            // is expired (do not delete)
            return null;
        }
        // value
        String valueStr = record.getString(getConfig().getFieldCacheValue());
        if (StringUtils.isBlank(valueStr)) { return null; }
        return getConfig().getSerializer().deserialize(valueStr);
    }

    @Override
    public boolean containsKey(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        return existRecord(key);
    }

    @Override
    public Object put(Object key, Object value) {
        // if timeToLive is not null
        if (getConfig().getTimeToLive() != null && getConfig().getTimeToLiveUnit() != null) {
            return put(key, value, getConfig().getTimeToLive(), getConfig().getTimeToLiveUnit());
        }
        // if timeToLive is null
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        value = getConfig().getSerializer().serialize(value);  boolean success;
        if (existRecord(key)) { success = updateRecord(key, value, null); }
        else { success = saveRecord(key, value, null); }
        Assert.state(success, "Put data failed. ");
        return null;
    }

    @Override
    public Object put(Object key, Object value, long timeToLive, TimeUnit timeUnit) {
        Assert.notNull(timeUnit, "Parameter \"timeUnit\" must not null. ");
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        Date expireTime = timeToLive > ZERO ?
                new Date(currentTimeMillis() + timeUnit.toMillis(timeToLive)) : null;
        value = getConfig().getSerializer().serialize(value);  boolean success;
        if (existRecord(key)) { success = updateRecord(key, value, expireTime); }
        else { success = saveRecord(key, value, expireTime); }
        Assert.state(success, "Put data failed. ");
        return null;
    }

    @Override
    public boolean expire(Object key, long timeToLive, TimeUnit timeUnit) {
        Assert.notNull(timeUnit, "Parameter \"timeUnit\" must not null. ");
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        Date expireTime = timeToLive > ZERO ?
                new Date(currentTimeMillis() + timeUnit.toMillis(timeToLive)) : null;
        return updateExpireTime(key, expireTime);
    }

    @Override
    public boolean persist(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        return updateExpireTime(key, null);
    }

    @Override
    public Object remove(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        Assert.state(deleteRecord(key), "Remove data failed. ");
        return null;
    }

}
