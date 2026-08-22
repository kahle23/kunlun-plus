package baibao.db.jdbc.mybatisplus.base;

import baibao.common.dto.base.BaseQuery;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import kunlun.data.validation.support.javax.ValidationUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * editBatch1 的删除集合推导测试（纯内存，不依赖数据库）.
 * 重点回归：ignoreSingleError = true 时，编辑失败的旧记录不能进入待删除集合。
 * @author Kahle
 */
public class BaseServiceImplEditBatch1Test {

    private static final Function<List<TestEditParam>, TestQuery> QUERY_BUILDER = params -> new TestQuery();
    private static final BiFunction<TestEditParam, TestResult, String> KEY_BY_ID = (param, rst) -> {
        if (param != null) {
            return param.getId() != null ? String.valueOf(param.getId()) : "NEW";
        }
        return rst != null && rst.getId() != null ? String.valueOf(rst.getId()) : "UNKNOWN";
    };

    private TestServiceImpl service;

    @BeforeClass
    public static void initValidator() {
        try {
            ValidationUtil.setValidator(Validation.buildDefaultValidatorFactory().getValidator());
        }
        catch (ValidationException e) {
            // 测试类路径没有 Bean Validation 实现时用空实现兜底（测试参数本身没有约束注解）
            ValidationUtil.setValidator(new EmptyValidator());
        }
    }

    @Before
    public void setUp() {
        service = new TestServiceImpl();
    }

    @Test
    public void editFailedRecordMustNotBeDeleted() {
        service.oldEntities.add(entity(1L, "old-1"));
        service.oldEntities.add(entity(2L, "old-2"));
        service.oldEntities.add(entity(3L, "old-3"));
        service.failEditNames.add("new-2");
        Map<String, Exception> errors = service.editBatch1(
                Arrays.asList(editParam(1L, "new-1"), editParam(2L, "new-2")),
                QUERY_BUILDER, KEY_BY_ID, true, false);
        // 只有 id = 2 的参数构建失败
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey("2"));
        // 编辑成功的记录进入批量更新
        assertNotNull(service.updated);
        assertEquals(Collections.singletonList(1L), ids(service.updated));
        // 只有未被参数引用的 3 被删除，编辑失败的 2 必须保留
        assertNotNull(service.removed);
        assertEquals(Collections.singleton(3L), new HashSet<>(service.removed));
        assertNull(service.saved);
    }

    @Test
    public void halfSuccessOnSameRecordKeepsRecord() {
        service.oldEntities.add(entity(1L, "old-1"));
        service.failEditNames.add("new-1x");
        Map<String, Exception> errors = service.editBatch1(
                Arrays.asList(editParam(1L, "new-1"), editParam(1L, "new-1x")),
                QUERY_BUILDER, KEY_BY_ID, true, false);
        assertEquals(1, errors.size());
        assertNotNull(service.updated);
        assertEquals(Collections.singletonList(1L), ids(service.updated));
        // 记录既被成功编辑过就不算"查询到的 - 当前的"，不能删除
        assertNull(service.removed);
    }

    @Test
    public void nullIdParamGoesToAddBranch() {
        service.oldEntities.add(entity(1L, "old-1"));
        Map<String, Exception> errors = service.editBatch1(
                Arrays.asList(editParam(1L, "new-1"), editParam(null, "added")),
                QUERY_BUILDER, KEY_BY_ID, true, false);
        assertTrue(String.valueOf(errors.values()), errors.isEmpty());
        // 无 id 的参数走新增分支
        assertNotNull(service.saved);
        assertEquals(1, service.saved.size());
        assertEquals("added", service.saved.get(0).getName());
        assertNotNull(service.updated);
        assertEquals(Collections.singletonList(1L), ids(service.updated));
        assertNull(service.removed);
    }

    @Test
    public void skipDeleteKeepsRecords() {
        service.oldEntities.add(entity(1L, "old-1"));
        service.oldEntities.add(entity(2L, "old-2"));
        Map<String, Exception> errors = service.editBatch1(
                Collections.singletonList(editParam(1L, "new-1")),
                QUERY_BUILDER, KEY_BY_ID, true, true);
        assertTrue(errors.isEmpty());
        assertNotNull(service.updated);
        assertNull(service.removed);
    }

    @Test
    public void errorPropagatesWhenNotIgnoring() {
        service.oldEntities.add(entity(1L, "old-1"));
        service.oldEntities.add(entity(2L, "old-2"));
        service.failEditNames.add("new-2");
        try {
            service.editBatch1(Arrays.asList(editParam(1L, "new-1"), editParam(2L, "new-2")),
                    QUERY_BUILDER, KEY_BY_ID, false, false);
            fail("ignoreSingleError = false 时异常应该向上抛出");
        }
        catch (IllegalStateException ignored) { }
    }

    private static List<Long> ids(List<TestEntity> entities) {
        List<Long> ids = new ArrayList<>();
        for (TestEntity entity : entities) { ids.add(entity.getId()); }
        return ids;
    }

    private static TestEntity entity(Long id, String name) {
        TestEntity entity = new TestEntity();
        entity.setId(id);
        entity.setName(name);
        return entity;
    }

    private static TestEditParam editParam(Long id, String name) {
        TestEditParam param = new TestEditParam();
        param.setId(id);
        param.setName(name);
        return param;
    }

    // region ======== 测试用类型 ========

    private interface TestMapper extends MPJBaseMapper<TestEntity> { }

    public static class TestEntity {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class TestResult {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class TestAddParam {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class TestEditParam {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class TestQuery extends BaseQuery { }

    private static class TestServiceImpl
            extends BaseServiceImpl<TestMapper, TestEntity, TestAddParam, TestEditParam, TestQuery, TestResult> {

        private final List<TestEntity> oldEntities = new ArrayList<>();
        private final Set<String> failEditNames = new HashSet<>();
        private List<TestEntity> saved;
        private List<TestEntity> updated;
        private Collection<?> removed;

        @Override
        public List<TestResult> queryList(TestQuery query) {
            List<TestResult> list = new ArrayList<>();
            for (TestEntity entity : oldEntities) {
                TestResult result = new TestResult();
                result.setId(entity.getId());
                result.setName(entity.getName());
                list.add(result);
            }
            return list;
        }

        @Override
        public boolean saveBatch(Collection<TestEntity> entityList) {
            saved = new ArrayList<>(entityList);
            return true;
        }

        @Override
        public boolean updateBatchById(Collection<TestEntity> entityList) {
            updated = new ArrayList<>(entityList);
            return true;
        }

        @Override
        public boolean removeByIds(Collection<?> idList) {
            removed = new ArrayList<>(idList);
            return true;
        }

        @Override
        protected TestEntity fromAddParam(TestAddParam param) {
            return entity(param.getId(), param.getName());
        }

        @Override
        protected TestEntity fromEditParam(TestResult old, TestEditParam param) {
            if (failEditNames.contains(param.getName())) {
                throw new IllegalStateException("模拟编辑转换失败: " + param.getId());
            }
            return entity(old.getId(), param.getName());
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
