/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.test.dto;

import java.util.List;

public class SimplePersonDTO {
    private String name;
    private String gender;
    private String wisdom;
    private List<String> skillList;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getGender() {

        return gender;
    }

    public void setGender(String gender) {

        this.gender = gender;
    }

    public String getWisdom() {

        return wisdom;
    }

    public void setWisdom(String wisdom) {

        this.wisdom = wisdom;
    }

    public List<String> getSkillList() {

        return skillList;
    }

    public void setSkillList(List<String> skillList) {

        this.skillList = skillList;
    }

}
