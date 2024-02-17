/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.entity;

import java.io.Serializable;

public class Student extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long studentId;
    private String schoolName;
    private Boolean graduated;

    public Long getStudentId() {

        return this.studentId;
    }

    public void setStudentId(Long studentId) {

        this.studentId = studentId;
    }

    public String getSchoolName() {

        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {

        this.schoolName = schoolName;
    }

    public Boolean getGraduated() {

        return this.graduated;
    }

    public void setGraduated(Boolean graduated) {

        this.graduated = graduated;
    }

}
