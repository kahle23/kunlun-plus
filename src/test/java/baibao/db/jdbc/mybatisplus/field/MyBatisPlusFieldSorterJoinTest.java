package baibao.db.jdbc.mybatisplus.field;

import baibao.db.jdbc.mybatisplus.field.MyBatisPlusFieldSorter;
import kunlun.data.sort.SortField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import kunlun.data.Dict;
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
 * MyBatisPlusSorter 联表场景示例测试（兼作调用方用法的活文档，五个场景对应业务侧写法）.
 * <p>场景清单：
 * <ol>
 * <li>单表排序：零配置，主表字段自动解析为裸列名；</li>
 * <li>跨表排序：零配置，联表字段自动解析为 别名.列名（读 wrapper 的 MPJ 联表注册表）；</li>
 * <li>跨表重名属性：默认歧义跳过 + 告警，经通用参数传入映射消歧；</li>
 * <li>虚拟/计算排序键：库里不存在的列（算式/CASE WHEN），经通用参数传入映射显式放行；</li>
 * <li>未知字段：一律跳过（防注入边界）。</li>
 * </ol>
 * @author Kahle
 */
public class MyBatisPlusFieldSorterJoinTest {

    private final MyBatisPlusFieldSorter sorter = new MyBatisPlusFieldSorter();

