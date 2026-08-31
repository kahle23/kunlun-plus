/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.engine.pipeline.limit.model;

import java.math.BigDecimal;

/**
 * 限制规则的维度阈值行：同一条规则按维度配置的阈值数值.
 * <p>
 * 一条规则可配多行阈值（不同维度/不同值），执行时按上下文维度匹配，
 * 取值优先级 USER 精确 &gt; ORG 精确 &gt; GLOBAL 兜底
 * （匹配语义见 {@code support} 子包的 CommonLimitRuleExecutor）：
 * 精确维度行以 dimensionKey 从上下文取维度值，与 dimensionValue 按字符串相等比较；
 * GLOBAL 行为兜底默认（键值为空即命中）.
 * <p>
 * 纯字段载体，无持久化痕迹；下游的配置实体（表行）映射为本类即可.
 *
 * @author Kahle
 */
public class LimitRuleValue {

    // region ======== 维度类型与运算符常量 ========
    /**
     * 维度类型：个人维度（dimensionKey 通常为 userId）.
     */
    public static final String DIMENSION_USER = "USER";
    /**
     * 维度类型：部门维度（dimensionKey 通常为 orgId）.
     */
    public static final String DIMENSION_ORG = "ORG";
    /**
     * 维度类型：全局兜底（键值为空，未配维度时使用）.
     */
    public static final String DIMENSION_GLOBAL = "GLOBAL";
    /**
     * 维度类型：自定义维度（预留，通用实现默认不参与匹配）.
     */
    public static final String DIMENSION_CUSTOM = "CUSTOM";
    /**
     * 运算符：实际值 &lt;= 阈值（默认）.
     */
    public static final String OPERATOR_LTE = "<=";
    /**
     * 运算符：实际值 &lt; 阈值.
     */
    public static final String OPERATOR_LT = "<";
    /**
     * 运算符：实际值 &gt;= 阈值.
     */
    public static final String OPERATOR_GTE = ">=";
    /**
     * 运算符：实际值 &gt; 阈值.
     */
    public static final String OPERATOR_GT = ">";
    /**
     * 运算符：实际值 = 阈值.
     */
    public static final String OPERATOR_EQ = "=";
    // endregion ======== 维度类型与运算符常量 ========


    /**
     * 维度类型：USER / ORG / GLOBAL / CUSTOM.
     */
    private String dimensionType;
    /**
     * 维度键：精确维度行从上下文取维度值的键名（如 orgId / userId），GLOBAL 行为空.
     */
    private String dimensionKey;
    /**
     * 维度取值：与上下文维度值按字符串相等比较的配置值（如 1001 / 88），GLOBAL 行为空.
     */
    private String dimensionValue;
    /**
     * 阈值数值（如 3000000）.
     */
    private BigDecimal thresholdValue;
    /**
     * 比较运算符：实际值与阈值比较的方式，默认 {@code <=}（见 OPERATOR_* 常量）.
     */
    private String thresholdOperator = OPERATOR_LTE;
    /**
     * 单位（元/数量/% 等，仅展示用）.
     */
    private String unit;
    /**
     * 是否启用（默认启用）.
     */
    private boolean enabled = true;

    public String getDimensionType() {

        return dimensionType;
    }

    public void setDimensionType(String dimensionType) {

        this.dimensionType = dimensionType;
    }

    public String getDimensionKey() {

        return dimensionKey;
    }

    public void setDimensionKey(String dimensionKey) {

        this.dimensionKey = dimensionKey;
    }

    public String getDimensionValue() {

        return dimensionValue;
    }

    public void setDimensionValue(String dimensionValue) {

        this.dimensionValue = dimensionValue;
    }

    public BigDecimal getThresholdValue() {

        return thresholdValue;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {

        this.thresholdValue = thresholdValue;
    }

    public String getThresholdOperator() {

        return thresholdOperator;
    }

    public void setThresholdOperator(String thresholdOperator) {

        this.thresholdOperator = thresholdOperator;
    }

    public String getUnit() {

        return unit;
    }

    public void setUnit(String unit) {

        this.unit = unit;
    }

    public boolean isEnabled() {

        return enabled;
    }

    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

}
