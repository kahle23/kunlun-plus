/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.engine.pipeline.limit.support;

import kunlun.core.function.Consumer;
import kunlun.engine.pipeline.PipelineContext;
import kunlun.engine.pipeline.base.AbstractPipelineConfig;
import kunlun.renderer.RenderUtil;
import kunlun.util.StrUtil;
import baibao.engine.pipeline.limit.model.LimitResult;
import baibao.engine.pipeline.limit.model.LimitRule;
import baibao.engine.pipeline.limit.model.LimitRuleResult;
import baibao.engine.pipeline.limit.model.LimitRuleValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用结果聚合阶段：把逐规则求值产物聚合为单一校验结果.
 * <p>
 * 聚合语义（各判定拆在 {@link #aggregate} 中，可覆写）：
 * <ul>
 *     <li>组合：AND 规则（默认）的失败参与拦截判定，OR 规则的失败不参与（静默）；</li>
 *     <li>效果：失败的 AND 规则按 effect 裁决——BLOCK 拦截 / TIP 放行但提示 / ALLOW 静默放行；</li>
 *     <li>未配置（NOT_CONFIGURED）：默认放行并提示，场景属性
 *     {@code not-configured-action=BLOCK}（见 {@link #ATTRIBUTE_NOT_CONFIGURED_ACTION}）可翻转为拦截；</li>
 *     <li>整体：存在拦截即 BLOCK（passed=false），否则存在提示即 TIP，否则 PASS；</li>
 *     <li>话术：规则声明了 failMessage 时经 {@link #renderMessage} 渲染占位符
 *     （{@code ${actualValue}} / {@code ${thresholdValue}} / 明细 detail 的键，渲染器取配置的
 *     rendererName，空则用默认渲染器），未声明时用结果自带的消息.</li>
 * </ul>
 * 读取 storage 中核心求值阶段写入的逐规则结果（键见
 * {@link CommonLimitRuleExecutor#STORAGE_KEY_RULE_RESULTS}，无即空列表——整体 PASS），
 * 产出 {@link LimitResult} 置入上下文输出槽位（{@code setRawOutput}），
 * 经基座后处理透传后由结果抽取器返回.
 *
 * @author Kahle
 */
public class CommonLimitResultAggregator implements Consumer<PipelineContext> {

    /**
     * 场景属性键：未配置（NOT_CONFIGURED）时的处置——PASS 放行并提示（默认）/ BLOCK 拦截.
     */
    public static final String ATTRIBUTE_NOT_CONFIGURED_ACTION = "not-configured-action";

    @Override
    public void accept(PipelineContext context) {
        List<LimitRuleResult> ruleResults = getRuleResults(context);
        context.setRawOutput(aggregate(context, ruleResults));
    }

    /**
     * 取核心求值阶段写入的逐规则结果（storage 中无即空列表）.
     *
     * @param context 管道引擎上下文
     * @return 逐规则求值结果列表
     */
    protected List<LimitRuleResult> getRuleResults(PipelineContext context) {
        Object results = context.getStorage().get(CommonLimitRuleExecutor.STORAGE_KEY_RULE_RESULTS);
        if (!(results instanceof List)) { return new ArrayList<LimitRuleResult>(); }
        List<LimitRuleResult> ruleResults = new ArrayList<LimitRuleResult>();
        for (Object result : (List<?>) results) {
            ruleResults.add((LimitRuleResult) result);
        }
        return ruleResults;
    }

    /**
     * 聚合：逐规则结果 → 单一校验结果.
     *
     * @param context 管道引擎上下文
     * @param ruleResults 逐规则求值结果列表
     * @return 整体校验结果
     */
    protected LimitResult aggregate(PipelineContext context, List<LimitRuleResult> ruleResults) {
        boolean blocked = false;
        boolean tipped = false;
        List<String> messages = new ArrayList<String>();
        boolean notConfiguredBlock = isNotConfiguredBlock(context);
        for (LimitRuleResult ruleResult : ruleResults) {
            if (ruleResult == null) { continue; }
            String status = ruleResult.getStatus();
            if (LimitRuleResult.STATUS_FAIL.equals(status)) {
                LimitRule rule = ruleResult.getRule();
                // 无规则快照时按最严口径处理（AND + BLOCK）.
                boolean combinedByAnd = rule == null || !LimitRule.COMBINE_OR.equals(rule.getCombineMode());
                String effect = rule == null || StrUtil.isBlank(rule.getEffect())
                        ? LimitRule.EFFECT_BLOCK : rule.getEffect();
                if (!combinedByAnd) { continue; }
                if (LimitRule.EFFECT_TIP.equals(effect)) {
                    tipped = true;
                    addMessage(messages, renderMessage(context, ruleResult));
                }
                else if (LimitRule.EFFECT_BLOCK.equals(effect)) {
                    blocked = true;
                    addMessage(messages, renderMessage(context, ruleResult));
                }
                // ALLOW：静默放行.
            }
            else if (LimitRuleResult.STATUS_NOT_CONFIGURED.equals(status)) {
                if (notConfiguredBlock) { blocked = true; }
                addMessage(messages, ruleResult.getMessage());
            }
            // PASS：无需处理.
        }
        LimitResult limitResult = new LimitResult();
        limitResult.setRuleResults(ruleResults);
        limitResult.setMessages(messages);
        if (blocked) {
            limitResult.setPassed(false);
            limitResult.setResultType(LimitResult.RESULT_BLOCK);
        }
        else if (tipped) {
            limitResult.setPassed(true);
            limitResult.setResultType(LimitResult.RESULT_TIP);
        }
        else {
            limitResult.setPassed(true);
            limitResult.setResultType(LimitResult.RESULT_PASS);
        }
        return limitResult;
    }

    /**
     * 渲染不满足规则的话术：规则声明了 failMessage 时渲染占位符，否则用结果自带的消息.
     * <p>
     * 占位符数据：ruleCode / actualValue / thresholdValue 与结果明细（detail）的全部键值；
     * 渲染器取配置的 rendererName（空则默认渲染器，即 {@code ${key}} 形制）.
     *
     * @param context 管道引擎上下文
     * @param ruleResult 不满足的单规则结果
     * @return 渲染后的话术（可能为 null，由调用方决定是否收集）
     */
    protected String renderMessage(PipelineContext context, LimitRuleResult ruleResult) {
        LimitRule rule = ruleResult.getRule();
        if (rule == null || StrUtil.isBlank(rule.getFailMessage())) {
            return ruleResult.getMessage();
        }
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("ruleCode", rule.getRuleCode());
        data.put("actualValue", ruleResult.getActualValue());
        LimitRuleValue matchedValue = ruleResult.getMatchedValue();
        if (matchedValue != null) { data.put("thresholdValue", matchedValue.getThresholdValue()); }
        if (ruleResult.getDetail() != null) { data.putAll(ruleResult.getDetail()); }
        String template = rule.getFailMessage();
        Object config = context.getConfig();
        String rendererName = config instanceof AbstractPipelineConfig
                ? ((AbstractPipelineConfig) config).getRendererName() : null;
        return StrUtil.isNotBlank(rendererName)
                ? RenderUtil.renderToString(rendererName, template, data)
                : RenderUtil.renderToString(template, data);
    }

    /**
     * 未配置（NOT_CONFIGURED）是否按拦截处置：场景属性 not-configured-action=BLOCK 时为 true.
     *
     * @param context 管道引擎上下文
     * @return true 表示未配置即拦截
     */
    protected boolean isNotConfiguredBlock(PipelineContext context) {
        Object config = context.getConfig();
        if (!(config instanceof AbstractPipelineConfig)) { return false; }
        Map<String, Object> attributes = ((AbstractPipelineConfig) config).getAttributes();
        if (attributes == null) { return false; }
        Object action = attributes.get(ATTRIBUTE_NOT_CONFIGURED_ACTION);
        return action != null && LimitResult.RESULT_BLOCK.equalsIgnoreCase(String.valueOf(action));
    }

    private void addMessage(List<String> messages, String message) {
        if (StrUtil.isNotBlank(message)) { messages.add(message); }
    }

}
