package artoria.exchange;

import artoria.entity.Student;
import artoria.random.RandomUtils;
import artoria.util.TypeUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FastJsonProviderTest {
    private Student data = new Student();
    private List<Student> data1 = new ArrayList<Student>();
    private String jsonString = null;
    private String jsonString1 = null;

    @Before
    public void init() {
        JsonUtils.setJsonProvider(new FastJsonProvider(true));
        data = RandomUtils.nextObject(Student.class);
        for (int i = 0; i < 5; i++) {
            data1.add(RandomUtils.nextObject(Student.class));
        }
        jsonString = JsonUtils.toJsonString(data);
        jsonString1 = JsonUtils.toJsonString(data1);
    }

    @Test
    public void test1() {
        System.out.println(JsonUtils.toJsonString(data));
        System.out.println(JsonUtils.toJsonString(data1));
        System.out.println(JsonUtils.toJsonString(data));
    }

    @Test
    public void test2() {
        JsonUtils.setJsonProvider(new FastJsonProvider(true));
        Student student = JsonUtils.parseObject(jsonString, Student.class);
        List<Student> list = JsonUtils.parseObject(jsonString1
                , TypeUtils.parameterizedOf(List.class, Student.class));
        System.out.println(JsonUtils.toJsonString(student));
        System.out.println("----");
        for (Student student1 : list) {
            System.out.println(JsonUtils.toJsonString(student1));
        }
    }

}
