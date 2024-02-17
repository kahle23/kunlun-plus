/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.test.entity;

import java.util.List;

public class OrdinaryPerson extends AbstractPerson {
    private List<Skill> skillList;

    public List<Skill> getSkillList() {

        return skillList;
    }

    public void setSkillList(List<Skill> skillList) {

        this.skillList = skillList;
    }

}
