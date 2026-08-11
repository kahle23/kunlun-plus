/*
 * Copyright (c) 2019. the original author or authors.
 * BaiBao is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.common.dto;

import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * ID集合的传输对象.
 * @author Kahle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LongIdsDTO implements Serializable {
    /**
     * ID集合
     */
    @NotEmpty(message = "ID集合不能为空！")
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    private List<Long> ids;

}
