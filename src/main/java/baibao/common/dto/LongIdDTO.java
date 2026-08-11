/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * ID的传输对象.
 * @author Kahle
 */
public class LongIdDTO implements Serializable {
    /**
     * ID
     */
    @NotNull(message = "ID不能为空！")
    private Long id;

    public LongIdDTO(Long id) {

        this.id = id;
    }

    public LongIdDTO() {

    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    @Override
    public String toString() {
        return "LongIdDTO{" +
                "id=" + id +
                '}';
    }
}
