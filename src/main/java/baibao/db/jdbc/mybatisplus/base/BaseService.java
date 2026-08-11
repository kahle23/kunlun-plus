/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.db.jdbc.mybatisplus.base;

import baibao.common.dto.DragSortDTO;
import baibao.common.dto.base.BaseQuery;
import baibao.common.enums.QueryMode;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import kunlun.common.Page;
import kunlun.core.function.BiConsumer;
import kunlun.db.jdbc.mybatisplus.base.IService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * BaseService
 * @author Kahle
 */
@SuppressWarnings({"unused"})
public interface BaseService<T, A, E, Q extends BaseQuery, R> extends IService<T> {

    // region ======== 内置方法 ========
    /**
     * getResultClass
     * @return ResultClass
     */
    Class<R> getResultClass();

    /**
     * getQueryClass
     * @return QueryClass
     */
    Class<Q> getQueryClass();

    /**
     * getEditParamClass
     * @return EditParamClass
     */
    Class<E> getEditParamClass();

    /**
     * getAddParamClass
     * @return AddParamClass
     */
    Class<A> getAddParamClass();
    // endregion


    // region ======== 通用方法 ========

    /**
     * 根据 Long ID 集合查询[业务数据]的 Map.
     * @param ids [业务数据]的 Long ID 集合
     * @return 查询结果
     */
    Map<Long, T> mapByIds(Collection<Long> ids);

    /**
     * 根据[业务数据]的指定字段更新状态（如果旧状态不为空，它将为查询条件进行筛选）
     * @param field 指定字段（可能是ID，可能是Code，也可能是其他）
     * @param value [业务数据]的ID或Code或其他
     * @param status 状态字段
     * @param oldVal 旧状态（可以为空）
     * @param newVal 新状态（不能为空）
     * @return 是否更新成功
     */
    boolean updateStatusBy(SFunction<T, ?> field, Object value, SFunction<T, ?> status, Object oldVal, Object newVal);

    /**
     * 根据[业务数据]的指定字段更新状态（如果旧状态不为空，它将为查询条件进行筛选）
     * @param field 指定字段（可能是ID，可能是Code，也可能是其他）
     * @param values [业务数据]的ID或Code或其他 的集合
     * @param status 状态字段
     * @param oldVal 旧状态（可以为空）
     * @param newVal 新状态（不能为空）
     * @return 是否更新成功
     */
    boolean updateStatusBy(SFunction<T, ?> field, Collection<?> values, SFunction<T, ?> status, Object oldVal, Object newVal);
    // endregion


    // region ======== 业务方法 ========

    /**
     * 增加[业务数据]表的数据（对象的唯一标识只能是"id"）.
     *
     * @param param 待增加的数据
     * @return 增加的数据的ID
     */
    Long addRecord(A param);

    /**
     * 批量增加[业务数据]表的数据（对象的唯一标识只能是"id"）.
     * @param params 待增加的数据的集合
     * @return 增加的数据的ID的集合
     */
    List<Long> addBatch(List<A> params);

    /**
     * 编辑[业务数据]表的数据（对象的唯一标识只能是"id"）.
     *
     * @param param 待编辑的数据
     */
    void editRecord(E param);

    /**
     * 批量编辑[业务数据]表的数据【仅新增、编辑】（对象的唯一标识只能是"id"）.
     * @param params 待编辑的数据的集合
     * @param ignoreSingleError 是否忽略构建单个新增/编辑对象时的异常
     * @return 如果 ignoreSingleError = true 的情况下，会把异常信息返回出来，否则则是一个空集合
     */
    Map<String, Exception> editBatch(List<E> params, boolean ignoreSingleError);

    /**
     * 批量编辑[业务数据]表的数据【新增、编辑和删除】（对象的唯一标识只能是"id"）.
     * @param params 待编辑的数据的集合
     * @param queryBuilder 根据入参集合构建查询对象，用于查询入参对应的全部旧数据
     * @param mapKeyBuilder Map的key生成器，主要用于查询结果构建Map，和入参对象从Map匹数据
     * @param ignoreSingleError 是否忽略构建单个新增/编辑对象时的异常
     * @return 如果 ignoreSingleError = true 的情况下，会把异常信息返回出来，否则则是一个空Map
     */
    Map<String, Exception> editBatch1(List<E> params, Function<List<E>, Q> queryBuilder,
                                      BiFunction<E, R, String> mapKeyBuilder,
                                      boolean ignoreSingleError,
                                      boolean skipDelete);

