package baibao.extension.tool.hscode;

import kunlun.data.Dict;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * HsCode.
 * @author Kahle
 */
@Data
public class HsData {
    // region ======== 基础信息 ========
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品编码（即 HsCode）
     */
    private String code;
    /**
     * 第一法定单位
     */
    private String firstLegalUnit;
    /**
     * 第二法定单位
     */
    private String secondLegalUnit;
    // endregion

    // region ======== 税率信息 ========
    /**
     * 普通税率
     */
    private BigDecimal basicTaxRate;
    /**
     * 增值税率
     */
    private BigDecimal vatRate;
    // endregion

    // region ======== 申报要素 ========
    /**
     * 申报要素
     */
    private List<String> declarationElements;
    // endregion

    // region ======== 其他 ========
    /**
     * 其他
     */
    private Dict others;
    // endregion

    public HsData() {
        this.declarationElements = new ArrayList<>();
        this.others = new Dict();
    }

}
