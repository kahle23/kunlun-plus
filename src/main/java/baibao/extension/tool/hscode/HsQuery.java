package baibao.extension.tool.hscode;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HsQuery.
 * @author Kahle
 */
@Data
@NoArgsConstructor
public class HsQuery {

    // region ======== 分页信息 ========
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 每页条数
     */
    private Integer pageSize;
    // endregion

    // region ======== 查询条件 ========
    /**
     * 关键词
     */
    private String keywords;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品编码（即 HsCode）
     */
    private String code;
    // endregion

}
