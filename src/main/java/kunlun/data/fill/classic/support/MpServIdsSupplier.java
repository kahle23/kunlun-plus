/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.fill.classic.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import kunlun.common.constant.Nil;
import kunlun.core.function.Consumer;
import kunlun.core.function.Function;
import kunlun.spring.util.SpringUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The data supplier of the "listByIds" method in the mybatis plus service.
 * @author Kahle
 */
public class MpServIdsSupplier<T> extends BaseServDataSupplier<IService<T>, T> {

    public static MpServIdsSupplier<Object> of(String beanName, String fieldName) {

        return of(beanName, fieldName, Nil.<Consumer<Map<String, Object>>>g());
    }

    public static MpServIdsSupplier<Object> of(String beanName, final String fieldName,
                                               Consumer<Map<String, Object>> dataProcessor) {
        //noinspection unchecked
        IService<Object> service = SpringUtil.getBean(beanName, IService.class);
//        Function<Object, Object> keyMapper = item-> ReflectUtil.getFieldValue(item, fieldName);
        Function<Object, Object> keyMapper = new Function<Object, Object>() {
            @Override
            public Object apply(Object item) {
                return ReflectUtil.getFieldValue(item, fieldName);
            }
        };
        return new MpServIdsSupplier<Object>(service, keyMapper, dataProcessor);
    }

    public static <T> MpServIdsSupplier<T> of(Class<? extends IService<T>> clazz, Function<T, Object> keyMapper,
                                              Consumer<Map<String, Object>> dataProcessor) {

        return new MpServIdsSupplier<T>(SpringUtil.getBean(clazz), keyMapper, dataProcessor);
    }

    public static <T> MpServIdsSupplier<T> of(Class<? extends IService<T>> clazz, Function<T, Object> keyMapper) {

        return new MpServIdsSupplier<T>(SpringUtil.getBean(clazz), keyMapper);
    }

    public static <T> MpServIdsSupplier<T> of(IService<T> service, Function<T, Object> keyMapper,
                                              Consumer<Map<String, Object>> dataProcessor) {

        return new MpServIdsSupplier<T>(service, keyMapper, dataProcessor);
    }

    public static <T> MpServIdsSupplier<T> of(IService<T> service, Function<T, Object> keyMapper) {

        return new MpServIdsSupplier<T>(service, keyMapper);
    }

    public MpServIdsSupplier(IService<T> service, Function<T, Object> keyMapper,
                             Consumer<Map<String, Object>> processor) {

        super(service, keyMapper, processor);
    }

    public MpServIdsSupplier(IService<T> service, Function<T, Object> keyMapper) {

        super(service, keyMapper);
    }

    @Override
    public Map<String, Map<String, Object>> acquire(Collection<?> coll) {
        // Parameter conversion and deduplication.
        List<Serializable> list = toList(coll, Serializable.class);
        if (CollUtil.isEmpty(list)) { return Collections.emptyMap(); }
        // Data query.
        List<T> data = getService().listByIds(list);
        if (CollUtil.isEmpty(data)) { return Collections.emptyMap(); }
        // Convert to map.
        return toMap(data);
    }
}
