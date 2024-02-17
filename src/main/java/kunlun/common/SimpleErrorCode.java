/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.common;

import kunlun.data.ErrorCode;

public class SimpleErrorCode implements ErrorCode {
    private String description;
    private String code;

    public SimpleErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public SimpleErrorCode() {

    }

    @Override
    public String getCode() {

        return code;
    }

    public void setCode(String code) {

        this.code = code;
    }

    @Override
    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    @Override
    public String toString() {
        return "SimpleErrorCode{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
