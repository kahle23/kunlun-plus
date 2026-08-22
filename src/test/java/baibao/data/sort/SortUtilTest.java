package baibao.data.sort;

import baibao.db.jdbc.mybatisplus.field.MyBatisPlusFieldSorter;
import kunlun.data.sort.SortField;
import kunlun.data.sort.Sorter;
import kunlun.data.sort.SortUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * SortUtil 门面的纯内存测试（不依赖数据库）.
 * 覆盖：SPI 发现多实现、按排序目标类型路由（supports）、后注册者优先、
 * 空排序参数快速返回、无人支持时告警忽略、register/unregister 生命周期、按实现类确定性获取.
 * @author Kahle
 */
public class SortUtilTest {

    @BeforeClass
    public static void initTableInfo() {
        // 路由到 MyBatis-Plus 实现时需要实体的 TableInfo 缓存（生产环境由 Mapper 启动时注册）
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), RouteEntity.class);
    }


    // region ======== SPI 发现与路由 ========

    @Test
    public void spiFoundMyBatisPlusImpl() {
        List<Sorter> sorters = SortUtil.getSorters();
        boolean found = false;
        for (Sorter sorter : sorters) {
            if (sorter instanceof MyBatisPlusFieldSorter) { found = true; }
        }
        // 测试类路径含 META-INF/services/kunlun.data.sort.Sorter，SPI 应能发现 MyBatis-Plus 实现
        assertTrue("registered=" + sorters, found);
    }

    @Test
    public void applySortRoutesBySupports() {
        MPJLambdaWrapper<RouteEntity> wrapper = new MPJLambdaWrapper<>();
        boolean applied = SortUtil.applySort(wrapper, RouteEntity.class,
                Collections.singletonList(SortField.of("name", "desc")));
        assertTrue(applied);
        assertEquals("name DESC", orderBySegment(wrapper));
    }

    @Test
    public void applySortArgsPassedThrough() {
        // 通用选项（命名选项包）经门面透传：MyBatis-Plus 实现约定 OPTION_COLUMN_MAPPING 对应的 Map 为自定义列映射
        Map<String, Object> options = new HashMap<>() ;
        options.put(MyBatisPlusFieldSorter.OPTION_COLUMN_MAPPING,
                Collections.singletonMap("name", "custom_name"));
        MPJLambdaWrapper<RouteEntity> wrapper = new MPJLambdaWrapper<>();
        boolean applied = SortUtil.applySort(wrapper, RouteEntity.class,
                Collections.singletonList(SortField.of("name", "desc")), options);
        assertTrue(applied);
        assertEquals("custom_name DESC", orderBySegment(wrapper));
    }

    @Test
    public void unsupportedTargetWarnedAndIgnored() {
        // 没有实现支持该排序目标时告警并忽略，不抛异常
        assertFalse(SortUtil.applySort("not-a-sort-target", RouteEntity.class,
                Collections.singletonList(SortField.of("name", "asc"))));
    }

    // endregion


    // region ======== 空参数快速返回 ========

    @Test
    public void blankSortFieldsReturnFalse() {
        assertFalse(SortUtil.applySort(new MPJLambdaWrapper<RouteEntity>(), RouteEntity.class, null));
        assertFalse(SortUtil.applySort(new MPJLambdaWrapper<RouteEntity>(), RouteEntity.class,
                Collections.<SortField>emptyList()));
        assertFalse(SortUtil.applySort(null, RouteEntity.class,
                Collections.singletonList(SortField.of("name", "asc"))));
    }

    // endregion


    // region ======== 注册生命周期与路由唯一性 ========

    @Test
    public void multiMatchThrowsAndRecoverAfterUnregister() {
        RecordingSorter recorder = new RecordingSorter();
        SortUtil.registerSorter(recorder);
        try {
            // 同一排序目标出现两个激活的实现（SPI 的 MP 实现 + recorder）是注册表状态冲突，直接报错
            try {
                SortUtil.applySort(new MPJLambdaWrapper<RouteEntity>(), RouteEntity.class,
                        Collections.singletonList(SortField.of("name", "asc")));
                fail("多命中应该抛 IllegalStateException");
            }
            catch (IllegalStateException ignored) { }
        }
        finally {
            SortUtil.unregisterSorter(recorder.getClass());
        }
        // 注销冗余实现后恢复单命中，正常路由到 MyBatis-Plus 实现
        MPJLambdaWrapper<RouteEntity> wrapper = new MPJLambdaWrapper<>();
        assertTrue(SortUtil.applySort(wrapper, RouteEntity.class,
                Collections.singletonList(SortField.of("name", "asc"))));
        assertEquals("name ASC", orderBySegment(wrapper));
    }

    @Test
    public void unregisterRemovesSorter() {
        RecordingSorter recorder = new RecordingSorter();
        SortUtil.registerSorter(recorder);
        SortUtil.unregisterSorter(recorder.getClass());
        for (Sorter sorter : SortUtil.getSorters()) {
            assertFalse("注销后不应再出现在实现列表", sorter instanceof RecordingSorter);
        }
    }

    @Test
    public void getSorterByClassCachedAndDeterministic() {
        Sorter first = SortUtil.getSorter(MyBatisPlusFieldSorter.class);
        Sorter second = SortUtil.getSorter(MyBatisPlusFieldSorter.class);
        // 按实现类获取是确定性的：同一实现类返回同一实例
        assertSame(first, second);
        assertTrue(first instanceof MyBatisPlusFieldSorter);
    }

    // endregion


    private static String orderBySegment(MPJLambdaWrapper<?> wrapper) {
        String sql = wrapper.getExpression().getOrderBy().getSqlSegment();
        if (sql == null) { return ""; }
        // 渲染结果带 " ORDER BY " 前缀，归一化掉，只保留列与方向
        return sql.replace("ORDER BY", "").trim();
    }


    // region ======== 测试用类型 ========

    @TableName("tb_route")
    public static class RouteEntity {
        @TableId
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    /**
     * 录制型假实现：支持 MPJLambdaWrapper，但不做任何事，仅记录被调用.
     */
    private static class RecordingSorter implements Sorter {

        private boolean invoked;

        @Override
        public boolean supports(Object target) {

            return target instanceof MPJLambdaWrapper;
        }

        @Override
        public boolean applySort(Object target, Class<?> entityClass, List<SortField> sortFields, Map<String, Object> options) {
            invoked = true;
            return true;
        }
    }

    // endregion

}
