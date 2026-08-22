/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.db.jdbc.mybatisplus.field;

import kunlun.data.sort.SortDirection;
import kunlun.data.sort.SortField;
import kunlun.data.sort.Sorter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.yulichang.toolkit.TableList;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import kunlun.util.Assert;
import kunlun.util.CastUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 排序器的 MyBatis-Plus 实现：命名选项映射 + 联表自动白名单 + 列解析钩子 + 实体元数据缓存.
 * <p>实现 {@link Sorter} 纯 JDK 契约：把 Object 入参强转为具体 wrapper 再处理；
 * 经 SPI（META-INF/services/kunlun.data.sort.Sorter）注册为 {@link kunlun.data.sort.SortUtil} 的实现之一。
 * <p><b>支持的排序目标</b>：MyBatis-Plus-Join 的 {@link MPJLambdaWrapper}（项目标准，单表/联表皆宜）
 * 与 MP 原生的 {@link QueryWrapper}（字符串列）。MP 的 {@code LambdaQueryWrapper} 列参数绑定
 * {@code SFunction}，字符串列无法进其 {@code orderBy}，不在支持范围（请改用前两者）。
 * <p><b>列解析规则（按优先级）</b>：
 * <ul>
 * <li><b>命名选项映射</b>：{@code applySort} 通用选项中 {@link #OPTION_COLUMN_MAPPING} 对应的
 * {@code Map}（含 kunlun 的 Dict），约定为"字段名 → 列名/列表达式"的自定义映射，命中即用——
 * 虚拟/计算排序键（CASE WHEN、算式、子查询等非实体属性）与跨表重名消歧都走这里，谁调用谁定制；</li>
 * <li>无联表：主实体白名单（{@link TableInfo} 元数据，含主键）命中 → 裸列名
 * （自动尊重 {@code @TableField} 的自定义列名）；</li>
 * <li>有联表：自动读取 wrapper 的联表注册表（{@link TableList}，MPJ 生成 WHERE 条件用的同一份元数据），
 * 构建"主表 + 全部联表"的联合白名单，属性唯一命中 → {@code 别名.列名}（如 {@code "t1.payer_name"}），
 * 跨表排序零配置；</li>
 * <li>重名属性（多张表都有）：歧义，跳过并告警，需经命名选项映射或覆写 {@link #resolveColumn} 消歧；</li>
 * <li>全部未命中：跳过并告警（防注入的核心——绝不直接拼接外部传入的列名）。</li>
 * </ul>
 * <p>已知限制：联表解析依赖 MPJ 的 {@link TableList} 公开 API（1.5.x），
 * 读取失败时自动回退主实体白名单，不影响查询本身；同一实体 join 两次（自联）时按首个别名解析。
 *
 * @author Kahle
 */
public class MyBatisPlusFieldSorter implements Sorter {
    private static final Logger log = LoggerFactory.getLogger(MyBatisPlusFieldSorter.class);
    private static final Map<Class<?>, EntityColumns> COLUMNS_CACHE = new ConcurrentHashMap<>();

    /**
     * 通用选项中"自定义列映射"的约定 key：对应"字段名 → 列名/列表达式"的 Map（含 Dict）.
     */
    public static final String OPTION_COLUMN_MAPPING = "columnMapping";

    @Override
    public boolean supports(Object target) {

        return target instanceof MPJLambdaWrapper || target instanceof QueryWrapper;
    }

    @Override
    public boolean applySort(Object target, Class<?> entityClass, List<SortField> sortFields, Map<String, Object> options) {
        if (target == null || entityClass == null) { return false; }
        Assert.isTrue(supports(target),
                "This object [" + target + "] is not supported by [" + getClass().getName() + "]. ");
        if (sortFields == null || sortFields.isEmpty()) { return false; }
        // MPJ 的 wrapper 才有联表注册表（可走联表自动白名单），MP 的 QueryWrapper 仅走主实体白名单
        MPJLambdaWrapper<?> mpjWrapper = target instanceof MPJLambdaWrapper ? CastUtil.cast(target) : null;
        Map<?, ?> optionMapping = getOptionColumnMapping(options);
        int applied = 0;
        // 按调用方传入顺序逐项追加，顺序即排序优先级；单项解析失败只跳过，不中断其余项
        for (SortField item : sortFields) {
            if (item == null || item.getField() == null || item.getField().trim().isEmpty()) { continue; }
            String field = item.getField().trim();
            // 命名选项映射优先（谁调用谁定制），未命中再走自动白名单
            String column = null;
            if (optionMapping != null) {
                Object custom = optionMapping.get(field);
                if (custom != null) { column = String.valueOf(custom); }
            }
            if (column == null) {
                column = mpjWrapper != null
                        ? resolveColumn(mpjWrapper, entityClass, field)
                        : getEntityColumns(entityClass).propertyToColumn.get(field);
            }
            if (column == null) {
                log.warn("MyBatisPlusSorter: '{}' is not a sortable column of {}, skipped", field, entityClass.getName());
                continue;
            }
            boolean isAsc = SortDirection.of(item.getOrder()).isAsc();
            if (mpjWrapper != null) {
                mpjWrapper.orderBy(true, isAsc, column);
            }
            else {
                ((QueryWrapper<?>) target).orderBy(true, isAsc, column);
            }
            applied++;
        }
        return applied > 0;
    }

    /**
     * 从通用选项中取约定的自定义列映射：{@link #OPTION_COLUMN_MAPPING} 对应的 Map（含 Dict）.
     *
     * @param options 通用扩展选项（可为 null）
     * @return 自定义列映射，无则 null
     */
    protected Map<?, ?> getOptionColumnMapping(Map<String, Object> options) {
        if (options == null) { return null; }
        Object mapping = options.get(OPTION_COLUMN_MAPPING);
        return mapping instanceof Map ? (Map<?, ?>) mapping : null;
    }

    /**
     * 字段名到列名/列表达式的解析（防注入核心），默认实现即"联表自动白名单".
     * <p>大多数定制（虚拟/计算排序键、跨表重名消歧）经通用选项传入命名映射即可，无需覆写本方法；
     * 仅当需要读取 wrapper/query 动态决策时才覆写，
     * 未识别的字段记得交回 {@code super.resolveColumn(...)} 走默认解析。
     *
     * @param wrapper 排序目标（可读取联表注册表 {@link TableList}）
     * @param entityClass 主实体类型
     * @param field 字段名（已去空白）
     * @return 命中返回列名/列表达式，不认识返回 null（跳过该项）
     */
    protected String resolveColumn(MPJLambdaWrapper<?> wrapper, Class<?> entityClass, String field) {
        Map<String, EntityColumns> tables = getTableColumns(wrapper, entityClass);
        // 无联表（或联表信息不可用）：主实体白名单，裸列名（与既有行为一致）
        if (tables == null) {
            return getEntityColumns(entityClass).propertyToColumn.get(field);
        }
        // 联表场景：全表联合白名单，唯一命中才带别名前缀输出；多表重名视为歧义
        String column = null;
        int hits = 0;
        for (Map.Entry<String, EntityColumns> table : tables.entrySet()) {
            String col = table.getValue().propertyToColumn.get(field);
            if (col != null) {
                hits++;
                column = table.getKey() + "." + col;
            }
        }
        if (hits > 1) {
            log.warn("MyBatisPlusSorter: '{}' matches columns of {} joined tables, ambiguous and skipped", field, hits);
            return null;
        }
        return column;
    }

    /**
     * 取联表候选（别名前缀 → 列白名单，主表在前，按实体类去重）.
     * <p>无联表、{@link TableList} 缺失或解析异常时返回 null，回退主实体白名单；
     * 读取 {@link TableList} 的过程整体防御，联表解析失败不影响查询本身。
     *
     * @param wrapper 排序目标
     * @param entityClass 主实体类型（TableList 缺主表信息时兜底）
     * @return 别名前缀 → 列白名单（至少两张表），或 null（回退主实体白名单）
     */
    protected Map<String, EntityColumns> getTableColumns(MPJLambdaWrapper<?> wrapper, Class<?> entityClass) {
        try {
            TableList tableList = wrapper.getTableList();
            if (tableList == null) { return null; }
            List<TableList.Node> nodes = tableList.getAll();
            if (nodes == null || nodes.isEmpty()) { return null; }
            Class<?> rootClass = tableList.getRootClass() != null ? tableList.getRootClass() : entityClass;
            Map<String, EntityColumns> tables = new LinkedHashMap<>();
            putTableColumns(tables, tableList, rootClass);
            Set<Class<?>> seen = new HashSet<>();
            seen.add(rootClass);
            for (TableList.Node node : nodes) {
                if (node == null || node.getClazz() == null || !seen.add(node.getClazz())) { continue; }
                putTableColumns(tables, tableList, node.getClazz());
            }
            // 只有主表说明没有真正的联表，按无联表处理（裸列名，兼容既有行为）
            return tables.size() > 1 ? tables : null;
        }
        catch (Throwable e) {
            log.debug("MyBatisPlusSorter: resolve join tables failed, fallback to main entity whitelist", e);
            return null;
        }
    }

    private void putTableColumns(Map<String, EntityColumns> tables, TableList tableList, Class<?> clazz) {
        try {
            String prefix = tableList.getPrefixByClass(clazz);
            if (prefix != null) {
                tables.put(prefix, getEntityColumns(clazz));
            }
        }
        catch (Throwable e) {
            log.debug("MyBatisPlusSorter: resolve table prefix failed for [{}]", clazz, e);
        }
    }

    /**
     * 取实体列白名单（属性→列名，含主键），按实体类缓存.
     *
     * @param entityClass 主实体类型
     * @return 实体列白名单
     */
    protected EntityColumns getEntityColumns(Class<?> entityClass) {
        EntityColumns columns = COLUMNS_CACHE.get(entityClass);
        if (columns != null) { return columns; }
        // 解析 MyBatis-Plus 实体元数据
        TableInfo info = TableInfoHelper.getTableInfo(entityClass);
        if (info == null) {
            throw new IllegalStateException("Not a MyBatis-Plus entity: " + entityClass);
        }
        columns = new EntityColumns();
        // 主键（允许按 id 排序）
        if (info.getKeyProperty() != null && info.getKeyColumn() != null) {
            columns.propertyToColumn.put(info.getKeyProperty(), info.getKeyColumn());
        }
        // 普通字段（自动尊重 @TableField 的自定义列名）
        for (TableFieldInfo f : info.getFieldList()) {
            columns.propertyToColumn.put(f.getProperty(), f.getColumn());
        }
        // 合入缓存（并发时保留先入者）
        EntityColumns prev = COLUMNS_CACHE.putIfAbsent(entityClass, columns);
        return prev != null ? prev : columns;
    }

    /**
     * 实体列白名单.
     */
    protected static class EntityColumns {
        final Map<String, String> propertyToColumn = new HashMap<>();
    }

}
