/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.dto.base;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * 基础的编辑入参对象.
 * @author Kahle
 */
@Data
public abstract class BaseEditParam implements Serializable {

    /**
     * 待置空的字段名集合（实体属性名，驼峰）。编辑更新时这些字段的置空子句会与更新合并为同一条
     * UPDATE（绕过 NOT_NULL 更新策略；无法合并时直接失败，不做二次尝试）；
     * 主键、审计填充字段、未命中实体列的字段会被自动跳过；
     * 同一字段若既携带非空入参值又在该集合中，以置空为准.
     * @see baibao.db.jdbc.mybatisplus.base.BaseService#clearFields(Object, Collection)
     */
    private Collection<String> clearFields;


    /**
     * 是否携带了待置空字段.
     *
     * @return 携带了返回 true
     */
    public boolean hasClearFields() {

        return CollUtil.isNotEmpty(clearFields);
    }

}
