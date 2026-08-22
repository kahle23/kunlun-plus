/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.db.jdbc.mybatisplus.base;

import baibao.common.dto.DragSortDTO;
import baibao.common.dto.base.BaseEditParam;
import baibao.common.dto.base.BaseQuery;
import baibao.common.enums.QueryMode;
import kunlun.data.sort.SortField;
import kunlun.data.sort.SortUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import kunlun.common.Page;
import kunlun.common.constant.Nil;
import kunlun.core.function.BiConsumer;
import kunlun.data.bean.BeanUtil;
import kunlun.db.jdbc.mybatisplus.base.ServiceImpl;
import kunlun.util.Assert;
import kunlun.util.PageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static baibao.common.enums.QueryMode.allowFill;
import static baibao.common.enums.QueryMode.allowProcess;
import static com.baomidou.mybatisplus.core.toolkit.ReflectionKit.getSuperClassGenericType;
import static java.util.Collections.*;
import static java.util.Objects.nonNull;
import static kunlun.common.Errors.*;
import static kunlun.common.constant.Numbers.*;
import static kunlun.common.constant.Words.ERROR;
import static kunlun.data.validation.support.javax.ValidationUtil.validateToThrow;
import static kunlun.exception.util.VerifyUtil.*;
import static kunlun.util.CollUtil.getFirst;
import static kunlun.util.StrUtil.isNotBlank;

/**
 * BaseServiceImpl
 * 通用约定：本基类通过反射读写实体的"id"字段并按 Long 处理，
 * 即实体主键须为名为 id 的 Long 型字段；不满足该约定的实体，
 * 请覆写 refGetId、refSetId、refSetIds 方法，或不继承本类。
 * @author Kahle
 */
