/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.enums;

import kunlun.data.CodeDefinition;
import kunlun.util.Assert;

/**
 * 是否启用的枚举（删除状态：0 禁用，1 启用）.
 * @author Kahle
 */
public enum Enabled implements CodeDefinition {
    /**
     * 未启用
     */
    N(0, "禁用"),
    /**
     * 启用
     */
    Y(1, "启用"),
    ;

    private final Integer code;
    private String description;

    Enabled(Integer code, String description) {
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

    public static Enabled parse(Object input) {
        if (input == null) { return null; }
        if (input instanceof Enabled) {
            return (Enabled) input;
        }
        int inputInt;
        if (!(input instanceof Integer)) {
            inputInt = Integer.parseInt(String.valueOf(input));
        }
        else { inputInt = (Integer) input; }
        Enabled[] values = values();
        for (Enabled value : values) {
            if (value.getCode().equals(inputInt)) { return value; }
        }
        return null;
    }

}
