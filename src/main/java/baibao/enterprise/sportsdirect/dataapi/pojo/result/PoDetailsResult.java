package baibao.enterprise.sportsdirect.dataapi.pojo.result;

import kunlun.data.json.JsonUtil;
import kunlun.data.map.FmMap;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import static cn.hutool.core.convert.Convert.*;

/**
 * SDI的PO详情表的简单结果对象.
 * @author Kahle
 */
@Data
@NoArgsConstructor
public class PoDetailsResult implements Serializable, FmMap {

    /**
     * 国家区域编码
     */
    private Integer warehouseCode;
    /**
     * IC编码（仓库编码）
     */
    private String icCode;

    /**
     * 颜色款式
     */
    private String colourWay;
    /**
     * 尺码描述
     */
    private String sizeDesc;
    /**
     * 款式描述
     */
    private String styleDesc;
    /**
     * 通用产品代码
     */
    private String upc;
    /**
     * 上市日期
     */
    private String launchDate;
    /**
     * 已上市周数
     */
    private String wksIn;
    /**
     * 剩余周数
     */
    private String wksRem;
    /**
     * 线上商品图
     */
    private String webImage;
    /**
     * 仅限线上销售
     */
    private String webOnly;
    /**
     * 类型
     */
    private String type;
    /**
     * 季节代码
     */
    private String seasonCode;
    /**
     * 箱数
     */
    private String cartonCount;
    /**
     * 每箱数量
     */
    private String cartonQty;
    /**
     * 集装箱尺寸（英尺）
     */
    private String containerSizeFt;
    /**
     * 集装箱数量
     */
    private String containerQty;
    /**
     * 预算额 (4周)
     */
    private String budget4wk;
    /**
     * 销售额 (4周)
     */
    private String sales4wk;
    /**
     * 达成率 (4周)
     */
    private String performance4wk;
    /**
     * 门店分配数量 (4周)
     */
    private String storeAlloc4;
    /**
     * 门店可用率 (4周)
     */
    private String storeAvailability4wk;
    /**
     * 门店可用性评分 (4周)
     */
    private String storeAvaScore4;
    /**
     * 预算额 (8周)
     */
    private String budget8wk;
    /**
     * 销售额 (8周)
     */
    private String sales8wk;
    /**
     * 达成率 (8周)
     */
    private String performance8wk;
    /**
     * 门店分配数量 (8周)
     */
    private String storeAlloc8;
    /**
     * 门店可用率 (8周)
     */
    private String storeAvailability8wk;
    /**
     * 门店可用性评分 (8周)
     */
    private String storeAvaScore8;
    /**
     * 预算额 (16周)
     */
    private String budget16wk;
    /**
     * 销售额 (16周)
     */
    private String sales16wk;
    /**
     * 达成率 (16周)
     */
    private String performance16wk;
    /**
     * 门店分配数量 (16周)
     */
    private String storeAlloc16;
    /**
     * 门店可用率 (16周)
     */
    private String storeAvailability16wk;
    /**
     * 门店可用性评分 (16周)
     */
    private String storeAvaScore16;
    /**
     * 门店分配数量 (52周)
     */
    private String storeAlloc52;
    /**
     * 门店可用性评分 (52周)
     */
    private String storeAvaScore52;
    /**
     * 前期预算
     */
    private String prevBudget;
    /**
     * 年度预算额
     */
    private String annualBudget;
    /**
     * 年度销售额
     */
    private String annualSales;
    /**
     * 年度达成率
     */
    private String annualPerformance;
    /**
     * 年度门店可用率
     */
    private String annualStoreAvailability;
    /**
     * 年度贡献度
     */
    private String annualContribution;
    /**
     * 本单数量
     */
    private String onThisOrder;
    /**
     * 年度总部订单次数
     */
    private String hqsYr;
    /**
     * 贡献度排名
     */
    private String contRank;
    /**
     * ABC等级
     */
    private String rnkAbc;
    /**
     * 复盘类型
     */
    private String reviewType;
    /**
     * 销售曲线
     */
    private String curve;

