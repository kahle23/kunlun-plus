/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.test.dto;

public class SkillDTO {
    private String name;
    private String description;
    private Integer level;
    private String implementor;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public Integer getLevel() {

        return level;
    }

    public void setLevel(Integer level) {

        this.level = level;
    }

    public String getImplementor() {

        return implementor;
    }

    public void setImplementor(String implementor) {

        this.implementor = implementor;
    }

}
