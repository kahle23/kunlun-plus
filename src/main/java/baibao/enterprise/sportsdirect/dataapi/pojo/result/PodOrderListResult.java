package baibao.enterprise.sportsdirect.dataapi.pojo.result;

import kunlun.data.map.FmMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

import static cn.hutool.core.convert.Convert.toInt;
import static cn.hutool.core.convert.Convert.toStr;

/**
 * SDI的POD订单List表的结果对象.
 * @author Kahle
 */
@Data
@NoArgsConstructor
public class PodOrderListResult implements Serializable, FmMap {

    // region ======== 区域 ========// endregion
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 国家区域编码
     */
    private Integer warehouseCode;
    /**
     * IC编码（仓库编码）
     */
    private String icCode;
    /**
     * 财务年度周次
     */
    private String fiscalYearWeek;
    /**
     * 当前周
     */
    private Integer curveWeek;
    /**
     * 相对周
     */
    private Integer relWk;
    /**
     * 周起始日期
     */
    private String weekComDate;
    /**
     * 到期日/应到日期
     */
    private String dueDate;
    /**
     * 实际入库数量
     */
    private Integer incQty;
    /**
     * 原始订单编号
     */
    private String orderCode;
    /**
     * 入库数量（排除纯线上款）
     */
    private Integer incQtyExclWebOnly;
    /**
     * 订单编号（排除纯线上款）
     */
    private String orderCodeExclWebOnly;
    /**
     * 备注
     */
    private String remark;


    @Override
    public void fromMap(Map<?, ?> map) {
        this.setFiscalYearWeek(toStr(map.get("FISCALYEARWEEK")));
        this.setWeekComDate(toStr("WEEKCOMDATE"));
        this.setCurveWeek(toInt(map.get("CURVEWEEK")));
        this.setRelWk(toInt(map.get("RELWK")));
        this.setDueDate(toStr("DUEDATE"));
        this.setIncQty(toInt(map.get("INCQTY")));
        this.setOrderCode(toStr(map.get("ORDERCODE")));
        this.setIncQtyExclWebOnly(toInt(map.get("INCQTY_EXCL_WEB_ONLY")));
        this.setOrderCodeExclWebOnly(toStr(map.get("ORDERCODE_EXCL_WEB_ONLY")));
    }

}
