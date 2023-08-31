package artoria.cache.support;

import artoria.common.constant.Numbers;
import artoria.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static artoria.common.constant.Numbers.ONE;
import static artoria.common.constant.Numbers.ZERO;

public class JdbcTemplateCache extends AbstractJdbcCache {
    private static final Logger log = LoggerFactory.getLogger(JdbcTemplateCache.class);

    public JdbcTemplateCache(String name, Object cacheConfig) {

        super(name, cacheConfig, null);
    }

    public JdbcTemplateCache(String name, Object cacheConfig, JdbcTemplate jdbcTemplate) {

        super(name, cacheConfig, jdbcTemplate);
    }

    @Override
    protected Map<String, Object> queryRecord(Object key) {
        String sql = String.format("select `%s`, `%s` from `%s` where `%s` = ? and `%s` = ?; "
            , getFieldCacheValue(), getFieldExpireTime(), getTableName(), getFieldCacheName(), getFieldCacheKey());
        List<Map<String, Object>> maps = getJdbcExecutor().queryForList(sql, getName(), key);
        if (CollectionUtils.isEmpty(maps)) { return null; }
        return maps.get(Numbers.ZERO);
    }

    @Override
    protected boolean existRecord(Object key) {
        String sql = String.format("select count(0) from `%s` where `%s` = ? and `%s` = ?; "
                , getTableName(), getFieldCacheName(), getFieldCacheKey());
        Integer count = getJdbcExecutor().queryForObject(sql, new Object[]{getName(), key}, Integer.class);
        return count != null && count > ZERO;
    }

    @Override
    protected boolean saveRecord(Object key, Object value, Date expireTime) {
        String sql = String.format("insert into `%s`(`%s`, `%s`, `%s`, `%s`) VALUES (?, ?, ?, ?); "
            , getTableName(), getFieldCacheName(), getFieldCacheKey(), getFieldCacheValue(), getFieldExpireTime());
        return getJdbcExecutor().update(sql, getName(), key, value, expireTime) == ONE;
    }

    @Override
    protected boolean updateRecord(Object key, Object value, Date expireTime) {
        String sql = String.format("update `%s` set `%s` = ?, `%s` = ? where `%s` = ? and `%s` = ?; "
            , getTableName(), getFieldCacheValue(), getFieldExpireTime(), getFieldCacheName(), getFieldCacheKey());
        return getJdbcExecutor().update(sql, value, expireTime, getName(), key) == ONE;
    }

    @Override
    protected boolean updateExpireTime(Object key, Date expireTime) {
        String sql = String.format("update `%s` set `%s` = ? where `%s` = ? and `%s` = ?; "
                , getTableName(), getFieldExpireTime(), getFieldCacheName(), getFieldCacheKey());
        return getJdbcExecutor().update(sql, expireTime, getName(), key) == ONE;
    }

    @Override
    protected boolean deleteRecord(Object key) {
        String sql = String.format("delete from `%s` where `%s` = ? and `%s` = ?; "
                , getTableName(), getFieldCacheName(), getFieldCacheKey());
        return getJdbcExecutor().update(sql, getName(), key) == ONE;
    }

    @Override
    public JdbcTemplate getJdbcExecutor() {

        return (JdbcTemplate) super.getJdbcExecutor();
    }

}
