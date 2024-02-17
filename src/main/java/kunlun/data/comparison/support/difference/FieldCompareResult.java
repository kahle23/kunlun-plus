/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.data.comparison.support.difference;

/**
 * The field compare result.
 * @author Kahle
 */
public class FieldCompareResult {
    private String name;
    private String description;
    private Object leftValue;
    private Object rightValue;

    public FieldCompareResult(String name, String description) {
        this.description = description;
        this.name = name;
    }

    public FieldCompareResult() {

    }

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

    public Object getLeftValue() {

        return leftValue;
    }

    public void setLeftValue(Object leftValue) {

        this.leftValue = leftValue;
    }

    public Object getRightValue() {

        return rightValue;
    }

    public void setRightValue(Object rightValue) {

        this.rightValue = rightValue;
    }

}
