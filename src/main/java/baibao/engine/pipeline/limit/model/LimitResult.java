/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.engine.pipeline.limit.model;

import java.util.List;

/**
 * 限制校验的整体结果：一次校验（一个场景）的最终产物.
 * <p>
 * 由结果聚合阶段产出并置入上下文的输出槽位，经结果抽取器返回给钩子点调用方；
 * 调用方据 {@code passed} 决定拦截与否、据 {@code messages} 展示提示，
 * {@code ruleResults} 保留逐规则明细供留痕与排查"为什么拦了我".
 *
 * @author Kahle
 */
public class LimitResult {

    // region ======== 整体结果类型常量 ========
    /**
     * 整体结果类型：通过（无拦截与提示）.
     */
    public static final String RESULT_PASS = "PASS";
    /**
     * 整体结果类型：拦截（存在不满足且效果为 BLOCK 的规则）.
     */
    public static final String RESULT_BLOCK = "BLOCK";
    /**
     * 整体结果类型：提示（存在不满足的提示型规则，但放行）.
     */
    public static final String RESULT_TIP = "TIP";
    // endregion ======== 整体结果类型常量 ========


    /**
     * 是否通过（false 即拦截）.
     */
    private boolean passed;
    /**
     * 整体结果类型：PASS / BLOCK / TIP（见 RESULT_* 常量）.
     */
    private String resultType = RESULT_PASS;
    /**
     * 提示消息（渲染后的最终话术，含拦截明细与未配置提示）.
     */
    private List<String> messages;
    /**
     * 逐规则求值结果（含规则与命中阈值行快照、实际值、状态）.
     */
    private List<LimitRuleResult> ruleResults;

    public boolean isPassed() {

        return passed;
    }

    public void setPassed(boolean passed) {

        this.passed = passed;
    }

    public String getResultType() {

        return resultType;
    }

    public void setResultType(String resultType) {

        this.resultType = resultType;
    }

    public List<String> getMessages() {

        return messages;
    }

    public void setMessages(List<String> messages) {

        this.messages = messages;
    }

    public List<LimitRuleResult> getRuleResults() {

        return ruleResults;
    }

    public void setRuleResults(List<LimitRuleResult> ruleResults) {

        this.ruleResults = ruleResults;
    }

}
