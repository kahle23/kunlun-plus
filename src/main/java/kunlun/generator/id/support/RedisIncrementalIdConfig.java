/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.generator.id.support;

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