@SuppressWarnings({"unused"})
public abstract class BaseServiceImpl<M extends MPJBaseMapper<T>, T, A, E, Q extends BaseQuery, R>
        extends ServiceImpl<M, T> implements BaseService<T, A, E, Q, R> {
    private static final Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);
    private static final String FIELD_ID = "id";

    // region ======== 内置方法 ========

    @SuppressWarnings("unchecked")
    private final Class<R> resultClass = (Class<R>) getSuperClassGenericType(getClass(), BaseServiceImpl.class, FIVE);
    @SuppressWarnings("unchecked")
    private final Class<Q> queryClass = (Class<Q>) getSuperClassGenericType(getClass(), BaseServiceImpl.class, FOUR);
    @SuppressWarnings("unchecked")
    private final Class<E> editParamClass = (Class<E>) getSuperClassGenericType(getClass(), BaseServiceImpl.class, THREE);
    @SuppressWarnings("unchecked")
    private final Class<A> addParamClass = (Class<A>) getSuperClassGenericType(getClass(), BaseServiceImpl.class, TWO);

    protected Long refGetId(Object obj) {
        if (obj == null) { return null; }
        Assert.isTrue(ReflectUtil.hasField(obj.getClass(), FIELD_ID));
        return Convert.toLong(ReflectUtil.getFieldValue(obj, FIELD_ID));
    }

    protected E refSetId(E param, Long id) {
        if (param == null || id == null) { return param; }
        Assert.isTrue(ReflectUtil.hasField(param.getClass(), FIELD_ID));
        ReflectUtil.setFieldValue(param, FIELD_ID, id);
        return param;
    }

    protected Q refSetIds(Q query, Object ids) {
        // 判空
        if (query == null || ids == null) { return query; }
        // ID对象的类型校验（只能是 Long 和 集合，集合的泛型就不判断了）
        Assert.isTrue(ids instanceof Long || ids instanceof Collection);
        boolean isColl = ids instanceof Collection;
        // 给查询对象设置ID
        String field;
        if (ReflectUtil.hasField(getQueryClass(), field = "idList")) {
            ReflectUtil.setFieldValue(query, field, isColl ? ids : singletonList(ids));
        } else if (ReflectUtil.hasField(getQueryClass(), field = "ids")) {
            ReflectUtil.setFieldValue(query, field,  isColl ? ids : singletonList(ids));
        } else if (ReflectUtil.hasField(getQueryClass(), FIELD_ID)) {
            Assert.isFalse(isColl);
            ReflectUtil.setFieldValue(query, FIELD_ID, ids);
        } else {
            String msg = String.format("查询对象“%s”不包含字段“idList”、“ids” 和 “id”！", getQueryClass().getName());
            throw new IllegalArgumentException(msg);
        }
        // 返回 query 对象
        return query;
    }

    /**
     * 查询对象的判空兜底（统一口径：query 为空时用默认实例，各入口行为一致）.
     * @param query 查询对象
     * @return 非空的查询对象
     */
    protected Q prepareQuery(Q query) {
        if (query != null) { return query; }
        return ReflectUtil.newInstance(getQueryClass());
    }

    @Override
    public Class<R> getResultClass() {

        return resultClass;
    }

    @Override
    public Class<Q> getQueryClass() {

        return queryClass;
    }

    @Override
    public Class<E> getEditParamClass() {

        return editParamClass;
    }

    @Override
    public Class<A> getAddParamClass() {

        return addParamClass;
    }

    // endregion


    // region ======== 通用方法 ========

    @Override
    public Map<Long, T> mapByIds(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) { return emptyMap(); }
        return Optional.ofNullable(listByIds(ids))
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(this::refGetId, Function.identity(), (k1, k2) -> k2));
    }

    @Override
    public boolean updateStatusBy(SFunction<T, ?> field, Object value, SFunction<T, ?> status, Object oldVal, Object newVal) {
        notNull(value, "字段值不能为空！");
        return updateStatusBy(field, singletonList(value), status, oldVal, newVal);
    }

    @Override
    public boolean updateStatusBy(SFunction<T, ?> field, Collection<?> values, SFunction<T, ?> status, Object oldVal, Object newVal) {
        // 参数校验
        Assert.notNull(status); Assert.notNull(field);
        notNull(newVal, "要修改的状态值不能为空！");
        notEmpty(values, "字段值不能为空！");
        // 进行更新操作（为什么new一个对象实例，因为mp的自动填充需要走对象）
        return update(ReflectUtil.newInstance(getEntityClass()), Wrappers.lambdaUpdate(getEntityClass())
                .set(status, newVal)
                .in(field, values)
                .eq(nonNull(oldVal), status, oldVal)
        );
    }
    // endregion


    // region ======== 业务方法 ========

    protected T fromAddParam(A param) {
        String format = String.format("请先覆写\"%s\"的\"fromAddParam\"方法！", getClass().getName());
        throw new UnsupportedOperationException(format);
    }

    protected T fromEditParam(R old, E param) {
        // 该设值一定不能忘记
        //T entity = BeanUtil.beanToBean(param, T.class);
        //entity.setId(old.getId());
        String format = String.format("请先覆写\"%s\"的\"fromEditParam\"方法！", getClass().getName());
        throw new UnsupportedOperationException(format);
    }

    /**
     * 从编辑入参中提取待置空字段（入参继承 BaseEditParam 且携带非空 clearFields 时才有效）.
     * @param param 编辑入参
     * @return 待置空的字段名集合，无需置空时返回 null
     */
    protected Collection<String> extractClearFields(E param) {
        if (!(param instanceof BaseEditParam)) { return null; }
        Collection<String> clearFields = ((BaseEditParam) param).getClearFields();
        return CollUtil.isNotEmpty(clearFields) ? clearFields : null;
    }

    @Override
    public Long addRecord(A param) {
        // 参数校验和常量声明
        validateToThrow(param);
        final String mtd = "addRecord";
        // 转换成实体
        T entity = fromAddParam(param);
        // 保存款式信息表数据（创建人、更新人会自动填充）
        isTrue(save(entity), recordSaveFailure);
        // 从对象中获取ID字段
        Long id = refGetId(entity);
        // 构建变更日志（新增事件统一按入参对象和 AddParam 口径记录）
        changeLog(id, Nil.OBJ, param, getAddParamClass(), mtd);
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Long> addBatch(List<A> params) {
        // 参数校验和常量声明
        notEmpty(params, paramIsRequired);
        for (A p : params) { validateToThrow(p); }
        final String mtd = "addBatch";
        // 转换成实体（中间可能有特殊逻辑）
        List<T> entityList = new ArrayList<>();
        for (A param : params) {
            entityList.add(fromAddParam(param));
        }
        // 批量保存数据（创建人、更新人会自动填充）
        isTrue(saveBatch(entityList), recordSaveFailure);
        // 提取Ids（params 与 entityList 按下标一一对应，日志统一按入参对象和 AddParam 口径记录）
        List<Long> ids = new ArrayList<>();
        Long id;
        for (int i = ZERO; i < entityList.size(); i++) {
            ids.add(id = refGetId(entityList.get(i)));
            changeLog(id, Nil.OBJ, params.get(i), getAddParamClass(), mtd);
        }
        return ids;
    }

    @Override
    public void editRecord(E param) {
        // 参数校验和常量声明
        validateToThrow(param);
        final String mtd = "editRecord";
        // 从对象中获取ID字段
        Long id = notNull(refGetId(param), recordIdNotNull);
        // 判断记录是否存在
        T oldEntity = getById(id);
        notNull(oldEntity, recordNotExist);
        // 转换成实体
        T entity = fromEditParam(BeanUtil.beanToBean(oldEntity, getResultClass()), param);
        // 待置空字段
        Collection<String> clearFields = extractClearFields(param);
        // 更新款式信息表数据（更新人会自动填充）：
        // 携带置空字段时与更新合并成同一条 UPDATE（updateAndClearById，主键取自实体），
        // 单笔写库完成"更新 + 置空"，失败即抛出
        if (clearFields == null) {
            isTrue(updateById(entity), recordUpdateFailure);
        } else {
            isTrue(updateAndClearById(entity, clearFields), recordUpdateFailure);
        }
        // 构建变更日志
        changeLog(id, oldEntity, param, getEditParamClass(), mtd);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Exception> editBatch(List<E> params, boolean ignoreSingleError) {
        return editBatch1(params, ps -> {
            // 查询旧数据（因为在编辑数据时，可能需要用到旧对象，所以始终查询）
            Q query = ReflectUtil.newInstance(getQueryClass());
            List<Long> ids = params.stream().filter(Objects::nonNull).map(this::refGetId)
                    .filter(Objects::nonNull).distinct().collect(Collectors.toList());
            refSetIds(query, Assert.notEmpty(ids));
            return query;
        }, (param, rst) -> {
            if (param != null) {
                return String.valueOf(Assert.notNull(refGetId(param)));
            } else {
                return String.valueOf(Assert.notNull(refGetId(rst)));
            }
        }, ignoreSingleError, true);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Exception> editBatch1(List<E> params, Function<List<E>, Q> queryBuilder,
                                             BiFunction<E, R, String> mapKeyBuilder,
                                             boolean ignoreSingleError,
                                             boolean skipDelete) {
        // 参数校验和常量声明
        notNull(params, paramIsRequired);
        for (E p : params) { validateToThrow(p); }
        final String mtd = "editBatch1";
        // 查询旧数据
        /*XxxxQuery query = new XxxxQuery();
        query.setOtherIdList(params.stream().filter(Objects::nonNull)
                .map(XxxxEditParam::getOtherId).distinct().collect(Collectors.toList()));*/
        List<R> oldList = queryList(queryBuilder.apply(params));
        if (oldList == null) { oldList = emptyList(); }
        // 转换成Map
        Map<String, R> oldMap = oldList.stream().filter(Objects::nonNull).collect(Collectors.toMap(
                (item) -> mapKeyBuilder.apply(Nil.g(), item),
                Function.identity(),
                (k1, k2) -> k2,
                LinkedHashMap::new
        ));
        // 声明待增加、待编辑、待删除的集合
        List<T> willAdd = new ArrayList<>(), willEdit = new ArrayList<>();
        Set<Long> willDel = new LinkedHashSet<>();
        Map<String, Exception> errors = new LinkedHashMap<>();
        // 携带置空字段的编辑项（与更新合并成单条 UPDATE，不进批量更新集合）
        Map<Long, T> willClearEntity = new LinkedHashMap<>();
        Map<Long, Collection<String>> willClearFields = new LinkedHashMap<>();
        // 编辑失败的旧记录的ID集合（编辑失败 != 删除，这些记录不能进入待删除集合）
        Set<Long> errorIds = new HashSet<>();
        // 遍历，区分待增加的和待编辑的（基础：所有数据都是传入的）
        for (E param : params) {
            // 构建 Map 的 key，并且查询 旧数据
            String mapKey = mapKeyBuilder.apply(param, Nil.g());
            R old = oldMap.get(mapKey);
            // 声明 错误对象 和 ID 变量
            Exception error = null;
            Long id;
            if (old != null) {
                try {
                    // 设值 id 到 param
                    refSetId(param, id = refGetId(old));
                    // 旧对象不为空，则是【编辑】
                    T e = fromEditParam(old, param);
                    // 携带置空字段的编辑项单独收集（后续与更新合并成单条 UPDATE），其余照常批量编辑
                    Collection<String> clearFields = extractClearFields(param);
                    if (clearFields == null) {
                        willEdit.add(e);
                    } else {
                        willClearEntity.put(id, e);
                        willClearFields.put(id, clearFields);
                    }
                    // 构建变更日志
                    changeLog(id, old, param, getEditParamClass(), mtd);
                } catch (Exception e) {
                    if (!ignoreSingleError) { throw e; }
                    else { log.debug(ERROR, error = e); }
                }
            } else {
                try {
                    // 旧对象为空，则是【新增】
                    T e = fromAddParam(BeanUtil.beanToBean(param, getAddParamClass()));
                    willAdd.add(e);
                    // 设值 id 到 param（如果 id 是在 fromAddParam 生成的，则可以带出去）
                    // （自增ID就不考虑了，毕竟有点折腾，而且批量更新方法的入参中提取新增的数据的ID的概率太小了）
                    refSetId(param, id = refGetId(e));
                    // 构建变更日志（新增事件统一按入参对象和 AddParam 口径记录）
                    changeLog(id, Nil.OBJ, param, getAddParamClass(), mtd);
                } catch (Exception e) {
                    if (!ignoreSingleError) { throw e; }
                    else { log.debug(ERROR, error = e); }
                }
            }
            // 记录错误对象
            if (error != null) {
                errors.put(mapKey, error);
                if (old != null) { errorIds.add(refGetId(old)); }
            }
        }
        // 区分待删除的（查询到的 - 当前的，编辑失败的记录除外）
        if (!skipDelete && CollUtil.isNotEmpty(oldList)) {
            willDel.addAll(oldList.stream().map(this::refGetId).collect(Collectors.toSet()));
            willDel.removeAll(willEdit.stream().map(this::refGetId).collect(Collectors.toSet()));
            willDel.removeAll(willClearEntity.keySet());
            willDel.removeAll(errorIds);
        }
        // 进行批量增加、批量编辑和批量删除
        if (!skipDelete && CollUtil.isNotEmpty(willDel)) {
            // 记录删除数据的变更日志
            oldList.stream().filter(Objects::nonNull).forEach(item -> {
                Long id = refGetId(item);
                if (!willDel.contains(id)) { return; }
                changeLog(id, item, Nil.OBJ, getEditParamClass(), mtd);
            });
            // 具体的批量删除的逻辑
            isTrue(removeByIds(willDel), recordDeleteFailure);
        }
        if (CollUtil.isNotEmpty(willAdd)) { isTrue(saveBatch(willAdd), recordSaveFailure); }
        if (CollUtil.isNotEmpty(willEdit)) { isTrue(updateBatchById(willEdit), recordUpdateFailure); }
        // 携带置空字段的编辑项：与更新合并成同一条 UPDATE（updateAndClearById，主键取自实体），
        // 失败即抛出，整个批量编辑回滚
        for (Map.Entry<Long, T> entry : willClearEntity.entrySet()) {
            isTrue(updateAndClearById(entry.getValue(), willClearFields.get(entry.getKey()))
                    , recordUpdateFailure);
        }
        // 错误信息返回
        return errors;
    }

    @Override
    public void enable(SFunction<T, ?> field, Serializable value, SFunction<T, ?> enabled, Integer enabledVal) {
        // 参数校验
        Assert.notNull(enabled); Assert.notNull(field);
        isTrue(enabledVal != null && (enabledVal == ZERO || enabledVal == ONE)
                , "启用/禁用状态值不能为空，且只能是0或1！");
        notNull(value, recordIdNotNull);
        // 统一按集合处理（value 兼容单值和集合两种传法）
        Collection<?> values = value instanceof Collection ? (Collection<?>) value : singletonList(value);
        notEmpty(values, recordIdNotNull);
        // 查询数据（存在性检查与更新条件用同一个字段，已经配置逻辑删除了）
        List<T> oldList = list(Wrappers.lambdaQuery(getEntityClass()).in(field, values));
        // 传入的每一个值都必须能查到记录（field 语义上是唯一标识，数量对得上即全部存在）
        isTrue(oldList.size() >= new HashSet<>(values).size(), recordNotExist);
        // 已经是预期状态（查出的记录全部达标才跳过，部分达标则统一拉齐）
        if (oldList.stream().allMatch(item -> ObjUtil.equal(enabled.apply(item), enabledVal))) { return; }
        // 更新状态
        isTrue(update(ReflectUtil.newInstance(getEntityClass()), Wrappers.lambdaUpdate(getEntityClass())
                .set(enabled, enabledVal)
                .in(field, values)
        ), recordUpdateFailure);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean dragSort(BiConsumer<T, Long> sort, List<DragSortDTO> sorts) {
        if (CollUtil.isEmpty(sorts)) { return false; }
        Assert.notNull(sort);
        // 过滤掉 [{}, {}, null] 和 [{}, {sort: null}]
        sorts = sorts.stream().filter(Objects::nonNull)
                .filter(item -> Objects.nonNull(item.getSort()))
                .collect(Collectors.toList());
        // 全部无效时无需更新（避免空集合走批量更新的不确定行为）
        if (CollUtil.isEmpty(sorts)) { return false; }
        // 逐项校验（id 是更新定位键，缺失会导致该条排序静默丢失）
        for (DragSortDTO item : sorts) { validateToThrow(item); }
        // 将 sorts 中的 sort 取出来重新排序
        List<Long> ascSortList = sorts.stream().map(DragSortDTO::getSort)
                .sorted().collect(Collectors.toList());
        // 尽管将 ascSortList 重新排序了，但是它的长度和 sorts 仍然是一致的
        List<T> updateList = new ArrayList<>();
        for (int i = ZERO; i < sorts.size(); i++) {
            T entity = BeanUtil.beanToBean(sorts.get(i), getEntityClass());
            sort.accept(entity, ascSortList.get(i));
            updateList.add(entity);
        }
        // 进行批量更新操作
        return updateBatchById(updateList);
    }

    @Override
    public void deleteById(Serializable id) {
        // 参数校验和常量声明
        notNull(id, recordIdNotNull);
        final String mtd = "deleteById";
        // 判断记录是否存在
        T oldEntity = getById(id);
        notNull(oldEntity, recordNotExist);
        // 删除（已配置逻辑删除）
        isTrue(removeById(id), recordDeleteFailure);
        // 构建变更日志
        changeLog(id, oldEntity, Nil.OBJ, getEditParamClass(), mtd);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByLongIds(Collection<Long> ids) {
        // 参数校验、处理和常量声明
        notEmpty(ids, paramIsRequired);
        final String mtd = "deleteByIds";
        ids = CollUtil.distinct(ids);
        // 判断记录是否存在
        Map<Long, T> oldMap = mapByIds(ids);
        notEmpty(oldMap, recordNotExist);
        for (Long id : ids) {
            if (id == null) { continue; }
            notNull(oldMap.get(id), recordNotExist);
        }
        // 删除（已配置逻辑删除）
        isTrue(removeByIds(ids), recordDeleteFailure);
        // 构建变更日志
        T old; for (Long id : ids) {
            if (id == null || (old = oldMap.get(id)) == null) { continue; }
            changeLog(id, old, Nil.OBJ, getEditParamClass(), mtd);
        }
    }

    @Deprecated
    @Override
    public R detailById(Serializable id) {
        /*// 参数校验
        notNull(id, recordIdNotNull);
        // 查询数据（已经配置逻辑删除了）
        T entity = getById(id);
        R result = BeanUtil.beanToBean(entity, getResultClass());
        // 填充其他信息
        QueryMode queryMode = QueryMode.FULL;
        if (result != null) {
            // 处理数据
            if (allowProcess(queryMode)) { processData(Nil.g(), singletonList(result)); }
            // 填充数据
            if (allowFill(queryMode)) { fillingData(Nil.g(), singletonList(result)); }
        }
        // 返回结果
        return result;*/
        return getById2(id);
    }

    @Override
    public R getById1(Serializable id) {

        return getById1(id, QueryMode.FULL);
    }

    @Override
    public R getById1(Serializable id, QueryMode queryMode) {
        /*// 参数校验
        notNull(id, recordIdNotNull);
        // 构建查询对象
        Q query = ReflectUtil.newInstance(getQueryClass());
        if (queryMode != null) {
            query.setQueryMode(queryMode);
        }
        query.setPageSize(ONE);
        query.setPageNum(ONE);
        // 给查询对象设置ID
        refSetIds(query, id);
        // 查询数据
        Page<R> page = queryPage(query);
        if (page == null) { return null; }
        if (CollUtil.isEmpty(page.getData())) { return null; }
        // 返回结果
        return getFirst(page.getData());*/
        // 参数校验
        notNull(id, recordIdNotNull);
        // 查询数据（已经配置逻辑删除了）
        T entity = getById(id);
        R result = BeanUtil.beanToBean(entity, getResultClass());
        // 填充其他信息
        if (result != null) {
            // 处理数据
            if (allowProcess(queryMode)) { processData(Nil.g(), singletonList(result)); }
            // 填充数据
            if (allowFill(queryMode)) { fillingData(Nil.g(), singletonList(result)); }
        }
        // 返回结果
        return result;
    }

    @Override
    public R getById2(Serializable id) {

        return getById2(id, QueryMode.FULL);
    }

    @Override
    public R getById2(Serializable id, QueryMode queryMode) {
        // 参数校验
        notNull(id, recordIdNotNull);
        // 构建查询对象
        Q query = ReflectUtil.newInstance(getQueryClass());
        if (queryMode != null) {
            query.setQueryMode(queryMode);
        }
        query.setPageSize(ONE);
        query.setPageNum(ONE);
        // 给查询对象设置ID
        refSetIds(query, id);
        // 查询数据
        Page<R> page = queryPage(query);
        if (page == null) { return null; }
        if (CollUtil.isEmpty(page.getData())) { return null; }
        // 返回结果
        return getFirst(page.getData());
    }

    @Override
    public long queryCount(Q query) {
        // 参数校验，默认值处理
        query = prepareQuery(query);
        validateToThrow(query);
        // 查询
        return count(buildQueryWrapper(query));
    }

    @Override
    public boolean queryExist(Q query) {

        return queryCount(query) > ZERO;
    }

    @Override
    public Page<R> queryPage(Q query) {
        // 参数校验，默认值处理
        query = prepareQuery(query);
        validateToThrow(query);
        // 构建查询条件（必须先于分页：条件构建若抛异常，
        // 分页插件的 ThreadLocal 会残留并污染该线程后续的查询）
        MPJLambdaWrapper<T> queryWrapper = buildQueryWrapper(query);
        // 应用前端的自定义排序（未传排序参数时无操作；字段未命中白名单时跳过该项并告警）
        applySort(queryWrapper, getEntityClass(), query.getSortFields(), Nil.g());
        // 分页
        if (query.isPaged()) {
            PageUtil.startPage(query.getPageNum(), query.getPageSize());
        }
        // 查询
        List<T> list = list(queryWrapper);
        // 判空+结果处理（空结果也走统一组装：data 为空集合而非 null，分页场景保留 total = 0）
        if (CollUtil.isEmpty(list)) { return PageUtil.handleResult(list, getResultClass()); }
        Page<R> result = PageUtil.handleResult(list, getResultClass());
        // 添加序号
        if (query.isPaged()) {
            PageUtil.fillSerialNumber(result.getData(), result.getPageNum(), result.getPageSize());
        }
        // 处理数据
        if (allowProcess(query)) { processData(query, result.getData()); }
        // 填充数据
        if (allowFill(query)) { fillingData(query, result.getData()); }
        // 返回
        return result;
    }

    @Override
    public Page<R> queryScrollPage(Q query, SFunction<T, ?> idField) {
        // 参数校验，默认值处理
        Assert.notNull(idField);
        query = prepareQuery(query);
        validateToThrow(query);
        // 浅拷贝一份查询对象再使用，避免就地修改调用方传入的对象（调用方可能复用同一 query）
        query = BeanUtil.beanToBean(query, getQueryClass());
        // 是否正序，true 正序，false 倒序
        if (query.getScrollByAsc() == null) {
            query.setScrollByAsc(false);
        }
        // 分页大小（非法值回落默认；超过上限直接报错，防止误传或恶意的超大 pageSize 拖垮内存）
        String  scrollId = query.getScrollId();
        Integer pageSize = query.getPageSize();
        if (pageSize == null || pageSize < ONE) { pageSize = PageUtil.getDefaultPageSize(); }
        Integer maxPageSize = PageUtil.getMaxPageSize();
        isTrue(pageSize <= maxPageSize, "分页大小不能超过%s！", maxPageSize);
        // 查询（滚动条件是本方法的核心语义，恒定生效，不受 paged 开关影响）
        MPJLambdaWrapper<T> queryWrapper = buildQueryWrapper(query);
        if (query.getScrollByAsc()) {
            // 正序
            queryWrapper.gt(isNotBlank(scrollId), idField, scrollId)
                    .orderByAsc(idField)
                    .last("limit " + pageSize);
        } else {
            // 倒序
            queryWrapper.lt(isNotBlank(scrollId), idField, scrollId)
                    .orderByDesc(idField)
                    .last("limit " + pageSize);
        }
        List<T> list = list(queryWrapper);
        // 判空+结果处理
        Page<R> result = Page.of();
        result.setPageSize(pageSize);
        if (CollUtil.isEmpty(list)) {
            // 当前滚动ID查不到数据，继续传入当前滚动ID
            result.setScrollId(scrollId);
            return result;
        }
        // 有数据的情况下，提取新的滚动ID（注意：ID不能为空）
        Long lastId = refGetId(CollUtil.getLast(list));
        notNull(lastId, "数据异常！");
        result.setScrollId(String.valueOf(lastId));
        // 将查询到的数据填充进去
        result.setData(BeanUtil.beanToBeanInList(list, getResultClass()));
        // 处理数据
        if (allowProcess(query)) { processData(query, result.getData()); }
        // 填充数据
        if (allowFill(query)) { fillingData(query, result.getData()); }
        // 返回
        return result;
    }

    @Override
    public List<R> queryList(Q query) {
        // 浅拷贝一份再关闭分页，避免就地修改调用方传入的查询对象（调用方可能复用同一 query）
        query = BeanUtil.beanToBean(prepareQuery(query), getQueryClass());
        query.setPaged(false);
        return queryPage(query).getData();
    }

    @Override
    public <K> Map<K, R> queryMap(Q query, Function<R, K> keyMapper) {
        return Optional.ofNullable(queryList(query))
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(keyMapper, Function.identity(), (k1, k2) -> k2, LinkedHashMap::new));
    }

    @Override
    public <K> Map<K, List<R>> queryGroupMap(Q query, Function<R, K> keyMapper) {
        return Optional.ofNullable(queryList(query))
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(keyMapper));
    }
    // endregion


    // region ======== 需要覆写的方法 ========
    /**
     * 将查询对象转换成查询条件.
     * @param query 查询对象
     * @return 查询条件
     */
    protected abstract MPJLambdaWrapper<T> buildQueryWrapper(Q query);

    /**
     * 应用自定义排序：默认经 {@link SortUtil} 门面按排序目标类型路由执行（与门面同形，便于覆写）.
     * <p>主表与跨表（联表属性唯一命中时自动输出 别名.列名）排序零配置；虚拟/计算排序键
     * （CASE WHEN、算式、子查询，库里不存在这一列的）与跨表重名消歧，覆写本方法在 options 为空时
     * 补充"字段名 → 列名/列表达式"映射（经约定 key {@code MyBatisPlusSorter.OPTION_COLUMN_MAPPING} 传入），例如：
     * <pre>{@code
     * private static final Map<String, String> SORT_EXPR_MAP = new HashMap<>();
     * static {
     *     SORT_EXPR_MAP.put("unpaidAmount", "(total_amount - paid_amount)");  // 虚拟排序键：算出来的列
     *     SORT_EXPR_MAP.put("name", "t.name");                               // 重名消歧：明确用主表
     * }
     *
     * @Override
     * protected boolean applySort(MPJLambdaWrapper<PaymentRecord> wrapper, Class<?> entityClass,
     *         List<SortField> sortFields, Map<String, Object> options) {
     *     if (CollUtil.isEmpty(options)) {
     *         options = Dict.of(MyBatisPlusSorter.OPTION_COLUMN_MAPPING, SORT_EXPR_MAP);
     *     }
     *     return super.applySort(wrapper, entityClass, sortFields, options);
     * }
     * }</pre>
     *
     * @param wrapper 查询条件（排序目标）
     * @param entityClass 实体类型
     * @param sortFields 排序参数（可为空，为空时无操作）
     * @param options 通用扩展选项（命名选项包，可为 null）
     * @return 有任意一项排序实际生效返回 true
     */
    protected boolean applySort(MPJLambdaWrapper<T> wrapper, Class<?> entityClass, List<SortField> sortFields,
                                Map<String, Object> options) {
        return SortUtil.applySort(wrapper, entityClass, sortFields, options);
    }

    /**
     * 统一的业务数据的处理逻辑
     * @param query 查询条件（可以为空）
     * @param data 待处理/填充的数据
     */
    protected abstract void processData(Q query, List<R> data);

    /**
     * 统一的业务数据的填充逻辑
     * @param query 查询条件（可以为空）
     * @param data 待处理/填充的数据
     */
//    protected abstract void fillingData(Q query, List<R> data);
    protected void fillingData(Q query, List<R> data) {

        // todo 以后给它做成抽象方法，目前先有默认实现
    }

    @Deprecated
    protected void recordChangeLog(Object bId, Object old, Object newDt) { changeLog(bId, old, newDt, Nil.CLZ); }
    @Deprecated
    protected void recordChangeLog(Object bId, Object old, Object newDt, Class<?> tCls) { changeLog(bId, old, newDt, tCls); }

    /**
     * 记录数据的变动日志.
     * @param bizId 业务数据的主键ID
     * @param oldData 改动前的业务数据对象
     * @param newData 改动后的业务数据对象
     * @param targetClz 目标类型
     * @param arguments 留有的额外的口子，用于灵活的传递其他参数
     *                  在大部分场景下，arguments[0] = String methodLabel
     *                                arguments[1] = boolean ignoreNullNewValue
     */
    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
        // todo 以后给它做成抽象方法，目前先有默认实现
        String format = String.format("请先覆写\"%s\"的\"changeLog\"方法！", getClass().getName());
        throw new UnsupportedOperationException(format);
    }

    // endregion

}
