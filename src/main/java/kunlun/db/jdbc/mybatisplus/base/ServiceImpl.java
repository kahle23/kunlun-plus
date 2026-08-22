package kunlun.db.jdbc.mybatisplus.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import kunlun.db.jdbc.JdbcUtil;
import kunlun.exception.util.VerifyUtil;
import kunlun.reflect.ReflectUtil;

import java.util.Collection;
import java.util.List;

import static kunlun.common.constant.Numbers.ONE;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.util.Assert.notEmpty;
import static kunlun.util.Assert.notNull;
import static kunlun.util.CollUtil.toCollection;
import static kunlun.util.StrUtil.isBlank;

/**
 * {@link IService} 的抽象实现基类：在 MyBatis-Plus-Join 的 {@link MPJBaseServiceImpl} 之上，
 * <p>用 {@code Wrappers.lambdaQuery} / {@code Wrappers.lambdaUpdate} 落地 {@link IService} 中以
 * {@link SFunction} 字段引用为入参的 exists / update / delete / get / find 便捷方法。
 * <p>子类仅需声明泛型 {@code <M, T>} 即可直接复用本类提供的字段化操作，无需再写 Wrapper 模板。
 * @author Kahle
 */
@SuppressWarnings({"unused"})
public abstract class ServiceImpl<M extends MPJBaseMapper<T>, T> extends MPJBaseServiceImpl<M, T> implements IService<T> {

    // region ======== exist related methods ========

    @Override
    public boolean existBy(SFunction<T, ?> field, Object value) {
        return count(Wrappers.lambdaQuery(getEntityClass())
                .in(notNull(field), CollUtil.distinct(notEmpty(toCollection(value))))
        ) > ZERO;
    }

    @Override
    public boolean existBy(SFunction<T, ?> field, Object value, SFunction<T, ?> neField, Object neValue) {
        boolean neValNotEmp = ObjUtil.isNotEmpty(neValue);
        if (neValNotEmp) { notNull(neField); }
        return count(Wrappers.lambdaQuery(getEntityClass())
                .eq(notNull(field), notNull(value))
                .ne(neValNotEmp, neField, neValue)
        ) > ZERO;
    }
    // endregion ======== exist related methods ========


    // region ======== update related methods ========

    @Override
    public boolean updateBy(T update, SFunction<T, ?> field, Object value) {

        return updateBy(update, field, toCollection(value));
    }

    @Override
    public boolean updateBy(T update, SFunction<T, ?> field, Collection<?> values) {
        return update(notNull(update), Wrappers.lambdaUpdate(getEntityClass())
                .in(notNull(field), CollUtil.distinct(notEmpty(values)))
        );
    }

    @Override
    public boolean updateAndClearById(T entity, Collection<String> clearFields) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
        VerifyUtil.notNull(tableInfo, "实体“%s”的表信息不可用，无法把置空子句合并进更新！", getEntityClass().getName());
        VerifyUtil.isTrue(!isBlank(tableInfo.getKeyColumn())
                , "实体“%s”未解析到主键列，无法把置空子句合并进更新！", getEntityClass().getName());
        Object id = VerifyUtil.notNull(ReflectUtil.getFieldValue(entity, tableInfo.getKeyProperty())
                , "实体“%s”的主键值为空，无法把置空子句合并进更新！", getEntityClass().getName());
        // 实体上与待置空字段重合的非空值先抹掉，避免同一列在 SET 子句出现两次
        for (String field : clearFields) {
            if (ReflectUtil.hasField(getEntityClass(), field)) {
                ReflectUtil.setFieldValue(entity, field, null);
            }
        }
        UpdateWrapper<T> wrapper = new UpdateWrapper<>();
        wrapper.eq(tableInfo.getKeyColumn(), id);
        JdbcUtil.appendClearFields(wrapper, getEntityClass(), clearFields);
        return update(entity, wrapper);
    }
    // endregion ======== update related methods ========


    // region ======== delete related methods ========

    @Override
    public boolean deleteBy(SFunction<T, ?> field, Object value) {

        return deleteBy(notNull(field), toCollection(value));
    }

    @Override
    public boolean deleteBy(SFunction<T, ?> field, Collection<?> values) {
        return remove(Wrappers.lambdaQuery(getEntityClass())
                .in(notNull(field), CollUtil.distinct(notEmpty(values)))
        );
    }
    // endregion ======== delete related methods ========


    // region ======== query related methods ========

    @Override
    public T getBy(SFunction<T, ?> field, Object value) {
        // 只取一条且不执行 count；物理分页 SQL 的数据库方言由 MyBatis-Plus 分页插件适配，
        // 未注册分页插件时退化为全量查询后取第一条（与 findBy 行为一致，不会更差）
        Page<T> page = page(new Page<>(ONE, ONE, false), Wrappers.lambdaQuery(getEntityClass())
                .in(notNull(field), CollUtil.distinct(notEmpty(toCollection(value))))
        );
        return CollUtil.getFirst(page.getRecords());
    }

    @Override
    public List<T> findBy(SFunction<T, ?> field, Collection<?> values) {
        return list(Wrappers.lambdaQuery(getEntityClass())
                .in(notNull(field), CollUtil.distinct(notEmpty(values)))
        );
    }
    // endregion ======== query related methods ========

}
