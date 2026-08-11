package baibao.enterprise.sportsdirect.dataapi.pojo.result;

import kunlun.data.map.FmMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

import static cn.hutool.core.convert.Convert.toStr;

/**
 * SDI 可视化 PWAR 的结果对象.
 * @author Kahle
 */
@Data
@NoArgsConstructor
public class VisualizerPwarResult implements Serializable, FmMap {

    /**
     * 产品描述
     */
    private String productDescription;
    /**
     * 版型
     */
    private String curve;
    /**
     * 颜色
     */
    private String colour;
    /**
     * 尺码
     */
    private String size;
    /**
     * 当前售价
     */
    private String currentSellPrice;
    /**
     * 价格已确认
     */
    private String pricingConfirmed;
    /**
     * 策略
     */
    private String strategy;
    /**
     * TW销售额
     */
    private String twSales;
    /**
     * 自动库存水平
     */
    private String autoLevels;
    /**
     * 审核类型
     */
    private String reviewType;
    /**
     * 唯一产品
     */
    private String uniqueProduct;
    /**
     * 对比库存
     */
    private String compStk;
    /**
     * 等级/评分
     */
    private String grade;
    /**
     * 仓库库存
     */
    private String whseStk;
    /**
     * 门店分配量
     */
    private String shopAllocation;
    /**
     * 网站最小库存
     */
    private String webMin;
    /**
     * 计划门店数
     */
    private String plannedStoreCount;
    /**
     * 实际门店数
     */
    private String actualStoreCount;
    /**
     * 订单量
     */
    private String orders;
    /**
     * 预算最后更新日期
     */
    private String budgetLastUpdated;
    /**
     * 生活方式内容策略
     */
    private String lifeStyleContStrat;
    /**
     * 首次收货日期
     */
    private String firstRcpt;
    /**
     * 滚动变更日期
     */
    private String rollingChangeDate;
    /**
     * 采购员
     */
    private String buyer;
    /**
     * 网页链接
     */
    private String webLink;
    /**
     * 类型
     */
    private String type;
    /**
     * 已预订数量
     */
    private String bookedQty;

    @Override
    public void fromMap(Map<?, ?> map) {
        this.setProductDescription(toStr(map.get("Product Description")));
        this.setCurve(toStr(map.get("Curve")));
        this.setColour(toStr(map.get("Colour")));
        this.setSize(toStr(map.get("Size")));
        this.setCurrentSellPrice(toStr(map.get("Current Sell Price")));
        this.setPricingConfirmed(toStr(map.get("Pricing Confirmed")));
        this.setStrategy(toStr(map.get("Strategy")));
        this.setTwSales(toStr(map.get("TW Sales")));
        this.setAutoLevels(toStr(map.get("Auto Levels")));
        this.setReviewType(toStr(map.get("Review Type")));
        this.setUniqueProduct(toStr(map.get("Unique Product")));
        this.setCompStk(toStr(map.get("Comp Stk")));
        this.setGrade(toStr(map.get("Grade")));
        this.setWhseStk(toStr(map.get("Whse Stk")));
        this.setShopAllocation(toStr(map.get("Shop Allocation")));
        this.setWebMin(toStr(map.get("Web min")));
        this.setPlannedStoreCount(toStr(map.get("Planned Store count")));
        this.setActualStoreCount(toStr(map.get("Actual Store count")));
        this.setOrders(toStr(map.get("Orders")));
        this.setBudgetLastUpdated(toStr(map.get("Budget Last Updated")));
        this.setLifeStyleContStrat(toStr(map.get("LifeStyle Cont Strat")));
        this.setFirstRcpt(toStr(map.get("First Rcpt")));
        this.setRollingChangeDate(toStr(map.get("Rolling Change Date")));
        this.setBuyer(toStr(map.get("Buyer")));
        this.setWebLink(toStr(map.get("Web Link")));
        this.setType(toStr(map.get("Type")));
        this.setBookedQty(toStr(map.get("Booked Qty")));
    }

}
