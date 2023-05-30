package artoria.generator.id.support;

/**
 * The redis incremental identifier configuration.
 * @author Kahle
 */
public class RedisIncrementalIdConfig extends IncrementalIdConfig {
    private String redisKeyPrefix;

    public String getRedisKeyPrefix() {

        return redisKeyPrefix;
    }

    public void setRedisKeyPrefix(String redisKeyPrefix) {

        this.redisKeyPrefix = redisKeyPrefix;
    }

}
