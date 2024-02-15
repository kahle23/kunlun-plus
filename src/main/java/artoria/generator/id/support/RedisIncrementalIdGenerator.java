package artoria.generator.id.support;

import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static artoria.common.constant.Numbers.ONE;

/**
 * The redis incremental identifier generator.
 * @see <a href="https://redis.io/commands/incrby">INCRBY key increment</a>
 * @author Kahle
 */
public class RedisIncrementalIdGenerator extends AbstractIncrementalIdGenerator {
    private final StringRedisTemplate stringRedisTemplate;

    public RedisIncrementalIdGenerator(RedisIncrementalIdConfig config
            , StringRedisTemplate stringRedisTemplate) {
        super(config);
        Assert.notNull(stringRedisTemplate
                , "Parameter \"stringRedisTemplate\" must not null. ");
        this.stringRedisTemplate = stringRedisTemplate;
        String redisKeyPrefix = config.getRedisKeyPrefix();
        if (StringUtils.isBlank(redisKeyPrefix)) {
            config.setRedisKeyPrefix("_identifier:");
        }
    }

    @Override
    public RedisIncrementalIdConfig getConfig() {

        return (RedisIncrementalIdConfig) super.getConfig();
    }

    @Override
    protected Long incrementAndGet() {
        // Build the redis key.
        String redisKeyPrefix = getConfig().getRedisKeyPrefix();
        Assert.notBlank(redisKeyPrefix
                , "Parameter \"redisKeyPrefix\" must not blank. ");
        String redisKey = redisKeyPrefix.endsWith(":") ? redisKeyPrefix : redisKeyPrefix + ":";
        redisKey = redisKey + getConfig().getName() + ":" + DateUtils.format("yyyyMMdd");
        // Do increment.
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        Integer stepLength = getConfig().getStepLength();
        Long increment = opsForValue.increment(redisKey, stepLength);
        if (increment == null) {
            throw new IllegalStateException("An error is likely due to use pipeline / transaction. ");
        }
        // Set expire time.
        if (increment <= stepLength) {
            // In redis key, it has been fixed as one day.
            // So the expiration time only needs to be greater than one day.
            stringRedisTemplate.expire(redisKey, ONE, TimeUnit.DAYS);
        }
        // Return result.
        return increment;
    }

}
