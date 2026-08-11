/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件的传输对象.
 * @author Zerox
 */
@Data
@NoArgsConstructor
public class FileDTO implements Serializable {
    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空！")
    private String name;
    /**
     * 文件地址
     */
    @NotBlank(message = "文件地址不能为空！")
    private String addr;
    /**
     * 文件预览地址
     */
    private String preview;
    /**
     * 文件备注
     */
    private String remark;

    public FileDTO(String name, String addr) {
        this.name = name;
        this.addr = addr;
    }

}
