/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.test.entity;

import java.util.Date;

public interface Animal {

    String getName();

    void setName(String name);

    Integer getGender();

    void setGender(Integer gender);

    Date getBirthday();

    void setBirthday(Date birthday);

    Integer getAge();

    void setAge(Integer age);

}
