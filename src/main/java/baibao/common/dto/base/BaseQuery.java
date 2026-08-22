/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.dto.base;

import baibao.common.enums.QueryMode;
import kunlun.data.sort.SortField;
import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import kunlun.common.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 基础的查询对象.
 * @author Kahle
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseQuery extends Page.Query implements Serializable {

    /**
     * 查询模式
     * @see QueryMode
     */
    @JsonIgnore
    private QueryMode queryMode = QueryMode.FULL;

    /**
     * 自定义排序字段列表（存储无关，顺序即排序优先级；为空时使用业务的默认排序）.
     * <p>示例：[{"field":"createTime","order":"desc"},{"field":"id","order":"asc"}]；
     * 字段名默认为实体属性名（驼峰），由排序器（{@code kunlun.data.sort.Sorter}）解析。
     */
    private List<SortField> sortFields;


    /**
     * 是否传入了自定义排序.
     *
     * @return 传入了返回 true
     */
    public boolean hasCustomSort() {

        return CollUtil.isNotEmpty(sortFields);
    }


    @JsonIgnore
    @Override
    public boolean isPaged() {

        return super.isPaged();
    }

    @JsonIgnore
    @Override
    public void setPaged(boolean paged) {

        super.setPaged(paged);
    }

}
