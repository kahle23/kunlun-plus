package kunlun.spring.config.cache;

import kunlun.cache.support.SimpleCacheConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The cache tools properties.
 * @author Kahle
 */
@ConfigurationProperties("kunlun.cache")
public class CacheProperties {
    /**
     * Enabled the cache tools.
     */
    private Boolean enabled;
    /**
     * The simple cache configurations.
     */
    private List<SimpleConfig> simple;
    /**
     * The redis cache configurations.
     */
    private List<RedisConfig> redis;
    /**
     * The jdbc cache configuration.
     */
    private List<JdbcConfig> jdbc;

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public List<SimpleConfig> getSimple() {

        return simple;
    }

    public void setSimple(List<SimpleConfig> simple) {

        this.simple = simple;
    }

    public List<RedisConfig> getRedis() {

        return redis;
    }

    public void setRedis(List<RedisConfig> redis) {

        this.redis = redis;
    }

    public List<JdbcConfig> getJdbc() {

        return jdbc;
    }

    public void setJdbc(List<JdbcConfig> jdbc) {

        this.jdbc = jdbc;
    }

    /**
     * The simple cache configuration.
     * @author Kahle
     */
    public static class SimpleConfig extends SimpleCacheConfig {
        private String name;

        public String getName() {

            return name;
        }

        public void setName(String name) {

            this.name = name;
        }
    }

    /**
     * The redis cache configuration.
     * @author Kahle
     */
    public static class RedisConfig {
        private String name;
        private String lockManager;
        private Long   timeToLive;
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

    /**
     * The jdbc cache configuration.
     * @author Kahle
     */
    public static class JdbcConfig {
        private String name;
        private String lockManager;
        private String tableName;
        private String fieldExpireTime;
        private String fieldCacheValue;
        private String fieldCacheName;
        private String fieldCacheKey;
        private Long   timeToLive;
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

}