    /**
     * 可售周数0
     */
    private BigDecimal week_0;
    /**
     * 可售周数1
     */
    private BigDecimal week_1;
    /**
     * 可售周数2
     */
    private BigDecimal week_2;
    /**
     * 可售周数3
     */
    private BigDecimal week_3;
    /**
     * 可售周数4
     */
    private BigDecimal week_4;
    /**
     * 可售周数5
     */
    private BigDecimal week_5;
    /**
     * 可售周数6
     */
    private BigDecimal week_6;
    /**
     * 可售周数7
     */
    private BigDecimal week_7;
    /**
     * 可售周数8
     */
    private BigDecimal week_8;
    /**
     * 可售周数9
     */
    private BigDecimal week_9;
    /**
     * 可售周数10
     */
    private BigDecimal week_10;
    /**
     * 可售周数11
     */
    private BigDecimal week_11;
    /**
     * 可售周数12
     */
    private BigDecimal week_12;
    /**
     * 可售周数13
     */
    private BigDecimal week_13;
    /**
     * 可售周数14
     */
    private BigDecimal week_14;
    /**
     * 可售周数15
     */
    private BigDecimal week_15;
    /**
     * 可售周数16
     */
    private BigDecimal week_16;
    /**
     * 可售周数17
     */
    private BigDecimal week_17;
    /**
     * 可售周数18
     */
    private BigDecimal week_18;
    /**
     * 可售周数19
     */
    private BigDecimal week_19;
    /**
     * 可售周数20
     */
    private BigDecimal week_20;
    /**
     * 可售周数21
     */
    private BigDecimal week_21;
    /**
     * 可售周数22
     */
    private BigDecimal week_22;
    /**
     * 可售周数23
     */
    private BigDecimal week_23;
    /**
     * 可售周数24
     */
    private BigDecimal week_24;
    /**
     * 可售周数25
     */
    private BigDecimal week_25;
    /**
     * 可售周数26
     */
    private BigDecimal week_26;
    /**
     * 可售周数27
     */
    private BigDecimal week_27;
    /**
     * 可售周数28
     */
    private BigDecimal week_28;
    /**
     * 可售周数29
     */
    private BigDecimal week_29;
    /**
     * 可售周数30
     */
    private BigDecimal week_30;
    /**
     * 可售周数31
     */
    private BigDecimal week_31;
    /**
     * 可售周数32
     */
    private BigDecimal week_32;
    /**
     * 可售周数33
     */
    private BigDecimal week_33;
    /**
     * 可售周数34
     */
    private BigDecimal week_34;
    /**
     * 可售周数35
     */
    private BigDecimal week_35;
    /**
     * 可售周数36
     */
    private BigDecimal week_36;
    /**
     * 可售周数37
     */
    private BigDecimal week_37;
    /**
     * 可售周数38
     */
    private BigDecimal week_38;
    /**
     * 可售周数39
     */
    private BigDecimal week_39;

    /**
     * 订单数0
     */
    private Integer order_0;
    /**
     * 订单数1
     */
    private Integer order_1;
    /**
     * 订单数2
     */
    private Integer order_2;
    /**
     * 订单数3
     */
    private Integer order_3;
    /**
     * 订单数4
     */
    private Integer order_4;
    /**
     * 订单数5
     */
    private Integer order_5;
    /**
     * 订单数6
     */
    private Integer order_6;
    /**
     * 订单数7
     */
    private Integer order_7;
    /**
     * 订单数8
     */
    private Integer order_8;
    /**
     * 订单数9
     */
    private Integer order_9;
    /**
     * 订单数10
     */
    private Integer order_10;
    /**
     * 订单数11
     */
    private Integer order_11;
    /**
     * 订单数12
     */
    private Integer order_12;
    /**
     * 订单数13
     */
    private Integer order_13;
    /**
     * 订单数14
     */
    private Integer order_14;
    /**
     * 订单数15
     */
    private Integer order_15;
    /**
     * 订单数16
     */
    private Integer order_16;
    /**
     * 订单数17
     */
    private Integer order_17;
    /**
     * 订单数18
     */
    private Integer order_18;
    /**
     * 订单数19
     */
    private Integer order_19;
    /**
     * 订单数20
     */
    private Integer order_20;
    /**
     * 订单数21
     */
    private Integer order_21;
    /**
     * 订单数22
     */
    private Integer order_22;
    /**
     * 订单数23
     */
    private Integer order_23;
    /**
     * 订单数24
     */
    private Integer order_24;
    /**
     * 订单数25
     */
    private Integer order_25;
    /**
     * 订单数26
     */
    private Integer order_26;
    /**
     * 订单数27
     */
    private Integer order_27;
    /**
     * 订单数28
     */
    private Integer order_28;
    /**
     * 订单数29
     */
    private Integer order_29;
    /**
     * 订单数30
     */
    private Integer order_30;
    /**
     * 订单数31
     */
    private Integer order_31;
    /**
     * 订单数32
     */
    private Integer order_32;
    /**
     * 订单数33
     */
    private Integer order_33;
    /**
     * 订单数34
     */
    private Integer order_34;
    /**
     * 订单数35
     */
    private Integer order_35;
    /**
     * 订单数36
     */
    private Integer order_36;
    /**
     * 订单数37
     */
    private Integer order_37;
    /**
     * 订单数38
     */
    private Integer order_38;
    /**
     * 订单数39
     */
    private Integer order_39;