    @BeforeClass
    public static void initTableInfo() {
        // 生产环境由 Mapper 启动时注册实体元数据，纯内存测试环境手动初始化
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, MainEntity.class);
        TableInfoHelper.initTableInfo(assistant, JoinEntity.class);
    }


    // region ======== 场景1：单表排序，零配置 ========

    @Test
    public void scenario1SingleTableZeroConfig() {
        // 业务侧什么都不用写：默认 getSorter() 即可，主表字段走白名单解析为裸列名
        MPJLambdaWrapper<MainEntity> wrapper = JoinWrappers.lambda(MainEntity.class);
        boolean applied = sorter.applySort(wrapper, MainEntity.class,
                Arrays.asList(SortField.of("name", "desc"), SortField.of("id", "asc")), null);
        assertTrue(applied);
        assertEquals("name DESC,id ASC", orderBySegment(wrapper));
    }

    // endregion


    // region ======== 场景2：跨表排序，零配置（自动 别名.列名） ========

    @Test
    public void scenario2JoinFieldAutoPrefix() {
        // 业务侧什么都不用写：联表字段自动解析出 MPJ 的别名前缀（主表 t、联表 t1）
        MPJLambdaWrapper<MainEntity> wrapper = joinWrapper();
        boolean applied = sorter.applySort(wrapper, MainEntity.class,
                Arrays.asList(SortField.of("createTime", "desc"), SortField.of("payerName", "asc")), null);
        assertTrue(applied);
        // createTime 只在主表 → t.create_time；payerName 只在联表 → t1.payer_name
        assertEquals("t.create_time DESC,t1.payer_name ASC", orderBySegment(wrapper));
    }

    // endregion


    // region ======== 场景3：跨表重名属性（默认跳过，命名映射消歧） ========

    @Test
    public void scenario3AmbiguousFieldSkippedByDefault() {
        // MainEntity 与 JoinEntity 都有 name 属性，无法替业务做决定：跳过该项并告警，其余项照常
        MPJLambdaWrapper<MainEntity> wrapper = joinWrapper();
        boolean applied = sorter.applySort(wrapper, MainEntity.class,
                Arrays.asList(SortField.of("name", "desc"), SortField.of("payerName", "asc")), null);
        assertTrue(applied);
        assertEquals("t1.payer_name ASC", orderBySegment(wrapper));
    }

    @Test
    public void scenario3bMappingResolvesAmbiguity() {
        // 通用选项（约定 key OPTION_COLUMN_MAPPING）传入"字段名 → 列表达式"映射即可消歧：
        // 不用构造任何新实例，默认排序器 + 一次传参搞定；未命中映射的字段仍走自动白名单
        MPJLambdaWrapper<MainEntity> wrapper = joinWrapper();
        boolean applied = sorter.applySort(wrapper, MainEntity.class,
                Arrays.asList(SortField.of("name", "desc"), SortField.of("payerName", "asc")),
                columnMappingOptions(Collections.singletonMap("name", "t.name")));
        assertTrue(applied);
        assertEquals("t.name DESC,t1.payer_name ASC", orderBySegment(wrapper));
    }

    // endregion


    // region ======== 场景4：虚拟/计算排序键（通用参数传入映射放行） ========

    @Test
    public void scenario4VirtualSortKey() {
        // 库里没有 unpaidAmount 这一列（是算出来的），白名单天然不认识：通用参数传入映射放行表达式
        Map<String, String> virtualColumns = new HashMap<>();
        virtualColumns.put("unpaidAmount", "(total_amount - paid_amount)");
        virtualColumns.put("statusGroup", "CASE WHEN status IN (3, 5) THEN 0 ELSE 1 END");
        MPJLambdaWrapper<MainEntity> wrapper = joinWrapper();
        boolean applied = sorter.applySort(wrapper, MainEntity.class,
                Arrays.asList(SortField.of("unpaidAmount", "desc"), SortField.of("payerName", "asc")),
                columnMappingOptions(virtualColumns));
        assertTrue(applied);
        // 虚拟键按表达式排序，实体字段仍走自动白名单，两者混用
        assertEquals("(total_amount - paid_amount) DESC,t1.payer_name ASC", orderBySegment(wrapper));
    }

    @Test
    public void scenario4bArgMappingBeatsAutoAndBlankMeansZeroConfig() {
        // 命名参数映射优先于自动白名单：即使自动能解析 payerName，映射命中则用映射的
        MPJLambdaWrapper<MainEntity> wrapper = joinWrapper();
        assertTrue(sorter.applySort(wrapper, MainEntity.class,
                Collections.singletonList(SortField.of("payerName", "asc")),
                columnMappingOptions(Collections.singletonMap("payerName", "t.caller_payer"))));
        assertEquals("t.caller_payer ASC", orderBySegment(wrapper));
        // null / 空 args 均等价于零配置（自动白名单照常）
        for (Map<String, Object> options : Arrays.asList(null, new HashMap<String, Object>(), new Dict())) {
            wrapper = joinWrapper();
            assertTrue(sorter.applySort(wrapper, MainEntity.class,
                    Collections.singletonList(SortField.of("payerName", "asc")), options));
            assertEquals("t1.payer_name ASC", orderBySegment(wrapper));
        }
        // kunlun 的 Dict 即 Map，可直接作选项包（envelope）
        Dict dictOptions = new Dict();
        dictOptions.put(MyBatisPlusFieldSorter.OPTION_COLUMN_MAPPING, Collections.singletonMap("payerName", "t.dict_payer"));
        wrapper = joinWrapper();
        assertTrue(sorter.applySort(wrapper, MainEntity.class,
                Collections.singletonList(SortField.of("payerName", "asc")), dictOptions));
        assertEquals("t.dict_payer ASC", orderBySegment(wrapper));
    }

    // endregion


    // region ======== 场景5：未知字段一律跳过（防注入边界） ========

    @Test
    public void scenario5UnknownFieldSkipped() {
        MPJLambdaWrapper<MainEntity> wrapper = joinWrapper();
        boolean applied = sorter.applySort(wrapper, MainEntity.class,
                Arrays.asList(SortField.of("id; DROP TABLE tb_main", "asc"), SortField.of("createTime", "asc")), null);
        // 注入串与未知字段全部跳过，不进 SQL；有效项照常生效
        assertTrue(applied);
        assertEquals("t.create_time ASC", orderBySegment(wrapper));
        assertFalse(sorter.applySort(joinWrapper(), MainEntity.class,
                Collections.singletonList(SortField.of("notExist", "asc")), null));
    }

    // endregion


    /**
     * 按约定 key（{@link MyBatisPlusFieldSorter#OPTION_COLUMN_MAPPING}）包装自定义列映射.
     */
    private static Map<String, Object> columnMappingOptions(Map<String, String> mapping) {
        Map<String, Object> options = new HashMap<>();
        options.put(MyBatisPlusFieldSorter.OPTION_COLUMN_MAPPING, mapping);

        return options;
    }

    /**
     * 构建带联表的 wrapper（模拟业务 buildQueryWrapper 的产物：主表 + leftJoin 一张表）.
     */
    private static MPJLambdaWrapper<MainEntity> joinWrapper() {

        return JoinWrappers.lambda(MainEntity.class)
                .selectAll(MainEntity.class)
                .leftJoin(JoinEntity.class, JoinEntity::getMainId, MainEntity::getId);
    }

    private static String orderBySegment(MPJLambdaWrapper<?> wrapper) {
        String sql = wrapper.getExpression().getOrderBy().getSqlSegment();
        if (sql == null) { return ""; }
        // 渲染结果带 " ORDER BY " 前缀，归一化掉，只保留列与方向
        return sql.replace("ORDER BY", "").trim();
    }


    // region ======== 测试用类型 ========

    @TableName("tb_main")
    public static class MainEntity {
        @TableId
        private Long id;
        private String name;
        @TableField("create_time")
        private Date createTime;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }
    }

    @TableName("tb_join")
    public static class JoinEntity {
        @TableId
        private Long id;
        @TableField("main_id")
        private Long mainId;
        @TableField("payer_name")
        private String payerName;
        // 与主表重名的属性，用于歧义场景
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getMainId() { return mainId; }
        public void setMainId(Long mainId) { this.mainId = mainId; }
        public String getPayerName() { return payerName; }
        public void setPayerName(String payerName) { this.payerName = payerName; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // endregion

}
