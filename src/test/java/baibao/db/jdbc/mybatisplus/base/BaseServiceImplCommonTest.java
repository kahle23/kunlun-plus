package baibao.db.jdbc.mybatisplus.base;

import baibao.common.dto.DragSortDTO;
import baibao.common.dto.base.BaseEditParam;
import baibao.common.dto.base.BaseQuery;
import baibao.data.paging.support.PageHelperResultProcessor;
import kunlun.data.sort.SortField;
import baibao.db.jdbc.mybatisplus.field.MyBatisPlusFieldSorter;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import kunlun.common.Page;
import kunlun.data.validation.support.javax.ValidationUtil;
import kunlun.db.jdbc.JdbcUtil;
import baibao.db.jdbc.mybatisplus.field.MyBatisPlusFieldValueClearer;
import kunlun.exception.BusinessException;
import kunlun.util.PageUtil;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * BaseServiceImpl 通用方法的纯内存测试（不依赖数据库）.
 * 覆盖：queryList/queryPage 的空结果契约与入参不被就地修改、
 * prepareQuery 判空兜底、enable 的存在性检查（含非主键字段和集合入参）、
 * dragSort 的无效项过滤、queryScrollPage 的滚动语义、
 * editRecord/editBatch 对 clearFields 的自动置空.
 * @author Kahle
 */
public class BaseServiceImplCommonTest {

    private TestServiceImpl service;

