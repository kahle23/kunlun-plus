package artoria.data.comparison;

import artoria.data.comparison.support.difference.FieldCompareResult;
import artoria.data.comparison.support.difference.FieldDifferenceComparator;
import artoria.entity.User;
import artoria.mock.MockUtils;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FieldDifferenceComparatorTest {
    private static final FieldDifferenceComparator comparator1 = new FieldDifferenceComparator(Boolean.TRUE);
    private static final FieldDifferenceComparator comparator = new FieldDifferenceComparator();
    private static final Logger log = LoggerFactory.getLogger(FieldDifferenceComparatorTest.class);

    @Test
    public void test1() {
        User user1 = MockUtils.mock(User.class);
        User user2 = MockUtils.mock(User.class);
        List<FieldCompareResult> list = comparator.compare(user1, user2);
        log.info("compare: {}", JSON.toJSONString(list, true));
    }

    @Test
    public void test2() {
        User user1 = MockUtils.mock(User.class);
        User user2 = MockUtils.mock(User.class);
        user2.setUsername(null);
        user2.setPassword(null);
        List<FieldCompareResult> list = comparator1.compare(user1, user2);
        log.info("compare: {}", JSON.toJSONString(list, true));
    }

}
