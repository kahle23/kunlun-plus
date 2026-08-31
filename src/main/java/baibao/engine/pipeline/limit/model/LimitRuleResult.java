/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.engine.pipeline.limit.model;

import java.util.Map;

/**
 * 单规则的求值结果：一条限制规则一次求值的产物.
 * <p>
 * 由策略（或框架代判）产出，经 {@code context.getStorage()} 传递给结果聚合阶段，
 * 也是执行留痕（写执行日志表）的数据来源；规则与命中阈值行做快照归属，
 * 供聚合与留痕直接取用而不必回查.
 * <p>
 * 判定的两种给法见 {@code LimitStrategy}：策略直接置状态，
 * 或只置 actualValue、状态留空由核心求值阶段按运算符代判.
 *
 * @author Kahle
 */
public class LimitRuleResult {

    // region ======== 状态常量 ========
    /**
     * 状态：通过（规则满足）.
     */
    public static final String STATUS_PASS = "PASS";
    /**
     * 状态：不通过（规则不满足，是否拦截由规则的效果决定）.
     */
    public static final String STATUS_FAIL = "FAIL";
    /**
     * 状态：未配置（三级维度都未命中阈值行，默认放行并提示，可按场景属性翻转）.
     */
    public static final String STATUS_NOT_CONFIGURED = "NOT_CONFIGURED";
    // endregion ======== 状态常量 ========


    /**
     * 本次求值的规则（快照归属，由核心求值阶段回填）.
     */
    private LimitRule rule;
    /**
     * 命中当前维度的阈值行（快照归属，由核心求值阶段回填；未配置时为 null）.
     */
    private LimitRuleValue matchedValue;
    /**
     * 求值状态：PASS / FAIL / NOT_CONFIGURED（见 STATUS_* 常量；null 表示留给框架代判）.
     */
    private String status;
    /**
     * 实际值（策略算出的指标值，如已用货值 + 本次申请）.
     */
    private Number actualValue;
    /**
     * 提示语或说明（未配置时为默认话术；聚合阶段可用规则话术模板覆盖）.
     */
    private String message;
    /**
     * 明细数据：策略产出的额外键值（如 usagedAmount / currAmount），
     * 供提示语占位符渲染与留痕快照使用.
     */
    private Map<String, Object> detail;

    public LimitRule getRule() {

        return rule;
    }

    public void setRule(LimitRule rule) {

        this.rule = rule;
    }

    public LimitRuleValue getMatchedValue() {

        return matchedValue;
    }

    public void setMatchedValue(LimitRuleValue matchedValue) {

        this.matchedValue = matchedValue;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public Number getActualValue() {

        return actualValue;
    }

    public void setActualValue(Number actualValue) {

        this.actualValue = actualValue;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public Map<String, Object> getDetail() {

        return detail;
    }

    public void setDetail(Map<String, Object> detail) {

        this.detail = detail;
    }

}
