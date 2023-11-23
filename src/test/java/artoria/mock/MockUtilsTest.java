package artoria.mock;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.test.entity.other.Book;
import artoria.test.entity.system.User;
import artoria.util.TypeUtils;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(MockUtilsTest.class);

    static {

        MockUtils.setMockProvider(new JMockDataProvider());
    }

    @Test
    public void testMock1() {
        Book book = MockUtils.mock(Book.class);
        log.info(JSON.toJSONString(book));
        User user = MockUtils.mock(User.class);
        log.info(JSON.toJSONString(user));
    }

    @Test
    public void testMock2() {
        List<Book> bookList = MockUtils.mock(TypeUtils.parameterizedOf(List.class, Book.class));
        log.info(JSON.toJSONString(bookList, Boolean.TRUE));
    }

    @Test
    public void testMock3() {
//        Menu menu = MockUtils.mock(Menu.class);
//        System.out.println(JSON.toJSONString(menu));
    }

    @Test
    public void testMock11() {
        MockUtils.setMockProvider(new SimpleMockProvider1());
        ClassMockerConfig classMockerConfig = new ClassMockerConfig();
        classMockerConfig.setType(User.class);
        Map<String, Mocker> map = new HashMap<String, Mocker>();
        map.put("name", new NameMocker());
        map.put("author", new NameMocker());
        classMockerConfig.setMockerMap(map);

        User[] bookArray = MockUtils.mock(User[].class, classMockerConfig);
        System.out.println(JSON.toJSONString(bookArray, Boolean.TRUE));
    }

}
