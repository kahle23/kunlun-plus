//package baibao.extension.mybatisplus;
//
//import baibao.common.dto.base.BaseQuery;
//import baibao.common.dto.DragSortDTO;
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.convert.Convert;
//import cn.hutool.core.util.ObjUtil;
//import cn.hutool.core.util.ReflectUtil;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
//import com.github.yulichang.base.MPJBaseMapper;
//import com.github.yulichang.wrapper.MPJLambdaWrapper;
//import kunlun.common.Page;
//import kunlun.common.constant.Nil;
//import kunlun.core.function.BiConsumer;
//import kunlun.data.bean.BeanUtil;
//import kunlun.db.jdbc.mybatisplus.base.ServiceImpl;
//import kunlun.util.Assert;
//import kunlun.util.PageUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.Serializable;
//import java.util.*;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static cn.hutool.core.collection.CollUtil.getFirst;
//import static com.baomidou.mybatisplus.core.toolkit.ReflectionKit.getSuperClassGenericType;
//import static java.util.Collections.*;
//import static java.util.Objects.nonNull;
//import static kunlun.common.Errors.*;
//import static kunlun.common.constant.Numbers.*;
//import static kunlun.common.constant.Words.ERROR;
//import static kunlun.data.validation.support.javax.ValidationUtil.validateToThrow;
//import static kunlun.exception.util.VerifyUtil.*;
//
///**
// * BaseServiceImpl
// * @author Kahle
// */
//@Deprecated
//@SuppressWarnings({"unused"})
//public abstract class BaseServiceImpl<M extends MPJBaseMapper<T>, T, A, E, Q extends BaseQuery, R>
//        extends ServiceImpl<M, T> implements BaseService<T, A, E, Q, R> {
//    private static final Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);
//    private static final String FIELD_ID = "id";
//
//    // region ======== 内置方法 ========
//
//    @SuppressWarnings("unchecked")
//    private final Class<R> resultClass = (Class<R>) getSuperClassGenericType(getClass(), BaseServiceImpl.class, FIVE);
//    @SuppressWarnings("unchecked")
//    private final Class<Q> queryClass = (Class<Q>) getSuperClassGenericType(getClass(), BaseServiceImpl.class, FOUR);
//    @SuppressWarnings("unchecked")
//    private final Class<E> editParamClass = (Class<E>) getSuperClassGenericType(getClass(), BaseServiceImpl.class, THREE);
//    @SuppressWarnings("unchecked")
//    private final Class<A> addParamClass = (Class<A>) getSuperClassGenericType(getClass(), BaseServiceImpl.class, TWO);
//
//    protected Long refGetId(Object obj) {
//        if (obj == null) { return null; }
//        Assert.isTrue(ReflectUtil.hasField(obj.getClass(), FIELD_ID));
//        return Convert.toLong(ReflectUtil.getFieldValue(obj, FIELD_ID));
//    }
//
//    protected E refSetId(E param, Long id) {
//        if (param == null || id == null) { return param; }
//        Assert.isTrue(ReflectUtil.hasField(param.getClass(), FIELD_ID));
//        ReflectUtil.setFieldValue(param, FIELD_ID, id);
//        return param;
//    }
//
//    protected Q refSetIds(Q query, Object ids) {
//        // 判空
//        if (query == null || ids == null) { return query; }
//        // ID对象的类型校验（只能是 Long 和 集合，集合的泛型就不判断了）
//        Assert.isTrue(ids instanceof Long || ids instanceof Collection);
//        boolean isColl = ids instanceof Collection;
//        // 给查询对象设置ID
//        String field;
//        if (ReflectUtil.hasField(getQueryClass(), field = "idList")) {
//            ReflectUtil.setFieldValue(query, field, isColl ? ids : singletonList(ids));
//        } else if (ReflectUtil.hasField(getQueryClass(), field = "ids")) {
//            ReflectUtil.setFieldValue(query, field,  isColl ? ids : singletonList(ids));
//        } else if (ReflectUtil.hasField(getQueryClass(), FIELD_ID)) {
//            Assert.isFalse(isColl);
//            ReflectUtil.setFieldValue(query, FIELD_ID, ids);
//        } else {
//            String msg = String.format("查询对象“%s”不包含字段“idList”、“ids” 和 “id”！", getQueryClass().getName());
//            throw new IllegalArgumentException(msg);
//        }
//        // 返回 query 对象
//        return query;
//    }
//
//    @Override
//    public Class<R> getResultClass() {
//
//        return resultClass;
//    }
//
//    @Override
//    public Class<Q> getQueryClass() {
//
//        return queryClass;
//    }
//
//    @Override
//    public Class<E> getEditParamClass() {
//
//        return editParamClass;
//    }
//
//    @Override
//    public Class<A> getAddParamClass() {
//
//        return addParamClass;
//    }
//
//    // endregion
//
//
//    // region ======== 通用方法 ========
//
//    @Override
//    public Map<Long, T> mapByIds(Collection<Long> ids) {
//        if (CollUtil.isEmpty(ids)) { return emptyMap(); }
//        return Optional.ofNullable(listByIds(ids))
//                .map(List::stream)
//                .orElseGet(Stream::empty)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toMap(this::refGetId, Function.identity(), (k1, k2) -> k2));
//    }
//
//    @Override
//    public boolean updateStatusBy(SFunction<T, ?> field, Object value, SFunction<T, ?> status, Object oldVal, Object newVal) {
//        notNull(value, "字段值不能为空！");
//        return updateStatusBy(field, singletonList(value), status, oldVal, newVal);
//    }
//
//    @Override
//    public boolean updateStatusBy(SFunction<T, ?> field, Collection<?> values, SFunction<T, ?> status, Object oldVal, Object newVal) {
//        // 参数校验
//        Assert.notNull(status); Assert.notNull(field);
//        notNull(newVal, "要修改的状态值不能为空！");
//        notEmpty(values, "字段值不能为空！");
//        // 进行更新操作（为什么new一个对象实例，因为mp的自动填充需要走对象）
//        return update(ReflectUtil.newInstance(getEntityClass()), Wrappers.lambdaUpdate(getEntityClass())
//                .set(status, newVal)
//                .in(field, values)
//                .eq(nonNull(oldVal), status, oldVal)
//        );
//    }
//    // endregion
//
//
//    // region ======== 业务方法 ========
//
//    protected T fromAddParam(A param) {
//        String format = String.format("请先覆写\"%s\"的\"fromAddParam\"方法！", getClass().getName());
//        throw new UnsupportedOperationException(format);
//    }
//
//    protected T fromEditParam(R old, E param) {
//        String format = String.format("请先覆写\"%s\"的\"fromEditParam\"方法！", getClass().getName());
//        throw new UnsupportedOperationException(format);
//    }
//
//    @Override
//    public Long addRecord(A param) {
//        // 参数校验
//        validateToThrow(param);
//        // 转换成实体
//        T entity = fromAddParam(param);
//        // 保存款式信息表数据（创建人、更新人会自动填充）
//        isTrue(save(entity), recordSaveFailure);
//        // 从对象中获取ID字段
//        Long id = refGetId(entity);
//        // 构建变更日志
//        String mtd = "addRecord";
//        changeLog(id, Nil.OBJ, param, getEditParamClass(), mtd);
//        return id;
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public List<Long> addBatch(List<A> params) {
//        // 参数校验
//        notEmpty(params, paramIsRequired);
//        for (A p : params) { validateToThrow(p); }
//        // 转换成实体（中间可能有特殊逻辑）
//        List<T> entityList = new ArrayList<>();
//        for (A param : params) {
//            entityList.add(fromAddParam(param));
//        }
//        // 批量保存数据（创建人、更新人会自动填充）
//        isTrue(saveBatch(entityList), recordSaveFailure);
//        // 提取Ids
//        List<Long> ids = new ArrayList<>();
//        String mtd = "addBatch"; Long id;
//        for (T entity : entityList) {
//            ids.add(id = refGetId(entity));
//            changeLog(id, Nil.OBJ, entity, getEditParamClass(), mtd);
//        }
//        return ids;
//    }
//
//    @Override
//    public void editRecord(E param) {
//        // 参数校验
//        validateToThrow(param);
//        // 从对象中获取ID字段
//        Long id = notNull(refGetId(param), recordIdNotNull);
//        // 判断记录是否存在
//        T oldEntity = getById(id);
//        notNull(oldEntity, recordNotExist);
//        // 转换成实体
//        T entity = fromEditParam(BeanUtil.beanToBean(oldEntity, getResultClass()), param);
//        // 更新款式信息表数据（更新人会自动填充）
//        isTrue(updateById(entity), recordUpdateFailure);
//        // 构建变更日志
//        String mtd = "editRecord";
//        changeLog(id, oldEntity, param, getEditParamClass(), mtd);
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public List<Exception> editBatch(List<E> params, boolean ignSinErr) {
//        // 参数校验
//        notNull(params, paramIsRequired);
//        for (E p : params) { validateToThrow(p); }
//        // 查询旧数据（因为在编辑数据时，可能需要用到旧对象，所以始终查询）
//        Q query = ReflectUtil.newInstance(getQueryClass());
//        List<Long> ids = params.stream().filter(Objects::nonNull)
//                .map(this::refGetId).filter(Objects::nonNull)
//                .distinct().collect(Collectors.toList());
//        Map<Long, R> oldMap = CollUtil.isNotEmpty(ids)
//                ? queryMap(refSetIds(query, ids), this::refGetId) : emptyMap();
//        // 声明待增加、待编辑、待删除的集合
//        List<T> willAdd = new ArrayList<>(), willEdit = new ArrayList<>();
//        List<Exception> errors = new ArrayList<>();
//        String mtd = "editBatch";
//        // 遍历，区分待增加的和待编辑的（基础：所有数据都是传入的）
//        for (E param : params) {
//            if (param == null) { continue; }
//            Exception error = null; Long id;
//            if ((id = refGetId(param)) != null) {
//                try {
//                    willEdit.add(fromEditParam(oldMap.get(id), param));
//                } catch (Exception e) {
//                    if (!ignSinErr) { throw e; }
//                    else { log.debug(ERROR, error = e); }
//                }
//            } else {
//                try {
//                    T e = fromAddParam(BeanUtil.beanToBean(param, getAddParamClass()));
//                    // 此处为何要设值 id 到 param，如果 id 是在 fromAddParam 生成的，则可以带出去
//                    // （自增ID就不考虑了，毕竟有点折腾，而且批量更新方法的入参中提取新增的数据的ID的概率太小了）
//                    refSetId(param, refGetId(e)); willAdd.add(e);
//                } catch (Exception e) {
//                    if (!ignSinErr) { throw e; }
//                    else { log.debug(ERROR, error = e); }
//                }
//            }
//            if (error != null) { errors.add(error); }
//        }
//        // 进行批量增加
//        if (CollUtil.isNotEmpty(willAdd)) {
//            isTrue(saveBatch(willAdd), recordSaveFailure);
//            for (T entity : willAdd) {
//                changeLog(refGetId(entity), Nil.g(), entity, getAddParamClass(), mtd);
//            }
//        }
//        // 进行批量编辑
//        if (CollUtil.isNotEmpty(willEdit)) {
//            isTrue(updateBatchById(willEdit), recordUpdateFailure);
//            for (T entity : willEdit) { Long id = refGetId(entity);
//                changeLog(id, oldMap.get(id), entity, getEditParamClass(), mtd);
//            }
//        }
//        return errors;
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public List<Exception> editBatch1(List<E> params, Function<List<E>, Q> query, BiFunction<E, R, String> mapKey, boolean ignSinErr) {
//        // 参数校验
//        notNull(params, paramIsRequired);
//        for (E p : params) { validateToThrow(p); }
//        // 查询旧数据
//        /*XxxxQuery query = new XxxxQuery();
//        query.setOtherIdList(params.stream().filter(Objects::nonNull)
//                .map(XxxxEditParam::getOtherId).distinct().collect(Collectors.toList()));*/
//        List<R> oldList = queryList(query.apply(params));
//        if (oldList == null) { oldList = emptyList(); }
//        // 转换成Map
//        Map<String, R> oldMap = oldList.stream().filter(Objects::nonNull).collect(Collectors.toMap(
//                (item) -> mapKey.apply(Nil.g(), item), Function.identity(), (k1, k2) -> k2));
//        // 声明待增加、待编辑、待删除的集合
//        List<T> willAdd = new ArrayList<>(), willEdit = new ArrayList<>();
//        List<Long> willDel = new ArrayList<>(); String mtd = "editBatch1";
//        List<Exception> errors = new ArrayList<>();
//        // 遍历，区分待增加的和待编辑的（基础：所有数据都是传入的）
//        for (E param : params) {
//            R old = oldMap.get(mapKey.apply(param, Nil.g()));
//            Exception error = null; Long id;
//            if (old != null) {
//                try {
//                    refSetId(param, id = refGetId(old)); willEdit.add(fromEditParam(old, param));
//                    // 构建变更日志
//                    changeLog(id, old, param, getEditParamClass(), mtd);
//                } catch (Exception e) {
//                    if (!ignSinErr) { throw e; }
//                    else { log.debug(ERROR, error = e); }
//                }
//            } else {
//                try {
//                    T e = fromAddParam(BeanUtil.beanToBean(param, getAddParamClass()));
//                    // 此处为何要设值 id 到 param，如果 id 是在 fromAddParam 生成的，则可以带出去
//                    // （自增ID就不考虑了，毕竟有点折腾，而且批量更新方法的入参中提取新增的数据的ID的概率太小了）
//                    refSetId(param, id = refGetId(e)); willAdd.add(e);
//                    // 构建变更日志
//                    changeLog(id, Nil.OBJ, e, getAddParamClass(), mtd);
//                } catch (Exception e) {
//                    if (!ignSinErr) { throw e; }
//                    else { log.debug(ERROR, error = e); }
//                }
//            }
//            if (error != null) { errors.add(error); }
//        }
//        // 区分待删除的（查询到的 - 当前的）
//        if (CollUtil.isNotEmpty(willEdit)) {
//            willDel.addAll(oldList.stream().map(this::refGetId).distinct().collect(Collectors.toList()));
//            willDel.removeAll(willEdit.stream().map(this::refGetId).distinct().collect(Collectors.toList()));
//        }
//        // 进行批量增加、批量编辑和批量删除
//        if (CollUtil.isNotEmpty(willDel)) {
//            oldList.stream().filter(Objects::nonNull).forEach(item -> {
//                Long id = refGetId(item); if (!willDel.contains(id)) { return; }
//                changeLog(id, item, Nil.OBJ, getEditParamClass(), mtd);
//            });
//            isTrue(removeByIds(willDel), recordDeleteFailure);
//        }
//        if (CollUtil.isNotEmpty(willAdd)) { isTrue(saveBatch(willAdd), recordSaveFailure); }
//        if (CollUtil.isNotEmpty(willEdit)) { isTrue(updateBatchById(willEdit), recordUpdateFailure); }
//        return errors;
//    }
//
//    @Override
//    public void enable(SFunction<T, ?> field, Serializable value, SFunction<T, ?> enabled, Integer enabledVal) {
//        // 参数校验
//        Assert.notNull(enabled); Assert.notNull(field);
//        isTrue(enabledVal != null && (enabledVal == ZERO || enabledVal == ONE)
//                , "启用/禁用状态值不能为空，且只能是0或1！");
//        notNull(value, recordIdNotNull);
//        // 查询数据（已经配置逻辑删除了）
//        T oldEntity = getById(value);
//        notNull(oldEntity, recordNotExist);
//        // 已经是预期状态
//        if (ObjUtil.equal(enabled.apply(oldEntity), enabledVal)) { return; }
//        // 更新状态
//        isTrue(update(ReflectUtil.newInstance(getEntityClass()), Wrappers.lambdaUpdate(getEntityClass())
//                .set(enabled, enabledVal)
//                .in(field, value)
//        ), recordUpdateFailure);
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public boolean dragSort(BiConsumer<T, Long> sort, List<DragSortDTO> sorts) {
//        if (CollUtil.isEmpty(sorts)) { return false; }
//        Assert.notNull(sort);
//        // 过滤掉 [{}, {}, null] 和 [{}, {sort: null}]
//        sorts = sorts.stream().filter(Objects::nonNull)
//                .filter(item -> Objects.nonNull(item.getSort()))
//                .collect(Collectors.toList());
//        // 将 sorts 中的 sort 取出来重新排序
//        List<Long> ascSortList = sorts.stream().map(DragSortDTO::getSort)
//                .sorted().collect(Collectors.toList());
//        // 尽管将 ascSortList 重新排序了，但是它的长度和 sorts 仍然是一致的
//        List<T> updateList = new ArrayList<>();
//        for (int i = ZERO; i < sorts.size(); i++) {
//            T entity = BeanUtil.beanToBean(sorts.get(i), getEntityClass());
//            sort.accept(entity, ascSortList.get(i));
//            updateList.add(entity);
//        }
//        // 进行批量更新操作
//        return updateBatchById(updateList);
//    }
//
//    @Override
//    public void deleteById(Serializable id) {
//        // 参数校验
//        notNull(id, recordIdNotNull);
//        // 判断记录是否存在
//        T oldEntity = getById(id);
//        notNull(oldEntity, recordNotExist);
//        // 删除（已配置逻辑删除）
//        isTrue(removeById(id), recordDeleteFailure);
//        // 构建变更日志
//        changeLog(id, oldEntity, Nil.OBJ, getEditParamClass(), "deleteById");
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void deleteByLongIds(Collection<Long> ids) {
//        // 参数校验和处理
//        notEmpty(ids, paramIsRequired);
//        ids = CollUtil.distinct(ids);
//        // 判断记录是否存在
//        Map<Long, T> oldMap = mapByIds(ids);
//        notEmpty(oldMap, recordNotExist);
//        for (Long id : ids) {
//            if (id == null) { continue; }
//            notNull(oldMap.get(id), recordNotExist);
//        }
//        // 删除（已配置逻辑删除）
//        isTrue(removeByIds(ids), recordDeleteFailure);
//        // 构建变更日志
//        T old; for (Long id : ids) {
//            if (id == null || (old = oldMap.get(id)) == null) { continue; }
//            changeLog(id, old, Nil.OBJ, getEditParamClass(), "deleteByIds");
//        }
//    }
//
//    @Override
//    public R detailById(Serializable id) {
//        // 参数校验
//        notNull(id, recordIdNotNull);
//        // 查询数据（已经配置逻辑删除了）
//        T entity = getById(id);
//        R result = BeanUtil.beanToBean(entity, getResultClass());
//        // 填充其他信息
//        if (result != null) {
//            processData(Nil.g(), singletonList(result));
//        }
//        // 返回结果
//        return result;
//    }
//
//    @Override
//    public R detailById1(Serializable id) {
//        // 参数校验
//        notNull(id, recordIdNotNull);
//        // 构建查询对象
//        Q query = ReflectUtil.newInstance(getQueryClass());
//        query.setPageSize(ONE);
//        query.setPageNum(ONE);
//        // 给查询对象设置ID
//        refSetIds(query, id);
//        // 查询数据
//        Page<R> page = queryPage(query);
//        if (page == null) { return null; }
//        if (CollUtil.isEmpty(page.getData())) { return null; }
//        // 返回结果
//        return getFirst(page.getData());
//    }
//
//    @Override
//    public long queryCount(Q query) {
//        // 参数校验，默认值处理
//        validateToThrow(query);
//        // 查询
//        return count(buildQueryWrapper(query));
//    }
//
//    @Override
//    public boolean queryExist(Q query) {
//
//        return queryCount(query) > ZERO;
//    }
//
//    @Override
//    public Page<R> queryPage(Q query) {
//        // 参数校验，默认值处理
//        validateToThrow(query);
//        // 分页
//        if (query.isPaged()) {
//            PageUtil.startPage(query.getPageNum(), query.getPageSize());
//        }
//        // 查询
//        List<T> list = list(buildQueryWrapper(query));
//        // 判空+结果处理
//        if (CollUtil.isEmpty(list)) { return Page.of(); }
//        Page<R> result = PageUtil.handleResult(list, getResultClass());
//        // 填充数据
//        processData(query, result.getData());
//        // 返回
//        return result;
//    }
//
//    @Override
//    public List<R> queryList(Q query) {
//        if (query == null) { query = ReflectUtil.newInstance(getQueryClass()); }
//        query.setPaged(false);
//        return queryPage(query).getData();
//    }
//
//    @Override
//    public <K> Map<K, R> queryMap(Q query, Function<R, K> keyMapper) {
//        return Optional.ofNullable(queryList(query))
//                .map(List::stream)
//                .orElseGet(Stream::empty)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toMap(keyMapper, Function.identity(), (k1, k2) -> k2));
//    }
//
//    @Override
//    public <K> Map<K, List<R>> queryGroupMap(Q query, Function<R, K> keyMapper) {
//        return Optional.ofNullable(queryList(query))
//                .map(List::stream)
//                .orElseGet(Stream::empty)
//                .filter(Objects::nonNull)
//                .collect(Collectors.groupingBy(keyMapper));
//    }
//    // endregion
//
//
//    // region ======== 需要覆写的方法 ========
//    /**
//     * 将查询对象转换成查询条件.
//     * @param query 查询对象
//     * @return 查询条件
//     */
//    protected abstract MPJLambdaWrapper<T> buildQueryWrapper(Q query);
//
//    /**
//     * 统一的业务数据的处理/填充逻辑
//     * @param query 查询条件（可以为空）
//     * @param data 待处理/填充的数据
//     */
//    protected abstract void processData(Q query, List<R> data);
//
//    @Deprecated
//    protected void recordChangeLog(Object bId, Object old, Object newDt) { changeLog(bId, old, newDt, Nil.CLZ); }
//    @Deprecated
//    protected void recordChangeLog(Object bId, Object old, Object newDt, Class<?> tCls) { changeLog(bId, old, newDt, tCls); }
//
//    /**
//     * 记录数据的变动日志.
//     * @param bizId 业务数据的主键ID
//     * @param oldData 改动前的业务数据对象
//     * @param newData 改动后的业务数据对象
//     * @param targetClz 目标类型
//     * @param arguments 留有的额外的口子，用于灵活的传递其他参数
//     *                  在大部分场景下，arguments[0] = String methodLabel
//     *                                arguments[1] = boolean ignoreNullNewValue
//     */
//    protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) {
//        // todo 以后给它做成抽象方法，目前先有默认实现
//        String format = String.format("请先覆写\"%s\"的\"changeLog\"方法！", getClass().getName());
//        throw new UnsupportedOperationException(format);
//    }
//
//    // endregion
//
//}
