/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.dto.base;

import baibao.common.enums.QueryMode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import kunlun.common.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
