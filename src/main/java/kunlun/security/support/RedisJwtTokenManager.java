package kunlun.security.support;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

import static kunlun.common.constant.Numbers.MINUS_THREE;
import static kunlun.common.constant.Numbers.ONE;

public class RedisJwtTokenManager extends AbstractTokenManager {
    public static final String PREFIX = "TOKEN:";

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenManager jwtTokenManager;
    // second  30*24*60*60
    private final Long timeToLive;

    public RedisJwtTokenManager(StringRedisTemplate redisTemplate,
                                JwtTokenManager jwtTokenManager,
                                Long timeToLive) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenManager = jwtTokenManager;
        this.timeToLive = timeToLive;
    }

    public RedisJwtTokenManager(StringRedisTemplate redisTemplate,
                                Long timeToLive) {

        this(redisTemplate, new JwtTokenManager(null, 6*30*24*60*60L), timeToLive);
    }

    @Override
    public String buildToken(Token token) {
        String tokenStr = jwtTokenManager.buildToken(token);

        String redisKey = PREFIX + tokenStr;
        String redisVal = String.valueOf(ONE);
        redisTemplate.opsForValue().set(redisKey, redisVal, timeToLive, TimeUnit.SECONDS);

        return tokenStr;
    }

    @Override
    public Token parseToken(String token) {

        return jwtTokenManager.parseToken(token);
    }

    @Override
    public int verifyToken(String token) {
        int jwtVerify = jwtTokenManager.verifyToken(token);
        if (jwtVerify != ONE) { return jwtVerify; }

        String redisKey = PREFIX + token;
        //noinspection ConstantConditions
        if (!redisTemplate.hasKey(redisKey)) {
            return MINUS_THREE;
        }
        return ONE;
    }

    @Override
    public void deleteToken(String token, Integer reason) {
        String redisKey = PREFIX + token;
        redisTemplate.delete(redisKey);
    }

    @Override
    public Object refreshToken(String token) {
        String redisKey = PREFIX + token;
        //noinspection ConstantConditions
        if (!redisTemplate.hasKey(redisKey)) {
            return null;
        }
        redisTemplate.expire(redisKey, timeToLive, TimeUnit.SECONDS);
        return null;
    }

}
