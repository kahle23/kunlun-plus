package kunlun.db.jdbc.mybatisplus.base;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.base.MPJBaseService;

import java.util.Collection;
import java.util.List;

/**
 * 基于字段引用的 Service 扩展接口：在 MyBatis-Plus-Join 的 {@link MPJBaseService} 之上，
 * <p>补充一组以 {@link SFunction} 字段引用 + 值/集合为入参的 exists / update / delete / get / find 便捷方法，
 * <p>免去上层反复手写 {@code Wrappers.lambdaQuery(...)} 与 {@code Wrappers.lambdaUpdate(...)} 模板。
 * <p>对应实现见 {@link ServiceImpl}。
 * @author Kahle
 */
@SuppressWarnings({"unused"})
public interface IService<T> extends MPJBaseService<T> {

    // region ======== 存在性判断相关方法 ========
    /**
     * 判断指定字段匹配给定值的记录是否存在。
     * <p>value 可传单值或集合：单值等价 eq，集合按 IN；传入值会自动去重；为 null / 空集合时抛出参数异常。
     *
     * @param field 字段引用（不可为 null）
     * @param value 字段值，可传单值或集合
     * @return 是否存在
     */
    boolean existBy(SFunction<T, ?> field, Object value);

    /**
     * 判断字段等于 value 的记录是否存在，并可选地排除另一字段等于 neValue 的记录。
     * <p>value 按 eq 单值匹配，不像 {@link #existBy(SFunction, Object)} 那样按集合展开。
     * neValue 非空时附加 ne 排除（此时 neField 不可为 null），为空时不附加。
     *
     * @param field 字段引用（不可为 null）
     * @param value 字段值（单值 eq 匹配，不可为 null）
     * @param neField 需排除的字段引用（neValue 非空时不可为 null）
     * @param neValue 需排除的字段值（为空时不附加排除）
     * @return 是否存在
     */
    boolean existBy(SFunction<T, ?> field, Object value, SFunction<T, ?> neField, Object neValue);
    // endregion ======== 存在性判断相关方法 ========


    // region ======== 更新相关方法 ========
    /**
     * 按 field 匹配 value 的条件更新数据，SET 取 update 实体的非空字段。
     * <p>value 可传单值或集合：单值等价 eq、集合按 IN，值自动去重；为 null / 空集合时抛出参数异常。
     * <p>仅更新 update 中非 null 的字段（null 字段不参与；需置空请改用 lambdaUpdate 显式 set）。
     *
     * @param update 待更新实体，其非空字段作为 SET 值
     * @param field 字段引用（不可为 null）
     * @param value 字段值，可传单值或集合
     * @return 是否更新成功（受影响行数 > 0）
     */
    boolean updateBy(T update, SFunction<T, ?> field, Object value);

    /**
     * 按 field IN values 的条件更新数据，SET 取 update 实体的非空字段。
     * <p>values 自动去重，为 null / 空时抛出参数异常。
     * <p>仅更新 update 中非 null 的字段；需置空请改用 lambdaUpdate 显式 set。
     *
     * @param update 待更新实体，其非空字段作为 SET 值
     * @param field 字段引用（不可为 null）
     * @param values 字段值集合
     * @return 是否更新成功（受影响行数 > 0）
     */
    boolean updateBy(T update, SFunction<T, ?> field, Collection<?> values);

    /**
     * 按主键更新实体，并把指定字段置空合并进同一条 UPDATE：实体的非空字段 SET 与置空子句（set(col, null)）
     * 由 MyBatis-Plus 合并生成单条 SQL，一笔写库即完成"更新 + 置空"，无需事务兜底，
     * 返回值语义与 updateById 一致（未命中记录返回 false）。
     * <p>主键值从实体的主键属性提取；实体上与待置空字段重合的非空值会先被抹掉，
     * 保证"置空优先"语义且与数据库方言无关（同一列在 SET 子句出现两次时，MySQL 靠后者覆盖、PostgreSQL 直接报错）。
     * <p>置空子句经 JdbcUtil 追加（默认走 MyBatisPlusFieldValueClearer 白名单）：主键、审计填充字段、
     * 未命中实体列的字段会被自动跳过；无法合并（如实体表信息不可用、主键值为空）时直接抛出异常，不做二次尝试。
     *
     * @param entity 待更新实体（须携带主键值和非空的新值）
     * @param clearFields 待置空的字段名集合（实体属性名，驼峰）
     * @return 是否更新成功（受影响行数 > 0）
     */
    boolean updateAndClearById(T entity, Collection<String> clearFields);
    // endregion ======== 更新相关方法 ========


    // region ======== 删除相关方法 ========
    /**
     * 按 field 匹配 value 的条件删除数据。
     * <p>value 可传单值或集合：单值等价 eq、集合按 IN，值自动去重；为 null / 空集合时抛出参数异常。
     *
     * @param field 字段引用（不可为 null）
     * @param value 字段值，可传单值或集合
     * @return 是否删除成功（受影响行数 > 0）
     */
    boolean deleteBy(SFunction<T, ?> field, Object value);

    /**
     * 按 field IN values 的条件删除数据。
     * <p>values 自动去重，为 null / 空时抛出参数异常。
     *
     * @param field 字段引用（不可为 null）
     * @param values 字段值集合
     * @return 是否删除成功（受影响行数 > 0）
     */
    boolean deleteBy(SFunction<T, ?> field, Collection<?> values);
    // endregion ======== 删除相关方法 ========


    // region ======== 查询相关方法 ========
    /**
     * 按 field 匹配 value 的条件取第一条记录。
     * <p>value 可传单值或集合：单值等价 eq、集合按 IN，值自动去重；为 null / 空集合时抛出参数异常。
     * <p>用物理分页（page 1 / size 1）只取一条且不执行 count；未注册分页插件时退化为全量查询后取首条。
     *
     * @param field 字段引用（不可为 null）
     * @param value 字段值，可传单值或集合
     * @return 首条匹配数据，无匹配时为 null
     */
    T getBy(SFunction<T, ?> field, Object value);

    /**
     * 按 field IN values 的条件查询全部匹配记录。
     * <p>values 自动去重，为 null / 空时抛出参数异常。
     *
     * @param field 字段引用（不可为 null）
     * @param values 字段值集合
     * @return 数据列表，永不为 null，无匹配时为空列表
     */
    List<T> findBy(SFunction<T, ?> field, Collection<?> values);
    // endregion ======== 查询相关方法 ========

}
