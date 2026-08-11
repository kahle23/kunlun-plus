package baibao.enterprise.sportsdirect.dataapi.pojo.result;

import kunlun.data.map.FmMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

import static cn.hutool.core.convert.Convert.toStr;

/**
 * SDI 可视化 PWAR 数据的结果对象.
 * @author Kahle
 */
@Data
@NoArgsConstructor
public class VisualizerPwarDataResult implements Serializable, FmMap {

    /**
     * 年份周数
     */
    private String yearWeek;
    /**
     * 周起始日期
     */
    private String weekCommencing;
    /**
     * 相关周/相对周数
     */
    private String relWk;
    /**
     * 网站可售库存
     */
    private String webAvailability;
    /**
     * 总可售库存
     */
    private String totalAvailability;
    /**
     * 可售性/库存状态
     */
    private String availability;
    /**
     * 门店可售库存
     */
    private String storeAvailability;
    /**
     * 平均售价
     */
    private String avgSell;
    /**
     * 利润率
     */
    private String margin;
    /**
     * 销售预测
     */
    private String forecast;
    /**
     * 销售完成率
     */
    private String salesForecast;
    /**
     * 贡献度/贡献额
     */
    private String contribution;
    /**
     * 门店销售额
     */
    private String storeSales;
    /**
     * 线上销售额
     */
    private String webSales;
    /**
     * 仓库库存
     */
    private String whsStk;
    /**
     * 门店库存
     */
    private String storeStk;
    /**
     * 网站库存
     */
    private String webStk;
    /**
     * 计划覆盖门店数
     */
    private String plannedStoreCount;
    /**
     * 实际覆盖门店数
     */
    private String actualStoreCount;
    /**
     * 订单数量
     */
    private String orders;
    /**
     * WB预算
     */
    private String wbBudget;
    /**
     * 订单代码
     */
    private String orderCode;
    /**
     * 累计预算
     */
    private String cumlBudgets;
    /**
     * 累计承诺量
     */
    private String cumlComm;
    /**
     * WB预算库存
     */
    private String wbBudgetStock;
    /**
     * 差异分配
     */
    private String varAlloc;
    /**
     * 旺销周
     */
    private String goodWeek;
    /**
     * 价格标志
     */
    private String priceFlag;
    /**
     * 数据加载标志
     */
    private String tranloadedFlag;
    /**
     * 门店可售性评分
     */
    private String storeAvaScore;
    /**
     * 门店分配量
     */
    private String storeAlloc;

    @Override
    public void fromMap(Map<?, ?> map) {
        this.setYearWeek(toStr(map.get("YEARWEEK")));
        this.setWeekCommencing(toStr(map.get("Week Commencing")));
        this.setRelWk(toStr(map.get("RELWK")));
        this.setWebAvailability(toStr(map.get("Web Availability")));
        this.setTotalAvailability(toStr(map.get("Total Availability")));
        this.setAvailability(toStr(map.get("Availability")));
        this.setStoreAvailability(toStr(map.get("Store Availability")));
        this.setAvgSell(toStr(map.get("Avg Sell")));
        this.setMargin(toStr(map.get("Margin")));
        this.setForecast(toStr(map.get("Forecast")));
        this.setSalesForecast(toStr(map.get("Sales / Forecast")));
        this.setContribution(toStr(map.get("Contribution")));
        this.setStoreSales(toStr(map.get("Store Sales")));
        this.setWebSales(toStr(map.get("Web Sales")));
        this.setWhsStk(toStr(map.get("Whs Stk")));
        this.setStoreStk(toStr(map.get("Store Stk")));
        this.setWebStk(toStr(map.get("Web Stk")));
        this.setPlannedStoreCount(toStr(map.get("Planned Store count")));
        this.setActualStoreCount(toStr(map.get("Actual Store count")));
        this.setOrders(toStr(map.get("Orders")));
        this.setWbBudget(toStr(map.get("WB Budget")));
        this.setOrderCode(toStr(map.get("ORDERCODE")));
        this.setCumlBudgets(toStr(map.get("CUMLBUDGETS")));
        this.setCumlComm(toStr(map.get("CUMLCOMM")));
        this.setWbBudgetStock(toStr(map.get("WB Budget Stock")));
        this.setVarAlloc(toStr(map.get("Varalloc")));
        this.setGoodWeek(toStr(map.get("Good Week")));
        this.setPriceFlag(toStr(map.get("PRICEFLAG")));
        this.setTranloadedFlag(toStr(map.get("Tranloaded Flag")));
        this.setStoreAvaScore(toStr(map.get("Store Ava Score")));
        this.setStoreAlloc(toStr(map.get("Store Alloc")));
    }

}