    @Override
    public void fromMap(Map<?, ?> map) {
        this.setColourWay(toStr(map.get("COLOURWAY")));
        this.setStyleDesc(toStr(map.get("Style Desc")));
        this.setSizeDesc(toStr(map.get("Size Desc")));
        this.setUpc(toStr(map.get("UPC")));
        this.setWarehouseCode(toInt(map.get("Warehouse")));
        this.setStoreAlloc4(toStr(map.get("StoreAlloc4")));
        this.setStoreAlloc8(toStr(map.get("StoreAlloc8")));
        this.setStoreAlloc16(toStr(map.get("StoreAlloc16")));
        this.setStoreAlloc52(toStr(map.get("StoreAlloc52")));
        this.setStoreAvaScore4(toStr(map.get("StoreAvaScore4")));
        this.setStoreAvaScore8(toStr(map.get("StoreAvaScore8")));
        this.setStoreAvaScore16(toStr(map.get("StoreAvaScore16")));
        this.setStoreAvaScore52(toStr(map.get("StoreAvaScore52")));
        this.setStoreAvailability4wk(toStr(map.get("Store Availability 4wk")));
        this.setStoreAvailability8wk(toStr(map.get("Store Availability 8wk")));
        this.setStoreAvailability16wk(toStr(map.get("Store Availability 16wk")));
        this.setAnnualStoreAvailability(toStr(map.get("Annual Store Availability")));
        this.setSales4wk(toStr(map.get("Sales 4wk")));
        this.setBudget4wk(toStr(map.get("Budget 4wk")));
        this.setPerformance4wk(toStr(map.get("Performance 4wk")));
        this.setSales8wk(toStr(map.get("Sales 8wk")));
        this.setBudget8wk(toStr(map.get("Budget 8wk")));
        this.setPerformance8wk(toStr(map.get("Performance 8wk")));
        this.setSales16wk(toStr(map.get("Sales 16wk")));
        this.setBudget16wk(toStr(map.get("Budget 16wk")));
        this.setPerformance16wk(toStr(map.get("Performance 16wk")));
        this.setAnnualSales(toStr(map.get("Annual Sales")));
        this.setAnnualBudget(toStr(map.get("Annual Budget")));
        this.setAnnualPerformance(toStr(map.get("Annual Performance")));
        this.setOnThisOrder(toStr(map.get("On this order")));
        this.setCartonCount(toStr(map.get("Carton Count")));
        this.setPrevBudget(toStr(map.get("Prev Budget")));
        this.setCartonQty(toStr(map.get("Carton Qty")));
        this.setContainerSizeFt(toStr(map.get("Size of container in Ft")));
        this.setContainerQty(toStr(map.get("Container Qty")));
        this.setHqsYr(toStr(map.get("HQs/yr")));
        this.setWebImage(JsonUtil.toJsonString("jackson", Arrays.asList(toStr(map.get("Web Image")))));
        this.setWeek_0(toBigDecimal(map.get("0")));
        this.setWeek_1(toBigDecimal(map.get("1")));
        this.setWeek_2(toBigDecimal(map.get("2")));
        this.setWeek_3(toBigDecimal(map.get("3")));
        this.setWeek_4(toBigDecimal(map.get("5")));
        this.setWeek_5(toBigDecimal(map.get("6")));
        this.setWeek_7(toBigDecimal(map.get("7")));
        this.setWeek_8(toBigDecimal(map.get("8")));
        this.setWeek_9(toBigDecimal(map.get("9")));
        this.setWeek_10(toBigDecimal(map.get("10")));
        this.setWeek_11(toBigDecimal(map.get("11")));
        this.setWeek_12(toBigDecimal(map.get("12")));
        this.setWeek_13(toBigDecimal(map.get("13")));
        this.setWeek_14(toBigDecimal(map.get("14")));
        this.setWeek_15(toBigDecimal(map.get("15")));
        this.setWeek_16(toBigDecimal(map.get("16")));
        this.setWeek_17(toBigDecimal(map.get("17")));
        this.setWeek_18(toBigDecimal(map.get("18")));
        this.setWeek_19(toBigDecimal(map.get("19")));
        this.setWeek_20(toBigDecimal(map.get("20")));
        this.setWeek_21(toBigDecimal(map.get("21")));
        this.setWeek_22(toBigDecimal(map.get("22")));
        this.setWeek_23(toBigDecimal(map.get("23")));
        this.setWeek_24(toBigDecimal(map.get("24")));
        this.setWeek_25(toBigDecimal(map.get("25")));
        this.setWeek_26(toBigDecimal(map.get("26")));
        this.setWeek_27(toBigDecimal(map.get("27")));
        this.setWeek_28(toBigDecimal(map.get("28")));
        this.setWeek_29(toBigDecimal(map.get("29")));
        this.setWeek_1(toBigDecimal(map.get("28")));
        this.setWeek_30(toBigDecimal(map.get("30")));
        this.setWeek_31(toBigDecimal(map.get("31")));
        this.setWeek_32(toBigDecimal(map.get("32")));
        this.setWeek_33(toBigDecimal(map.get("33")));
        this.setWeek_34(toBigDecimal(map.get("34")));
        this.setWeek_35(toBigDecimal(map.get("35")));
        this.setWeek_36(toBigDecimal(map.get("36")));
        this.setWeek_37(toBigDecimal(map.get("37")));
        this.setWeek_38(toBigDecimal(map.get("38")));
        this.setWeek_39(toBigDecimal(map.get("39")));
        this.setOrder_0(toInt(map.get("0 ")));
        this.setOrder_1(toInt(map.get("1 ")));
        this.setOrder_2(toInt(map.get("2 ")));
        this.setOrder_3(toInt(map.get("3 ")));
        this.setOrder_4(toInt(map.get("4 ")));
        this.setOrder_5(toInt(map.get("5 ")));
        this.setOrder_6(toInt(map.get("6 ")));
        this.setOrder_7(toInt(map.get("7 ")));
        this.setOrder_8(toInt(map.get("8 ")));
        this.setOrder_9(toInt(map.get("9 ")));
        this.setOrder_10(toInt(map.get("10 ")));
        this.setOrder_11(toInt(map.get("11 ")));
        this.setOrder_12(toInt(map.get("12 ")));
        this.setOrder_13(toInt(map.get("13 ")));
        this.setOrder_14(toInt(map.get("14 ")));
        this.setOrder_15(toInt(map.get("15 ")));
        this.setOrder_16(toInt(map.get("16 ")));
        this.setOrder_17(toInt(map.get("17 ")));
        this.setOrder_18(toInt(map.get("18 ")));
        this.setOrder_19(toInt(map.get("19 ")));
        this.setOrder_20(toInt(map.get("20 ")));
        this.setOrder_21(toInt(map.get("21 ")));
        this.setOrder_22(toInt(map.get("22 ")));
        this.setOrder_23(toInt(map.get("23 ")));
        this.setOrder_24(toInt(map.get("24 ")));
        this.setOrder_25(toInt(map.get("25 ")));
        this.setOrder_26(toInt(map.get("26 ")));
        this.setOrder_27(toInt(map.get("27 ")));
        this.setOrder_28(toInt(map.get("28 ")));
        this.setOrder_29(toInt(map.get("29 ")));
        this.setOrder_30(toInt(map.get("30 ")));
        this.setOrder_31(toInt(map.get("31 ")));
        this.setOrder_32(toInt(map.get("32 ")));
        this.setOrder_33(toInt(map.get("33 ")));
        this.setOrder_34(toInt(map.get("34 ")));
        this.setOrder_35(toInt(map.get("35 ")));
        this.setOrder_36(toInt(map.get("36 ")));
        this.setOrder_37(toInt(map.get("37 ")));
        this.setOrder_38(toInt(map.get("38 ")));
        this.setOrder_39(toInt(map.get("39 ")));
        this.setContRank(toStr(map.get("CONT_RANK")));
        this.setRnkAbc(toStr(map.get("RNK_ABC")));
        this.setIcCode(toStr(map.get("IC Code")));
        this.setReviewType(toStr(map.get("ReviewType")));
        this.setWksIn(toStr(map.get("Wks In")));
        this.setWebOnly(toStr(map.get("Web Only")));
        this.setCurve(toStr(map.get("Curve")));
        this.setAnnualContribution(toStr(map.get("Annual Contribution")));
        this.setSeasonCode(toStr(map.get("Season Code")));
        this.setWksRem(toStr(map.get("Wks Rem")));
        this.setLaunchDate(toStr(map.get("Launch Date")));
        this.setType(toStr(map.get("Type")));
    }

}
