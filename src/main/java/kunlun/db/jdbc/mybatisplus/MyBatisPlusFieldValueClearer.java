/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.db.jdbc.mybatisplus;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import kunlun.db.jdbc.FieldValueClearer;
import kunlun.util.Assert;
import kunlun.util.CastUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字段值清除器的 MyBatis-Plus 实现：默认置空逻辑 + 实体元数据缓存 + 钩子。
 * <p>实现 {@link FieldValueClearer} 纯 JDK 契约：把 Object 入参强转为 MyBatis-Plus 的 {@link IService} /
 * {@link UpdateWrapper} 再处理。
 * <p>经 SPI（META-INF/services/kunlun.db.jdbc.FieldValueClearer）注册为 {@link kunlun.db.jdbc.JdbcUtil}
 * 的默认清除器；自定义实现可继承本类并覆写 {@link #shouldSkip}、{@link #doClear}、{@link #newEntity} 等。
 *
 * @author Kahle
 */
public class MyBatisPlusFieldValueClearer implements FieldValueClearer {
    private static final Logger log = LoggerFactory.getLogger(MyBatisPlusFieldValueClearer.class);
    private static final Map<Class<?>, EntityMeta> META_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Class<?>> ENTITY_CACHE = new ConcurrentHashMap<>();

    @Override
    public boolean clear(Object service, Object id, Collection<String> fields) {
        if (service == null) { return true; }
        Assert.isInstanceOf(IService.class, service,
                "This object [" + service + "] is not instanceof [" + IService.class + "]. ");
        return doClear(CastUtil.cast(service), resolveEntityClass(service.getClass()), id, fields);
    }

    @Override
    public boolean clear(Object service, Class<?> entityClass, Object id, Collection<String> fields) {
        if (service == null) { return true; }
        Assert.isInstanceOf(IService.class, service,
                "This object [" + service + "] is not instanceof [" + IService.class + "]. ");
        return doClear(CastUtil.cast(service), entityClass, id, fields);
    }

    @Override
    public Object appendClearFields(Object wrapper, Class<?> entityClass, Collection<String> fields) {
        if (wrapper == null || entityClass == null) { return wrapper; }
        if (CollUtil.isEmpty(fields)) { return wrapper; }
        Assert.isInstanceOf(UpdateWrapper.class, wrapper,
                "This object [" + wrapper + "] is not instanceof [" + UpdateWrapper.class + "]. ");
        appendClears(CastUtil.cast(wrapper), getEntityMeta(entityClass), entityClass, fields);
        return wrapper;
    }

    /**
     * 执行型核心：按 id 构 wrapper（eq+追加清除）并执行 UPDATE。
     */
    protected <T> boolean doClear(IService<T> service, Class<?> entityClass, Object id, Collection<String> fields) {
        if (service == null || entityClass == null || id == null) { return true; }
        if (CollUtil.isEmpty(fields)) { return true; }
        EntityMeta meta = getEntityMeta(entityClass);
        UpdateWrapper<T> wrapper = new UpdateWrapper<>();
        wrapper.eq(meta.keyColumn, id);
        int setCount = appendClears(wrapper, meta, entityClass, fields);
        if (setCount == 0) { return true; }
        // new 一个空对象触发 modifyTime/modifyUser 自动填充（与 updateStatusBy/enable 一致）
        Object emptyEntity = newEntity(entityClass);
        return service.update(CastUtil.cast(emptyEntity), wrapper);
    }

    /**
     * 追加清除子句的共用逻辑：校验命中实体列、跳过主键/审计字段、防注入。返回实际追加条数。
     */
    protected int appendClears(UpdateWrapper<?> wrapper, EntityMeta meta,
            Class<?> entityClass, Collection<String> fields) {
        int setCount = 0;
        // 逐字段校验：跳过主键/审计字段与非实体列，命中的列追加 SET col=null
        for (String prop : fields) {
            if (prop == null || prop.trim().isEmpty()) { continue; }
            prop = prop.trim();
            if (shouldSkip(meta, prop)) {
                log.warn("FieldValueClearer: skip field '{}'", prop);
                continue;
            }
            String col = meta.propertyToColumn.get(prop);
            if (col == null) {
                log.warn("FieldValueClearer: '{}' is not a column of {}", prop, entityClass.getName());
                continue;
            }
            wrapper.set(col, null);
            setCount++;
        }
        return setCount;
    }

    /**
     * 是否跳过该字段（默认：主键或 @TableField(fill=INSERT/INSERT_UPDATE) 审计字段）。
     * 可覆写调整规则。
     */
    protected boolean shouldSkip(EntityMeta meta, String property) {

        return meta.keyProperty.equals(property) || meta.auditProperties.contains(property);
    }

    /**
     * 创建用于触发自动填充的空实体；可覆写。
     */
    protected Object newEntity(Class<?> entityClass) {

        return ReflectUtil.newInstance(entityClass);
    }

    /**
     * 从 service 泛型推断实体类（缓存）。
     */
    protected Class<?> resolveEntityClass(Class<?> serviceClass) {
        Class<?> cached = ENTITY_CACHE.get(serviceClass);
        if (cached != null) { return cached; }
        Class<?>[] args = GenericTypeResolver.resolveTypeArguments(serviceClass, ServiceImpl.class);
        if (args == null || args.length <= 1 || args[1] == null) {
            throw new IllegalStateException("FieldValueClearer: cannot resolve entity class for " + serviceClass
                    + ", use apply(service, EntityClass.class, id, fields) instead");
        }
        ENTITY_CACHE.putIfAbsent(serviceClass, args[1]);
        return ENTITY_CACHE.get(serviceClass);
    }

    /**
     * 取实体元数据（列映射/主键/审计字段），按实体类缓存。
     */
    protected EntityMeta getEntityMeta(Class<?> entityClass) {
        EntityMeta meta = META_CACHE.get(entityClass);
        if (meta != null) { return meta; }
        // 解析 MyBatis-Plus 实体元数据
        TableInfo info = TableInfoHelper.getTableInfo(entityClass);
        if (info == null) {
            throw new IllegalStateException("Not a MyBatis-Plus entity: " + entityClass);
        }
        meta = new EntityMeta();
        meta.keyColumn = info.getKeyColumn();
        meta.keyProperty = info.getKeyProperty();
        // 遍历字段：建属性→列映射，并标记需自动填充的审计字段
        for (TableFieldInfo f : info.getFieldList()) {
            String prop = f.getProperty();
            meta.propertyToColumn.put(prop, f.getColumn());
            Field field = ReflectUtil.getField(entityClass, prop);
            if (field != null) {
                TableField tf = field.getAnnotation(TableField.class);
                if (tf != null && (tf.fill() == FieldFill.INSERT || tf.fill() == FieldFill.INSERT_UPDATE)) {
                    meta.auditProperties.add(prop);
                }
            }
        }
        // 合入缓存（并发时保留先入者）
        EntityMeta prev = META_CACHE.putIfAbsent(entityClass, meta);
        return prev != null ? prev : meta;
    }

    /**
     * 实体元数据。
     */
    protected static class EntityMeta {
        String keyColumn;
        String keyProperty;
        final Map<String, String> propertyToColumn = new HashMap<>();
        final Set<String> auditProperties = new HashSet<>();
    }

}