    @BeforeClass
    public static void initValidatorAndProcessor() {
        try {
            ValidationUtil.setValidator(Validation.buildDefaultValidatorFactory().getValidator());
        }
        catch (ValidationException e) {
            // 测试类路径没有 Bean Validation 实现时用空实现兜底（测试参数本身没有约束注解）
            ValidationUtil.setValidator(new EmptyValidator());
        }
        // queryPage 的结果统一走 PageUtil.handleResult（SPI），测试环境注册 PageHelper 实现
        PageUtil.setPageResultProcessor(new PageHelperResultProcessor());
        // enable 等方法构建 LambdaWrapper 时需要实体的 TableInfo 缓存，
        // 生产环境由 Mapper 启动时注册，纯内存测试环境手动初始化
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), TestEntity.class);
        // 单语句合并置空依赖 JdbcUtil 的默认 FieldValueClearer，
        // 而 SPI 发现在 surefire 2.4.2 + JDK8 测试环境下失效，手动注册默认实现
        JdbcUtil.setDefaultFieldValueClearer(new MyBatisPlusFieldValueClearer());
    }

    @Before
    public void setUp() {
        service = new TestServiceImpl();
    }

    // region ======== queryList / queryPage 契约 ========

    @Test
    public void queryListEmptyReturnsEmptyList() {
        List<TestResult> list = service.queryList(new TestQuery());
        // 契约：返回值一定不为 null，无数据时为空集合
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void queryListNullQueryUsesDefaultInstance() {
        List<TestResult> list = service.queryList(null);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void queryListDoesNotMutateInputQuery() {
        service.dbList.add(entity(1L));
        TestQuery query = new TestQuery();
        assertTrue(query.isPaged());
        service.queryList(query);
        // 浅拷贝验证：调用方的 query 不被就地关闭分页
        assertTrue(query.isPaged());
    }

    @Test
    public void queryPageEmptyDataNotNull() {
        TestQuery query = new TestQuery();
        query.setPaged(false);
        Page<TestResult> page = service.queryPage(query);
        // 契约：返回值及其 data 一定不为 null，无数据时 data 为空集合
        assertNotNull(page);
        assertNotNull(page.getData());
        assertTrue(page.getData().isEmpty());
    }

    @Test
    public void queryPageMapsResults() {
        service.dbList.add(entity(1L));
        service.dbList.add(entity(2L));
        TestQuery query = new TestQuery();
        query.setPaged(false);
        Page<TestResult> page = service.queryPage(query);
        assertEquals(2, page.getData().size());
        assertEquals(Long.valueOf(1L), page.getData().get(0).getId());
    }

    @Test
    public void queryCountNullQueryUsesDefaultInstance() {
        service.countResult = 7;
        assertEquals(7, service.queryCount(null));
    }

    // endregion

    // region ======== queryPage 自定义排序 ========

    @Test
    public void queryPageAppliesCustomSort() {
        TestQuery query = new TestQuery();
        query.setPaged(false);
        query.setSortFields(Arrays.asList(SortField.of("name", "desc"), SortField.of("id", "asc")));
        service.queryPage(query);
        // 排序项按列表顺序逐项追加到查询条件（顺序即优先级）
        String orderBy = orderBySegment(service.lastListWrapper);
        assertEquals("name DESC,id ASC", orderBy);
    }

    @Test
    public void queryPageWithoutCustomSortKeepsZeroBreakage() {
        TestQuery query = new TestQuery();
        query.setPaged(false);
        service.queryPage(query);
        // 未传排序参数时无操作，行为与既有逻辑一致
        String orderBy = orderBySegment(service.lastListWrapper);
        assertTrue(orderBy == null || orderBy.isEmpty());
    }

    @Test
    public void queryPageSkipsUnknownSortField() {
        TestQuery query = new TestQuery();
        query.setPaged(false);
        query.setSortFields(Arrays.asList(SortField.of("notExist", "asc"), SortField.of("name", "desc")));
        service.queryPage(query);
        // 非实体白名单字段被跳过，其余项照常生效
        String orderBy = orderBySegment(service.lastListWrapper);
        assertEquals("name DESC", orderBy);
    }

    @Test
    public void queryPagePassesSortColumnMapping() {
        // 覆写 applySort 在 options 为空时补充的映射，经 queryPage 生效（虚拟键/消歧的推荐接入方式）
        service.sortColumnMapping = Collections.singletonMap("name", "t.custom_name");
        TestQuery query = new TestQuery();
        query.setPaged(false);
        query.setSortFields(Collections.singletonList(SortField.of("name", "desc")));
        service.queryPage(query);
        assertEquals("t.custom_name DESC", orderBySegment(service.lastListWrapper));
    }

    // endregion

    // region ======== enable ========

    @Test
    public void enableSingleExistingUpdates() {
        service.dbList.add(entity(1L));
        service.enable(TestEntity::getId, 1L, TestEntity::getStatus, 1);
        assertTrue(service.updateCalled);
    }

    @Test
    public void enableNotExistThrows() {
        try {
            service.enable(TestEntity::getId, 1L, TestEntity::getStatus, 1);
            fail("记录不存在应该报错");
        }
        catch (BusinessException ignored) { }
    }

    @Test
    public void enableListMissingOneThrows() {
        service.dbList.add(entity(1L));
        // enable 的 value 声明为 Serializable，而 List 接口不继承 Serializable，须用实现类类型传递
        Serializable ids = new ArrayList<>(Arrays.asList(1L, 2L));
        try {
            service.enable(TestEntity::getId, ids, TestEntity::getStatus, 1);
            fail("集合中存在查不到的值应该报错");
        }
        catch (BusinessException ignored) { }
    }

    @Test
    public void enableAlreadyEnabledSkipsUpdate() {
        TestEntity entity = entity(1L);
        entity.setStatus(1);
        service.dbList.add(entity);
        service.enable(TestEntity::getId, 1L, TestEntity::getStatus, 1);
        assertFalse(service.updateCalled);
    }

    @Test
    public void enableByNonIdFieldChecksExistence() {
        TestEntity entity = entity(9L);
        entity.setCode("C001");
        service.dbList.add(entity);
        // field 用 code（非主键）：存在性检查必须按 code 查，而不是按主键查
        service.enable(TestEntity::getCode, "C001", TestEntity::getStatus, 1);
        assertTrue(service.updateCalled);
    }

    // endregion

    // region ======== dragSort ========

    @Test
    public void dragSortAllInvalidReturnsFalse() {
        List<DragSortDTO> sorts = Arrays.asList(null, dto(null, null), dto(1L, null));
        assertFalse(service.dragSort((entity, val) -> entity.setSort(val), sorts));
        assertNull(service.updatedBatch);
    }

    @Test
    public void dragSortMissingIdRejected() {
        List<DragSortDTO> sorts = Collections.singletonList(dto(null, 3L));
        try {
            service.dragSort((entity, val) -> entity.setSort(val), sorts);
            fail("有效项的 id 缺失应该被校验拦截");
        }
        catch (ConstraintViolationException ignored) { }
    }

    @Test
    public void dragSortReordersAndUpdates() {
        List<DragSortDTO> sorts = Arrays.asList(dto(1L, 5L), dto(2L, 3L));
        assertTrue(service.dragSort((entity, val) -> entity.setSort(val), sorts));
        assertNotNull(service.updatedBatch);
        assertEquals(2, service.updatedBatch.size());
        // 第 i 条 DTO 配上第 i 小的 sort 值：[3, 5]
        assertEquals(Long.valueOf(3L), service.updatedBatch.get(0).getSort());
        assertEquals(Long.valueOf(5L), service.updatedBatch.get(1).getSort());
    }

    // endregion

    // region ======== queryScrollPage ========

    @Test
    public void queryScrollPageEmptyKeepsScrollId() {
        TestQuery query = new TestQuery();
        query.setScrollId("99");
        Page<TestResult> page = service.queryScrollPage(query, TestEntity::getId);
        // 当前滚动位置查不到数据时，滚动ID保持不变
        assertEquals("99", page.getScrollId());
    }

    @Test
    public void queryScrollPageNormalAndNoMutation() {
        service.dbList.add(entity(1L));
        service.dbList.add(entity(2L));
        TestQuery query = new TestQuery();
        // paged = false 也必须施加滚动条件（本测试覆写 list 方法，条件体现在不查全表的行为上）
        query.setPaged(false);
        Page<TestResult> page = service.queryScrollPage(query, TestEntity::getId);
        assertEquals(2, page.getData().size());
        assertEquals("2", page.getScrollId());
        // 浅拷贝验证：入参未被就地填 scrollByAsc 默认值
        assertNull(query.getScrollByAsc());
    }

    @Test
    public void queryScrollPagePageSizeOverLimitThrows() {
        TestQuery query = new TestQuery();
        query.setPageSize(PageUtil.getMaxPageSize() + 1);
        try {
            service.queryScrollPage(query, TestEntity::getId);
            fail("超过最大分页大小应该报错");
        }
        catch (BusinessException ignored) { }
    }

    // endregion

    // region ======== editRecord / editBatch 置空字段 ========

    @Test
    public void editRecordWithClearFieldsMergesIntoSingleUpdate() {
        service.dbList.add(entity(1L));
        TestEditParam param = new TestEditParam();
        param.setId(1L);
        param.setName("新名字");
        param.setClearFields(Arrays.asList("name", "code"));
        service.editRecord(param);
        // 置空与更新合并成单条 UPDATE：只调用一次 update(entity, wrapper)，不再单独置空
        assertEquals(Collections.singletonList("update"), service.actions);
        assertTrue(service.cleared.isEmpty());
        // 置空子句带上了全部待置空列，且实体上与置空重合的非空新值被抹掉（置空优先）
        UpdateWrapper<TestEntity> wrapper = (UpdateWrapper<TestEntity>) service.lastUpdateWrapper;
        String sqlSet = wrapper.getSqlSet().toLowerCase();
        assertTrue(sqlSet.contains("name"));
        assertTrue(sqlSet.contains("code"));
        assertNull(service.lastUpdateEntity.getName());
    }

    @Test
    public void editRecordWithoutClearFieldsSkipsClear() {
        service.dbList.add(entity(1L));
        TestEditParam param = new TestEditParam();
        param.setId(1L);
        service.editRecord(param);
        // 未携带 clearFields 时不触发置空
        assertEquals(Collections.singletonList("updateById"), service.actions);
        assertTrue(service.cleared.isEmpty());
    }

    @Test
    public void editBatchAppliesClearFieldsForEditBranchOnly() {
        service.dbList.add(entity(1L));
        service.dbList.add(entity(2L));
        TestEditParam editWithClear = new TestEditParam();
        editWithClear.setId(1L);
        editWithClear.setClearFields(Collections.singletonList("code"));
        TestEditParam editWithoutClear = new TestEditParam();
        editWithoutClear.setId(2L);
        TestEditParam addWithClear = new TestEditParam();
        addWithClear.setId(3L);
        addWithClear.setClearFields(Collections.singletonList("name"));
        service.editBatch(Arrays.asList(editWithClear, editWithoutClear, addWithClear), false);
        // 携带置空字段的编辑项（id=1）与更新合并成单条 UPDATE，其余照常批量编辑；
        // 新增分支（id=3）即使携带 clearFields 也不处理
        assertEquals(Arrays.asList("updateBatchById", "update"), service.actions);
        assertTrue(service.cleared.isEmpty());
    }

    @Test
    public void editRecordClearFieldsFailsFastWhenMergeUnavailable() {
        // 模拟实体表信息不可用（未在 TableInfoHelper 注册）：直接失败，不做二次尝试
        service = new TestServiceImpl() {
            @Override
            @SuppressWarnings({"unchecked", "rawtypes"})
            public Class<TestEntity> getEntityClass() { return (Class) NotRegisteredEntity.class; }
        };
        service.dbList.add(entity(1L));
        TestEditParam param = new TestEditParam();
        param.setId(1L);
        param.setClearFields(Collections.singletonList("code"));
        try {
            service.editRecord(param);
            fail("无法合并置空子句时应该直接失败");
        }
        catch (BusinessException ignored) { }
        // 失败发生在任何写库动作之前
        assertTrue(service.actions.isEmpty());
    }

    // endregion

    private static String orderBySegment(MPJLambdaWrapper<?> wrapper) {
        String sql = wrapper.getExpression().getOrderBy().getSqlSegment();
        if (sql == null) { return ""; }
        // 渲染结果带 " ORDER BY " 前缀，归一化掉，只保留列与方向
        return sql.replace("ORDER BY", "").trim();
    }

    private static TestEntity entity(Long id) {
        TestEntity entity = new TestEntity();
        entity.setId(id);
        entity.setStatus(0);
        return entity;
    }

    private static DragSortDTO dto(Long id, Long sort) {
        DragSortDTO dto = new DragSortDTO();
        dto.setId(id);
        dto.setSort(sort);
        return dto;
    }

    // region ======== 测试用类型 ========

    private interface TestMapper extends MPJBaseMapper<TestEntity> { }

    // 未在 TableInfoHelper 注册的实体，用于模拟"实体表信息不可用"
    public static class NotRegisteredEntity { }

    public static class TestEntity {
        private Long id;
        private String name;
        private Integer status;
        private Long sort;
        private String code;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public Long getSort() { return sort; }
        public void setSort(Long sort) { this.sort = sort; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    public static class TestResult {
        private Long id;
        private String name;
        private Integer status;
        private Long sort;
        private String code;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public Long getSort() { return sort; }
        public void setSort(Long sort) { this.sort = sort; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    public static class TestAddParam {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class TestEditParam extends BaseEditParam {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class TestQuery extends BaseQuery {

        private List<Long> idList;

        public List<Long> getIdList() { return idList; }
        public void setIdList(List<Long> idList) { this.idList = idList; }
    }

    private static class TestServiceImpl
            extends BaseServiceImpl<TestMapper, TestEntity, TestAddParam, TestEditParam, TestQuery, TestResult> {

        private final List<TestEntity> dbList = new ArrayList<>();
        private List<TestEntity> updatedBatch;
        private boolean updateCalled;
        private long countResult;
        private MPJLambdaWrapper<TestEntity> lastListWrapper;
        private Map<String, String> sortColumnMapping;
        // 按发生顺序记录的动作，用于断言置空与更新的先后关系
        private final List<String> actions = new ArrayList<>();
        private final Map<Long, Collection<String>> cleared = new LinkedHashMap<>();
        private Wrapper<TestEntity> lastUpdateWrapper;
        private TestEntity lastUpdateEntity;

        @Override
        protected boolean applySort(MPJLambdaWrapper<TestEntity> wrapper, Class<?> entityClass,
                List<SortField> sortFields, Map<String, Object> options) {
            // 模拟业务覆写：options 为空时补充自定义列映射（原 getSortColumnMapping 的职责）
            if ((options == null || options.isEmpty()) && sortColumnMapping != null && !sortColumnMapping.isEmpty()) {
                options = Collections.singletonMap(MyBatisPlusFieldSorter.OPTION_COLUMN_MAPPING, sortColumnMapping);
            }
            return super.applySort(wrapper, entityClass, sortFields, options);
        }

        @Override
        public List<TestEntity> list(Wrapper<TestEntity> queryWrapper) {
            // enable 等方法走 LambdaQueryWrapper，queryPage 走 MPJLambdaWrapper，只捕获后者
            if (queryWrapper instanceof MPJLambdaWrapper) {
                lastListWrapper = (MPJLambdaWrapper<TestEntity>) queryWrapper;
            }
            return new ArrayList<>(dbList);
        }

        @Override
        public long count(Wrapper<TestEntity> queryWrapper) {
            return countResult;
        }

        @Override
        public TestEntity getById(Serializable id) {
            Long target = Long.valueOf(String.valueOf(id));
            for (TestEntity entity : dbList) {
                if (target.equals(entity.getId())) { return entity; }
            }
            return null;
        }

        @Override
        public boolean update(TestEntity entity, Wrapper<TestEntity> updateWrapper) {
            updateCalled = true;
            actions.add("update");
            lastUpdateWrapper = updateWrapper;
            lastUpdateEntity = entity;
            return true;
        }

        @Override
        public boolean updateById(TestEntity entity) {
            actions.add("updateById");
            return true;
        }

        @Override
        public boolean saveBatch(Collection<TestEntity> entityList, int batchSize) {
            return true;
        }

        @Override
        public boolean updateBatchById(Collection<TestEntity> entityList) {
            actions.add("updateBatchById");
            updatedBatch = new ArrayList<>(entityList);
            return true;
        }

        @Override
        public boolean clearFields(Object id, Collection<String> fields) {
            actions.add("clearFields:" + id);
            cleared.put(Long.valueOf(String.valueOf(id)), fields);
            return true;
        }

        @Override
        protected TestEntity fromAddParam(TestAddParam param) {
            TestEntity entity = new TestEntity();
            entity.setId(param.getId());
            entity.setName(param.getName());
            return entity;
        }

        @Override
        protected TestEntity fromEditParam(TestResult old, TestEditParam param) {
            TestEntity entity = new TestEntity();
            entity.setId(old.getId());
            entity.setName(param.getName());
            return entity;
        }

        @Override
        protected MPJLambdaWrapper<TestEntity> buildQueryWrapper(TestQuery query) {
            return new MPJLambdaWrapper<>();
        }

        @Override
        protected void processData(TestQuery query, List<TestResult> data) { }

        @Override
        protected void changeLog(Object bizId, Object oldData, Object newData, Class<?> targetClz, Object... arguments) { }
    }

    private static class EmptyValidator implements Validator {

        @Override
        public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
            return Collections.emptySet();
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
            return Collections.emptySet();
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
            return Collections.emptySet();
        }

        @Override
        public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
            return null;
        }

        @Override
        public <U> U unwrap(Class<U> type) {
            return null;
        }

        @Override
        public javax.validation.executable.ExecutableValidator forExecutables() {
            return null;
        }
    }

    // endregion

}
