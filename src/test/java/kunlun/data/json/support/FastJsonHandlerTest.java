/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.json.support;

import kunlun.common.constant.Words;
import kunlun.data.json.JsonUtils;
import kunlun.entity.Student;
import kunlun.mock.MockUtils;
import kunlun.util.TypeUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static kunlun.data.json.JsonFormat.PRETTY_FORMAT;

public class FastJsonHandlerTest {
    private final List<Student> data1 = new ArrayList<Student>();
    private Student data = new Student();
    private String jsonString = null;
    private String jsonString1 = null;

    @Before
    public void init() {
        JsonUtils.registerHandler(Words.DEFAULT, new FastJsonHandler());
        data = MockUtils.mock(Student.class);
        for (int i = 0; i < 5; i++) {
            data1.add(MockUtils.mock(Student.class));
        }
        jsonString = JsonUtils.toJsonString(data, PRETTY_FORMAT);
        jsonString1 = JsonUtils.toJsonString(data1, PRETTY_FORMAT);
    }

    @Test
    public void test1() {
        System.out.println(JsonUtils.toJsonString(data, PRETTY_FORMAT));
        System.out.println(JsonUtils.toJsonString(data1, PRETTY_FORMAT));
        System.out.println(JsonUtils.toJsonString(data, PRETTY_FORMAT));
    }

    @Test
    public void test2() {
        JsonUtils.registerHandler(Words.DEFAULT, new FastJsonHandler());
        Student student = JsonUtils.parseObject(jsonString, Student.class);
        List<Student> list = JsonUtils.parseObject(jsonString1
                , TypeUtils.parameterizedOf(List.class, Student.class));
        System.out.println(JsonUtils.toJsonString(student, PRETTY_FORMAT));
        System.out.println("----");
        for (Student student1 : list) {
            System.out.println(JsonUtils.toJsonString(student1));
        }
    }

}
