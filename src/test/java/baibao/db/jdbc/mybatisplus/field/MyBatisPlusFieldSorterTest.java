package baibao.db.jdbc.mybatisplus.field;

import baibao.db.jdbc.mybatisplus.field.MyBatisPlusFieldSorter;
import kunlun.data.sort.SortField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * MyBatisPlusSorter 的纯内存测试（不依赖数据库）.
 * 覆盖：白名单列解析（含 @TableField 自定义列名与主键）、防注入（未知字段/注入串跳过）、
 * 多字段按序追加、单项失败不中断、方向宽松解析、resolveColumn 覆写（跨表）.
 * @author Kahle
 */
public class MyBatisPlusFieldSorterTest {

    private final MyBatisPlusFieldSorter sorter = new MyBatisPlusFieldSorter();

    @BeforeClass
    public static void initTableInfo() {
        // 生产环境由 Mapper 启动时注册实体元数据，纯内存测试环境手动初始化
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SortEntity.class);
    }


    // region ======== 白名单列解析 ========

    @Test
    public void plainPropertyResolvedAndApplied() {
        MPJLambdaWrapper<SortEntity> wrapper = wrapper();
        boolean applied = sorter.applySort(wrapper, SortEntity.class,
                Collections.singletonList(SortField.of("name", "desc")), null);
        assertTrue(applied);
        assertEquals("name DESC", orderBySegment(wrapper));
    }

    @Test
    public void tableFieldCustomColumnResolved() {
        MPJLambdaWrapper<SortEntity> wrapper = wrapper();
        boolean applied = sorter.applySort(wrapper, SortEntity.class,
                Collections.singletonList(SortField.of("createTime", "asc")), null);
        assertTrue(applied);
        assertEquals("create_time ASC", orderBySegment(wrapper));
    }

    @Test
    public void idPropertyAllowed() {
        MPJLambdaWrapper<SortEntity> wrapper = wrapper();
        boolean applied = sorter.applySort(wrapper, SortEntity.class,
                Collections.singletonList(SortField.of("id", "desc")), null);
        assertTrue(applied);
        assertEquals("id DESC", orderBySegment(wrapper));
    }

    @Test
    public void existFalseFieldNotSortable() {
        // @TableField(exist = false) 的字段不是真实列，不允许参与排序
        MPJLambdaWrapper<SortEntity> wrapper = wrapper();
        boolean applied = sorter.applySort(wrapper, SortEntity.class,
                Collections.singletonList(SortField.of("virtualField", "asc")), null);
        assertFalse(applied);
        assertEquals("", orderBySegment(wrapper));
    }

    // endregion


    // region ======== 防注入：未知字段/注入串跳过 ========

    @Test
    public void unknownFieldSkipped() {
        MPJLambdaWrapper<SortEntity> wrapper = wrapper();
        boolean applied = sorter.applySort(wrapper, SortEntity.class,
                Collections.singletonList(SortField.of("notExist", "asc")), null);
        assertFalse(applied);
        assertEquals("", orderBySegment(wrapper));
    }

    @Test
    public void injectionAttemptSkipped() {
        // 非实体属性一律不进 SQL（防 SQL 注入）
        MPJLambdaWrapper<SortEntity> wrapper = wrapper();
        for (String evil : Arrays.asList("id; DROP TABLE tb_sort", "id) UNION SELECT 1", "id--")) {
            boolean applied = sorter.applySort(wrapper, SortEntity.class,
                    Collections.singletonList(SortField.of(evil, "asc")), null);
            assertFalse(applied);
            assertEquals("", orderBySegment(wrapper));
        }
    }

    @Test
    public void singleBadItemSkippedButOthersApplied() {
        // 单项解析失败只跳过该项，不中断其余项
        MPJLambdaWrapper<SortEntity> wrapper = wrapper();
        boolean applied = sorter.applySort(wrapper, SortEntity.class,
                Arrays.asList(SortField.of("notExist", "asc"), SortField.of("name", "desc")), null);
        assertTrue(applied);
        assertEquals("name DESC", orderBySegment(wrapper));
    }

    // endregion


    // region ======== 多字段与方向解析 ========

    @Test
    public void multiFieldsKeepInputOrder() {
        // 列表顺序即排序优先级
        MPJLambdaWrapper<SortEntity> wrapper = wrapper();
        boolean applied = sorter.applySort(wrapper, SortEntity.class,
                Arrays.asList(SortField.of("name", "desc"), SortField.of("createTime", "asc"), SortField.of("id", "desc")), null);
        assertTrue(applied);
        assertEquals("name DESC,create_time ASC,id DESC", orderBySegment(wrapper));
    }

    @Test
    public void directionLenientParse() {
        assertEquals("name DESC", applyForDirection("desc"));
        assertEquals("name DESC", applyForDirection("DESC"));
        assertEquals("name DESC", applyForDirection("descending"));
        assertEquals("name DESC", applyForDirection(" Desc "));
        assertEquals("name ASC", applyForDirection("asc"));
        assertEquals("name ASC", applyForDirection("ASC"));
        assertEquals("name ASC", applyForDirection("ascending"));
        // 缺省/未知方向按正序处理
        assertEquals("name ASC", applyForDirection(null));
        assertEquals("name ASC", applyForDirection(""));
        assertEquals("name ASC", applyForDirection("garbage"));
    }

    // endregion


    // region ======== 空参与类型契约 ========

    @Test
    public void blankInputsReturnFalse() {
        assertFalse(sorter.applySort(null, SortEntity.class,
                Collections.singletonList(SortField.of("name", "asc")), null));
        assertFalse(sorter.applySort(wrapper(), null,
                Collections.singletonList(SortField.of("name", "asc")), null));
        assertFalse(sorter.applySort(wrapper(), SortEntity.class, null, null));
        assertFalse(sorter.applySort(wrapper(), SortEntity.class, Collections.<SortField>emptyList(), null));
        // 空白字段名的排序项被跳过
        assertFalse(sorter.applySort(wrapper(), SortEntity.class,
                Collections.singletonList(SortField.of("  ", "asc")), null));
    }

    @Test
    public void supportsOnlyKnownWrappers() {
        assertTrue(sorter.supports(wrapper()));
        // MP 原生的 QueryWrapper（字符串列）也支持；LambdaQueryWrapper 列参数绑定 SFunction，不支持
        assertTrue(sorter.supports(new QueryWrapper<>()));
        assertFalse(sorter.supports("not-a-wrapper"));
        assertFalse(sorter.supports(null));
    }

    @Test
    public void plainQueryWrapperAlsoSupported() {
        // MP 原生 QueryWrapper：无联表注册表，走主实体白名单 + 命名选项映射，行为与 MPJ 单表一致
        QueryWrapper<SortEntity> wrapper = new QueryWrapper<>();
        boolean applied = sorter.applySort(wrapper, SortEntity.class,
                Arrays.asList(SortField.of("createTime", "desc"), SortField.of("id", "asc")), null);
        assertTrue(applied);
        assertEquals("create_time DESC,id ASC", queryWrapperOrderBySegment(wrapper));
        // 命名选项映射同样生效（虚拟键/自定义列）
        Map<String, Object> options = new HashMap<>();
        options.put(MyBatisPlusFieldSorter.OPTION_COLUMN_MAPPING,
                Collections.singletonMap("unpaidAmount", "(total_amount - paid_amount)"));
        wrapper = new QueryWrapper<>();
        applied = sorter.applySort(wrapper, SortEntity.class,
                Collections.singletonList(SortField.of("unpaidAmount", "asc")), options);
        assertTrue(applied);
        assertEquals("(total_amount - paid_amount) ASC", queryWrapperOrderBySegment(wrapper));
    }

    // endregion


    // region ======== resolveColumn 覆写（跨表/表达式） ========

    @Test
    public void resolveColumnOverrideForJoinField() {
        // 业务覆写 resolveColumn 放行列表达式（消歧/虚拟排序键），未识别的字段仍走默认白名单
        MyBatisPlusFieldSorter joinSorter = new MyBatisPlusFieldSorter() {
            @Override
            protected String resolveColumn(MPJLambdaWrapper<?> wrapper, Class<?> entityClass, String field) {
                if ("payerName".equals(field)) { return "t1.payer_name"; }
                return super.resolveColumn(wrapper, entityClass, field);
            }
        };
        MPJLambdaWrapper<SortEntity> wrapper = wrapper();
        boolean applied = joinSorter.applySort(wrapper, SortEntity.class,
                Arrays.asList(SortField.of("payerName", "asc"), SortField.of("name", "desc")), null);
        assertTrue(applied);
        assertEquals("t1.payer_name ASC,name DESC", orderBySegment(wrapper));
        // 主实体白名单仍生效：未知字段依旧被拒绝
        assertFalse(joinSorter.applySort(wrapper(), SortEntity.class,
                Collections.singletonList(SortField.of("notExist", "asc")), null));
    }

    // endregion


    private static MPJLambdaWrapper<SortEntity> wrapper() {

        return new MPJLambdaWrapper<>();
    }

    private static String orderBySegment(MPJLambdaWrapper<?> wrapper) {
        String sql = wrapper.getExpression().getOrderBy().getSqlSegment();
        if (sql == null) { return ""; }
        // 渲染结果带 " ORDER BY " 前缀，归一化掉，只保留列与方向
        return sql.replace("ORDER BY", "").trim();
    }

    private static String queryWrapperOrderBySegment(QueryWrapper<?> wrapper) {
        String sql = wrapper.getExpression().getOrderBy().getSqlSegment();
        if (sql == null) { return ""; }
        // 渲染结果带 " ORDER BY " 前缀，归一化掉，只保留列与方向
        return sql.replace("ORDER BY", "").trim();
    }

    private String applyForDirection(String direction) {
        MPJLambdaWrapper<SortEntity> wrapper = wrapper();
        assertTrue(sorter.applySort(wrapper, SortEntity.class,
                Collections.singletonList(SortField.of("name", direction)), null));

        return orderBySegment(wrapper);
    }


    // region ======== 测试用类型 ========

    @TableName("tb_sort")
    public static class SortEntity {
        @TableId
        private Long id;
        private String name;
        @TableField("create_time")
        private Date createTime;
        @TableField(exist = false)
        private String virtualField;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }
        public String getVirtualField() { return virtualField; }
        public void setVirtualField(String virtualField) { this.virtualField = virtualField; }
    }

    // endregion

}