    /**
     * 基于指定的字段启用/禁用[业务数据].
     * @param field 指定的字段（ID或其他字段）
     * @param value [业务数据]的ID或其他属性的值
     * @param enabled 启用禁用字段
     * @param enabledVal 启用/禁用状态：0 未启用，1 启用
     */
    void enable(SFunction<T, ?> field, Serializable value, SFunction<T, ?> enabled, Integer enabledVal);

    /**
     * 拖拽排序.
     * @param sort 对象的排序字段
     * @param sorts 入参，排序后的结果
     * @return 是否更新成功
     */
    boolean dragSort(BiConsumer<T, Long> sort, List<DragSortDTO> sorts);

    /**
     * 根据ID删除[业务数据].
     *
     * @param id [业务数据]的ID
     */
    void deleteById(Serializable id);

    /**
     * 根据 Long ID 集合批量删除[业务数据].
     *
     * @param ids [业务数据]的 Long ID 集合
     */
    void deleteByLongIds(Collection<Long> ids);

    /**
     * 根据ID查询[业务数据]详情.
     *
     * @param id [业务数据]的ID
     * @return 查询到的[业务数据]
     */
    R detailById(Serializable id);

//    /**
//     * 根据ID查询[业务数据]详情（基于 queryPage 方法）.
//     *
//     * @param id [业务数据]的ID
//     * @return 查询到的[业务数据]
//     * @see BaseService#getById1(Serializable)
//     */
//    @Deprecated
//    R detailById1(Serializable id);

    /**
     * 根据ID查询[业务数据]详情（基于 getById 方法）.
     *
     * @param id [业务数据]的ID
     * @return 查询到的[业务数据]
     */
    R getById1(Serializable id);

    /**
     * 根据ID查询[业务数据]详情（基于 getById 方法）.
     *
     * @param id [业务数据]的ID
     * @param queryMode 查询模式（用于挂载不同数据处理逻辑），可以为空
     * @return 查询到的[业务数据]
     */
    R getById1(Serializable id, QueryMode queryMode);

    /**
     * 根据ID查询[业务数据]详情（基于 queryPage 方法）.
     *
     * @param id [业务数据]的ID
     * @return 查询到的[业务数据]
     */
    R getById2(Serializable id);

    /**
     * 根据ID查询[业务数据]详情（基于 queryPage 方法）.
     *
     * @param id [业务数据]的ID
     * @param queryMode 查询模式（用于挂载不同数据处理逻辑），可以为空
     * @return 查询到的[业务数据]
     */
    R getById2(Serializable id, QueryMode queryMode);

    /**
     * 条件查询[业务数据]存在的条数.
     * @param query 查询条件
     * @return 查询结果
     */
    long queryCount(Q query);

    /**
     * 条件查询[业务数据]是否存在.
     * @param query 查询条件
     * @return 查询结果
     */
    boolean queryExist(Q query);

    /**
     * 分页条件查询[业务数据]列表.
     *
     * @param query 查询条件
     * @return 查询结果
     */
    Page<R> queryPage(Q query);

    /**
     * 滚动分页条件查询[业务数据]列表.
     *
     * @param query 查询条件
     * @param idField 唯一标识的字段
     * @return 查询结果
     */
    Page<R> queryScrollPage(Q query, SFunction<T, ?> idField);

    /**
     * 条件查询[业务数据]列表.
     *
     * @param query 查询条件
     * @return 查询结果
     */
    List<R> queryList(Q query);

    /**
     * 条件查询[业务数据]的Map数据（key：支持自定义，value：数据对象）.
     * @param query 查询条件
     * @param keyMapper keyMapper
     * @return 查询结果
     */
    <K> Map<K, R> queryMap(Q query, Function<R, K> keyMapper);

    /**
     * 条件查询[业务数据]的ListMap数据(key：支持自定义，value：对象集合)
     * @param query 查询条件
     * @param keyMapper keyMapper
     * @return 查询结果
     */
    <K> Map<K, List<R>> queryGroupMap(Q query, Function<R, K> keyMapper);
    // endregion

}
