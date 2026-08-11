/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.enums;

import kunlun.data.CodeDefinition;
import kunlun.util.Assert;

/**
 * 删除状态的枚举（删除状态：0 未删除，1 已删除）.
 * @author Kahle
 */
public enum DeleteStatus implements CodeDefinition {
    /**
     * 未删除
     */
    N(0, "未删除"),
    /**
     * 已删除
     */
    Y(1, "已删除"),

    ;

    private final Integer code;
    private String description;

    DeleteStatus(Integer code, String description) {
        this.description = description;
        this.code = code;
    }

    @Override
    public Integer getCode() {

        return code;
    }

    @Override
    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = Assert.notNull(description);
    }

    public static DeleteStatus parse(Object input) {
        if (input == null) { return null; }
        if (input instanceof DeleteStatus) {
            return (DeleteStatus) input;
        }
        int inputInt;
        if (!(input instanceof Integer)) {
            inputInt = Integer.parseInt(String.valueOf(input));
        }
        else { inputInt = (Integer) input; }
        DeleteStatus[] values = values();
        for (DeleteStatus value : values) {
            if (value.getCode().equals(inputInt)) { return value; }
        }
        return null;
    }

}
