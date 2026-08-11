/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 拖拽排序的传输对象.
 * @author Kahle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DragSortDTO implements Serializable {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空！")
    private Long id;
    /**
     * 排序
     */
    private Long sort;

}
