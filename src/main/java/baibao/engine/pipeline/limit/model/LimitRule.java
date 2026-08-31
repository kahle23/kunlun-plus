/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.engine.pipeline.limit.model;

import java.util.List;

/**
 * 限制规则：挂在场景下的一条限制，声明"怎么算"与"不满足怎么办".
 * <p>
 * 怎么算——策略类型与载体：JAVA 时按 {@code strategyBean} 名经查找函数路由到
 * {@code LimitStrategy} 实现；SCRIPT 时执行 {@code scriptContent}（二期，由下游启用）.
 * 不满足怎么办——{@code effect}：BLOCK 拦截 / ALLOW 放行 / TIP 仅提示；
 * 与同场景其他规则的组合——{@code combineMode}：AND 参与拦截判定 / OR 不参与.
 * <p>
 * 纯字段载体，无持久化痕迹；下游的配置实体（表行）映射为本类即可.
 *
 * @author Kahle
 */
public class LimitRule {

    // region ======== 策略类型、组合方式与效果常量 ========
    /**
     * 策略类型：JAVA 策略 Bean（按 strategyBean 名路由）.
     */
    public static final String STRATEGY_JAVA = "JAVA";
    /**
     * 策略类型：脚本（执行 scriptContent，二期启用）.
     */
    public static final String STRATEGY_SCRIPT = "SCRIPT";
    /**
     * 组合方式：与同场景其他规则取与（失败参与拦截判定，默认）.
     */
    public static final String COMBINE_AND = "AND";
    /**
     * 组合方式：与同场景其他规则取或（失败不参与拦截判定）.
     */
    public static final String COMBINE_OR = "OR";
    /**
     * 效果：不满足时拦截.
     */
    public static final String EFFECT_BLOCK = "BLOCK";
    /**
     * 效果：不满足时放行（静默）.
     */
    public static final String EFFECT_ALLOW = "ALLOW";
    /**
     * 效果：不满足时仅提示（不拦截）.
     */
    public static final String EFFECT_TIP = "TIP";
    // endregion ======== 策略类型、组合方式与效果常量 ========


    /**
     * 规则编码（场景内唯一，如 READY-STOCK-VALUE）.
     */
    private String ruleCode;
    /**
     * 规则名称.
     */
    private String ruleName;
    /**
     * 策略类型：JAVA / SCRIPT（见 STRATEGY_* 常量，默认 JAVA）.
     */
    private String strategyType = STRATEGY_JAVA;
    /**
     * JAVA 策略 Bean 名（strategyType=JAVA 时必填，如 readyStockValueStrategy）.
     */
    private String strategyBean;
    /**
     * 脚本内容（strategyType=SCRIPT 时使用，二期启用）.
     */
    private String scriptContent;
    /**
     * 脚本引擎名（SCRIPT 时使用，如 javascript / groovy）.
     */
    private String scriptEngine;
    /**
     * 同场景内执行顺序（小者先执行）.
     */
    private int priority = 100;
    /**
     * 与同场景其他规则的组合：AND / OR（见 COMBINE_* 常量，默认 AND）.
     */
    private String combineMode = COMBINE_AND;
    /**
     * 不满足时的效果：BLOCK / ALLOW / TIP（见 EFFECT_* 常量，默认 BLOCK）.
     */
    private String effect = EFFECT_BLOCK;
    /**
     * 不满足时的提示语模板（支持 ${actualValue} 等占位符，由聚合阶段渲染）.
     */
    private String failMessage;
    /**
     * 是否启用（默认启用）.
     */
    private boolean enabled = true;
    /**
     * 维度阈值行集合（一行一个维度的阈值配置）.
     */
    private List<LimitRuleValue> values;

    public String getRuleCode() {

        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {

        this.ruleCode = ruleCode;
    }

    public String getRuleName() {

        return ruleName;
    }

    public void setRuleName(String ruleName) {

        this.ruleName = ruleName;
    }

    public String getStrategyType() {

        return strategyType;
    }

    public void setStrategyType(String strategyType) {

        this.strategyType = strategyType;
    }

    public String getStrategyBean() {

        return strategyBean;
    }

    public void setStrategyBean(String strategyBean) {

        this.strategyBean = strategyBean;
    }

    public String getScriptContent() {

        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {

        this.scriptContent = scriptContent;
    }

    public String getScriptEngine() {

        return scriptEngine;
    }

    public void setScriptEngine(String scriptEngine) {

        this.scriptEngine = scriptEngine;
    }

    public int getPriority() {

        return priority;
    }

    public void setPriority(int priority) {

        this.priority = priority;
    }

    public String getCombineMode() {

        return combineMode;
    }

    public void setCombineMode(String combineMode) {

        this.combineMode = combineMode;
    }

    public String getEffect() {

        return effect;
    }

    public void setEffect(String effect) {

        this.effect = effect;
    }

    public String getFailMessage() {

        return failMessage;
    }

    public void setFailMessage(String failMessage) {

        this.failMessage = failMessage;
    }

    public boolean isEnabled() {

        return enabled;
    }

    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    public List<LimitRuleValue> getValues() {

        return values;
    }

    public void setValues(List<LimitRuleValue> values) {

        this.values = values;
    }

}
