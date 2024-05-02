/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.cache.support;

import kunlun.common.constant.Nulls;
import kunlun.common.constant.Numbers;
import kunlun.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Numbers.ZERO;

public class JdbcTemplateCache extends AbstractJdbcCache {
    private static final Logger log = LoggerFactory.getLogger(JdbcTemplateCache.class);

    public JdbcTemplateCache(JdbcCacheConfig cacheConfig) {

        super(cacheConfig, Nulls.OBJ);
    }

    public JdbcTemplateCache(JdbcCacheConfig cacheConfig, JdbcTemplate jdbcTemplate) {

        super(cacheConfig, jdbcTemplate);
    }

    @Override
    protected Map<String, Object> queryRecord(Object key) {
        String sql = String.format("select `%s`, `%s` from `%s` where `%s` = ? and `%s` = ?; "
                , getConfig().getFieldCacheValue(), getConfig().getFieldExpireTime()
                , getConfig().getTableName(), getConfig().getFieldCacheName()
                , getConfig().getFieldCacheKey());
        List<Map<String, Object>> maps = getNative().queryForList(sql, getConfig().getName(), key);
        if (CollectionUtils.isEmpty(maps)) { return null; }
        return maps.get(Numbers.ZERO);
    }

    @Override
    protected boolean existRecord(Object key) {
        String sql = String.format("select count(0) from `%s` where `%s` = ? and `%s` = ?; "
                , getConfig().getTableName(), getConfig().getFieldCacheName()
                , getConfig().getFieldCacheKey());
        Integer count = getNative().queryForObject(
                sql, new Object[]{getConfig().getName(), key}, Integer.class);
        return count != null && count > ZERO;
    }

    @Override
    protected boolean saveRecord(Object key, Object value, Date expireTime) {
        String sql = String.format("insert into `%s`(`%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?); "
                , getConfig().getTableName(), getConfig().getFieldCacheName()
                , getConfig().getFieldCacheKey(), getConfig().getFieldCacheValue()
                , getConfig().getFieldExpireTime());
        return getNative().update(sql, getConfig().getName(), key, value, expireTime) == ONE;
    }

    @Override
    protected boolean updateRecord(Object key, Object value, Date expireTime) {
        String sql = String.format("update `%s` set `%s` = ?, `%s` = ? where `%s` = ? and `%s` = ?; "
                , getConfig().getTableName(), getConfig().getFieldCacheValue()
                , getConfig().getFieldExpireTime(), getConfig().getFieldCacheName()
                , getConfig().getFieldCacheKey());
        return getNative().update(sql, value, expireTime, getConfig().getName(), key) == ONE;
    }

    @Override
    protected boolean updateExpireTime(Object key, Date expireTime) {
        String sql = String.format("update `%s` set `%s` = ? where `%s` = ? and `%s` = ?; "
                , getConfig().getTableName(), getConfig().getFieldExpireTime()
                , getConfig().getFieldCacheName(), getConfig().getFieldCacheKey());
        return getNative().update(sql, expireTime, getConfig().getName(), key) == ONE;
    }

    @Override
    protected boolean deleteRecord(Object key) {
        String sql = String.format("delete from `%s` where `%s` = ? and `%s` = ?; "
                , getConfig().getTableName(), getConfig().getFieldCacheName()
                , getConfig().getFieldCacheKey());
        return getNative().update(sql, getConfig().getName(), key) == ONE;
    }

    @Override
    public JdbcTemplate getNative() {

        return (JdbcTemplate) super.getNative();
    }

}
