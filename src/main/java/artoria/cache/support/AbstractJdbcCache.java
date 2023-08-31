package artoria.cache.support;

import artoria.cache.AbstractCache;
import artoria.core.Serializer;
import artoria.data.Dict;
import artoria.data.bean.BeanUtils;
import artoria.serialize.support.Base64TextSerializer;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static artoria.common.constant.Numbers.ZERO;

public abstract class AbstractJdbcCache extends AbstractCache {
    private static final Logger log = LoggerFactory.getLogger(AbstractJdbcCache.class);
    private final Serializer serializer;
    private final Object jdbcExecutor;
    private final String lockManager;
    private final String tableName;
    private final String fieldExpireTime;
    private final String fieldCacheValue;
    private final String fieldCacheName;
    private final String fieldCacheKey;
    protected final TimeUnit timeToLiveUnit;
    protected final Long timeToLive;

    public AbstractJdbcCache(String name, Object cacheConfig, Object jdbcExecutor) {
        super(name);
        Dict config = Dict.of(BeanUtils.beanToMap(cacheConfig));
        if (jdbcExecutor == null) {
            jdbcExecutor = config.get("jdbcExecutor");
        }
        Assert.notNull(jdbcExecutor, "Parameter \"jdbcExecutor\" must not null. ");
        this.jdbcExecutor = jdbcExecutor;
        Serializer serializer = (Serializer) config.get("serializer");
        this.serializer = serializer != null ? serializer : new Base64TextSerializer();
        this.lockManager = config.getString("lockManager");
        this.tableName = config.getString("tableName", "t_cache");
        this.fieldExpireTime = config.getString("fieldExpireTime", "expire_time");
        this.fieldCacheValue = config.getString("fieldCacheValue", "value");
        this.fieldCacheName = config.getString("fieldCacheName", "name");
        this.fieldCacheKey = config.getString("fieldCacheKey", "key");
        // Process the timeToLive and the timeToLiveUnit.
        this.timeToLiveUnit = config.get("timeToLiveUnit", TimeUnit.class);
        this.timeToLive = config.getLong("timeToLive");
    }

    @Override
    protected String getLockManager() {

        return lockManager;
    }

    protected abstract Map<String, Object> queryRecord(Object key);

    protected abstract boolean existRecord(Object key);

    protected abstract boolean saveRecord(Object key, Object value, Date expireTime);

    protected abstract boolean updateRecord(Object key, Object value, Date expireTime);

    protected abstract boolean updateExpireTime(Object key, Date expireTime);

    protected abstract boolean deleteRecord(Object key);

    public Serializer getSerializer() {

        return serializer;
    }

    public Object getJdbcExecutor() {

        return jdbcExecutor;
    }

    public String getTableName() {

        return tableName;
    }

    public String getFieldExpireTime() {

        return fieldExpireTime;
    }

    public String getFieldCacheValue() {

        return fieldCacheValue;
    }

    public String getFieldCacheName() {

        return fieldCacheName;
    }

    public String getFieldCacheKey() {

        return fieldCacheKey;
    }

    @Override
    public Object getNative() {

        return getJdbcExecutor();
    }

    @Override
    public Object get(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        Map<String, Object> recordMap = queryRecord(key);
        if (recordMap == null) { return null; }
        Dict record = Dict.of(recordMap);
        // expireTime
        Date expireTime = record.get(getFieldExpireTime(), Date.class);
        if (expireTime != null && new Date().after(expireTime)) {
            // is expire (do not delete)
            return null;
        }
        // value
        String valueStr = record.getString(getFieldCacheValue());
        if (StringUtils.isBlank(valueStr)) { return null; }
        return getSerializer().deserialize(valueStr);
    }

    @Override
    public boolean containsKey(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        return existRecord(key);
    }

    @Override
    public Object put(Object key, Object value) {
        // if timeToLive is not null
        if (timeToLive != null && timeToLiveUnit != null) {
            return put(key, value, timeToLive, timeToLiveUnit);
        }
        // if timeToLive is null
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        value = getSerializer().serialize(value);  boolean success;
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
        value = getSerializer().serialize(value);  boolean success;
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
