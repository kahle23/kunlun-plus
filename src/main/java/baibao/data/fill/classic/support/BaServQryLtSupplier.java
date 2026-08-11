/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.data.fill.classic.support;

import baibao.common.dto.base.BaseQuery;
import baibao.db.jdbc.mybatisplus.base.BaseService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import kunlun.core.function.BiConsumer;
import kunlun.core.function.Consumer;
import kunlun.core.function.Function;
import kunlun.data.fill.classic.support.BaseServDataSupplier;
import kunlun.spring.util.SpringUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static kunlun.util.Assert.notNull;

/**
 * 基于 baseService 的 queryList 的数据提供者.
 * @author Zerox
 */
public class BaServQryLtSupplier<T, A, E, Q extends BaseQuery, R> extends BaseServDataSupplier<BaseService<T, A, E, Q, R>, R> {

    public static <T, A, E, Q extends BaseQuery, R> BaServQryLtSupplier<T, A, E, Q, R> of(
            Class<? extends BaseService<T, A, E, Q, R>> clazz, Function<R, Object> keyMapper,
            BiConsumer<Q, Collection<Serializable>> queryMapper) {
        return new BaServQryLtSupplier<>(SpringUtil.getBean(clazz), keyMapper, queryMapper);
    }

    public static <T, A, E, Q extends BaseQuery, R> BaServQryLtSupplier<T, A, E, Q, R> of(
            Class<? extends BaseService<T, A, E, Q, R>> clazz, Function<R, Object> keyMapper,
            BiConsumer<Q, Collection<Serializable>> queryMapper,
            Consumer<Map<String, Object>> processor) {
        return new BaServQryLtSupplier<>(SpringUtil.getBean(clazz), keyMapper, queryMapper, processor);
    }

    public static <T, A, E, Q extends BaseQuery, R> BaServQryLtSupplier<T, A, E, Q, R> of(
            BaseService<T, A, E, Q, R> service, Function<R, Object> keyMapper,
            BiConsumer<Q, Collection<Serializable>> queryMapper) {
        return new BaServQryLtSupplier<>(service, keyMapper, queryMapper);
    }

    public static <T, A, E, Q extends BaseQuery, R> BaServQryLtSupplier<T, A, E, Q, R> of(
            BaseService<T, A, E, Q, R> service, Function<R, Object> keyMapper,
            BiConsumer<Q, Collection<Serializable>> queryMapper,
            Consumer<Map<String, Object>> processor) {
        return new BaServQryLtSupplier<>(service, keyMapper, queryMapper, processor);
    }

    private final BiConsumer<Q, Collection<Serializable>> querySetter;

    public BaServQryLtSupplier(BaseService<T, A, E, Q, R> service, Function<R, Object> keyMapper,
                               BiConsumer<Q, Collection<Serializable>> querySetter,
                               Consumer<Map<String, Object>> processor) {
        super(service, keyMapper, processor);
        this.querySetter = notNull(querySetter);
    }

    public BaServQryLtSupplier(BaseService<T, A, E, Q, R> service, Function<R, Object> keyMapper,
                               BiConsumer<Q, Collection<Serializable>> querySetter) {
        super(service, keyMapper);
        this.querySetter = notNull(querySetter);
    }

    @Override
    public Map<String, Map<String, Object>> acquire(Collection<?> coll) {
        // Parameter conversion and deduplication.
        List<Serializable> list = toList(coll, Serializable.class);
        if (CollUtil.isEmpty(list)) { return Collections.emptyMap(); }
        // Data query.
        Q query = ReflectUtil.newInstance(getService().getQueryClass());
        querySetter.accept(query, list);
        List<R> queryList = getService().queryList(query);
        // Convert to map.
        return toMap(queryList);
    }
}
