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
 * 启用/禁用的传输对象.
 * @author Kahle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnableDTO implements Serializable {
    /**
     * 数据记录ID
     */
    @NotNull(message = "数据记录ID不能为空！")
    private Long    recordId;
    /**
     * 启用/禁用状态值：0 未启用，1 启用
     * @see baibao.common.enums.Enabled
     */
    @NotNull(message = "启用/禁用状态值不能为空！")
    private Integer enabled;

}
