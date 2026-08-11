/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 表单字段的实体对象.
 * @author Kahle
 */
public class FormField {
    /**
     * 字段唯一标识符，用于后端存储或数据绑定（如 user_name）
     */
    @NotBlank(message = "字段唯一标识不能为空！")
    private String key;
    /**
     * 字段名称，显示给用户的标签（如 "用户名"）
     */
    @NotBlank(message = "字段名称不能为空！")
    private String label;
    /**
     * 描述，帮助性说明（如 "请输入您的真实姓名"）
     */
    private String description;
    /**
     * 字段类型：0 缺省，1 文本，2 数字，3 日期，4 日期时间，11 单文件上传
     */
    @NotNull(message = "字段类型不能为空！")
    private Integer type;
    /**
     * 是否必填，强制用户填写
     */
    private Boolean required;

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }

    public String getLabel() {

        return label;
    }

    public void setLabel(String label) {

        this.label = label;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public Integer getType() {

        return type;
    }

    public void setType(Integer type) {

        this.type = type;
    }

    public Boolean getRequired() {

        return required;
    }

    public void setRequired(Boolean required) {

        this.required = required;
    }
}
