/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.enums;

import kunlun.data.CodeDefinition;
import kunlun.util.Assert;

/**
 * 是否成功的枚举（删除状态：0 失败，1 成功）.
 * @author Kahle
 */
public enum Success implements CodeDefinition {
    /**
     * 失败
     */
    N(0, "失败"),
    /**
     * 成功
     */
    Y(1, "成功"),
    ;

    private final Integer code;
    private String description;

    Success(Integer code, String description) {
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

    public static Success parse(Object input) {
        if (input == null) { return null; }
        if (input instanceof Success) {
            return (Success) input;
        }
        int inputInt;
        if (!(input instanceof Integer)) {
            inputInt = Integer.parseInt(String.valueOf(input));
        }
        else { inputInt = (Integer) input; }
        Success[] values = values();
        for (Success value : values) {
            if (value.getCode().equals(inputInt)) { return value; }
        }
        return null;
    }

}
