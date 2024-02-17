/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.test.entity;

import java.util.List;

public class Superman extends AbstractPerson {
    private List<SpecialSkill> skillList;

    public List<SpecialSkill> getSkillList() {

        return skillList;
    }

    public void setSkillList(List<SpecialSkill> skillList) {

        this.skillList = skillList;
    }

}
