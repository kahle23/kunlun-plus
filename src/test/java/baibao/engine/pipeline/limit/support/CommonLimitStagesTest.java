/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.engine.pipeline.limit.support;

import kunlun.core.function.Consumer;
import kunlun.core.function.Function;
import kunlun.engine.pipeline.PipelineContext;
import kunlun.engine.pipeline.base.DefaultPipelineContext;
import kunlun.engine.pipeline.limit.AbstractLimitEngine;
import baibao.engine.pipeline.limit.LimitStrategy;
import baibao.engine.pipeline.limit.model.LimitResult;
import baibao.engine.pipeline.limit.model.LimitRule;
import baibao.engine.pipeline.limit.model.LimitRuleResult;
import baibao.engine.pipeline.limit.model.LimitRuleValue;
import baibao.engine.pipeline.limit.model.LimitSceneConfig;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The common limit stages test: 候选筛选、结果聚合与三件套组装的端到端.
 *
 * @author Kahle
 */
public class CommonLimitStagesTest {

    // region ======== 测试素材 ========
    /**
     * 构建规则（指定组合方式、效果与话术）.
     */
    private static LimitRule rule(String ruleCode, String combineMode
            , String effect, String failMessage, String thresholdValue) {
        LimitRule rule = new LimitRule();
        rule.setRuleCode(ruleCode);
        rule.setCombineMode(combineMode);
        rule.setEffect(effect);
        rule.setFailMessage(failMessage);
        LimitRuleValue value = new LimitRuleValue();
        value.setDimensionType(LimitRuleValue.DIMENSION_GLOBAL);
        value.setThresholdValue(new BigDecimal(thresholdValue));
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>();
        values.add(value);
        rule.setValues(values);
        return rule;
    }

    /**
     * 构建不满足的单规则结果（含规则与命中阈值行快照、实际值）.
     */
    private static LimitRuleResult failResult(LimitRule rule, String actualValue) {
        LimitRuleResult result = new LimitRuleResult();
        result.setRule(rule);
        result.setMatchedValue(rule.getValues().get(0));
        result.setStatus(LimitRuleResult.STATUS_FAIL);
        result.setActualValue(new BigDecimal(actualValue));
        return result;
    }

    /**
     * 构建未配置的单规则结果.
     */
    private static LimitRuleResult notConfiguredResult(LimitRule rule, String message) {
        LimitRuleResult result = new LimitRuleResult();
        result.setRule(rule);
        result.setStatus(LimitRuleResult.STATUS_NOT_CONFIGURED);
        result.setMessage(message);
        return result;
    }

    /**
     * 构建带场景配置与逐规则结果的上下文.
     */
    private static PipelineContext context(LimitSceneConfig config, List<LimitRuleResult> ruleResults) {
        DefaultPipelineContext context = new DefaultPipelineContext("scene-1", null, null);
        context.setConfig(config);
        context.getStorage().put(CommonLimitRuleExecutor.STORAGE_KEY_RULE_RESULTS, ruleResults);
        return context;
    }

    /**
     * 场景配置（可选属性表）.
     */
    private static LimitSceneConfig sceneConfig(Map<String, Object> attributes) {
        LimitSceneConfig config = new LimitSceneConfig();
        config.setSceneCode("scene-1");
        config.setAttributes(attributes);
        return config;
    }

    /**
     * 跑聚合并取整体结果.
     */
    private static LimitResult aggregate(PipelineContext context) {
        new CommonLimitResultAggregator().accept(context);
        return (LimitResult) context.getRawOutput();
    }
    // endregion ======== 测试素材 ========


    // region ======== 候选筛选 ========
    @Test
    public void testSelectorFiltersDisabledAndSortsByPriority() {
        LimitRule priority30 = rule("RULE-30", null, null, null, "100");
        priority30.setPriority(30);
        LimitRule priority10 = rule("RULE-10", null, null, null, "100");
        priority10.setPriority(10);
        LimitRule disabled = rule("RULE-20", null, null, null, "100");
        disabled.setPriority(20);
        disabled.setEnabled(false);
        LimitSceneConfig config = sceneConfig(null);
        config.setRules(new ArrayList<LimitRule>(Arrays.asList(priority30, priority10, disabled)));
        DefaultPipelineContext context = new DefaultPipelineContext("scene-1", null, null);
        context.setConfig(config);
        new CommonLimitCandidateSelector().accept(context);
        List<?> candidates = (List<?>) context.getStorage()
                .get(CommonLimitCandidateSelector.STORAGE_KEY_CANDIDATES);
        // 未启用的规则剔除，其余按优先级升序.
        assertEquals(2, candidates.size());
        assertEquals("RULE-10", ((LimitRule) candidates.get(0)).getRuleCode());
        assertEquals("RULE-30", ((LimitRule) candidates.get(1)).getRuleCode());
    }

    @Test
    public void testSelectorReturnsEmptyWhenSceneDisabled() {
        LimitSceneConfig config = sceneConfig(null);
        config.setEnabled(false);
        config.setRules(new ArrayList<LimitRule>(Arrays.asList(rule("RULE-1", null, null, null, "100"))));
        DefaultPipelineContext context = new DefaultPipelineContext("scene-1", null, null);
        context.setConfig(config);
        new CommonLimitCandidateSelector().accept(context);
        List<?> candidates = (List<?>) context.getStorage()
                .get(CommonLimitCandidateSelector.STORAGE_KEY_CANDIDATES);
        // 场景停用（总开关）：不做任何限制.
        assertEquals(0, candidates.size());
    }

    @Test
    public void testSelectorRequiresLimitSceneConfig() {
        DefaultPipelineContext context = new DefaultPipelineContext("scene-1", null, null);
        context.setConfig(null);
        try {
            new CommonLimitCandidateSelector().accept(context);
            fail("Expected IllegalStateException for missing scene config. ");
        }
        catch (IllegalStateException ignore) { }
    }
    // endregion ======== 候选筛选 ========


    // region ======== 结果聚合 ========
    @Test
    public void testAggregatorBlocksOnFailedAndBlockRule() {
        LimitRule rule = rule("RULE-1", null, null, "已超限", "1000000");
        LimitResult result = aggregate(context(sceneConfig(null),
                new ArrayList<LimitRuleResult>(Arrays.asList(failResult(rule, "1200000")))));
        // 默认 AND + BLOCK：失败即拦截.
        assertEquals(false, result.isPassed());
        assertEquals(LimitResult.RESULT_BLOCK, result.getResultType());
        assertEquals(1, result.getMessages().size());
        // 规则未配话术模板时用话术原文（此处 failMessage 无占位符，渲染即原文）.
        assertTrue(result.getMessages().get(0).contains("已超限"));
    }

    @Test
    public void testAggregatorTipsWithoutBlocking() {
        LimitRule rule = rule("RULE-1", null, LimitRule.EFFECT_TIP, "接近上限", "1000000");
        LimitResult result = aggregate(context(sceneConfig(null),
                new ArrayList<LimitRuleResult>(Arrays.asList(failResult(rule, "1200000")))));
        // TIP：放行但提示.
        assertEquals(true, result.isPassed());
        assertEquals(LimitResult.RESULT_TIP, result.getResultType());
        assertEquals(1, result.getMessages().size());
    }

    @Test
    public void testAggregatorAllowsSilently() {
        LimitRule rule = rule("RULE-1", null, LimitRule.EFFECT_ALLOW, "已超限", "1000000");
        LimitResult result = aggregate(context(sceneConfig(null),
                new ArrayList<LimitRuleResult>(Arrays.asList(failResult(rule, "1200000")))));
        // ALLOW：静默放行.
        assertEquals(true, result.isPassed());
        assertEquals(LimitResult.RESULT_PASS, result.getResultType());
        assertEquals(0, result.getMessages().size());
    }

    @Test
    public void testAggregatorOrFailureDoesNotBlock() {
        LimitRule rule = rule("RULE-1", LimitRule.COMBINE_OR, LimitRule.EFFECT_BLOCK, "已超限", "1000000");
        LimitResult result = aggregate(context(sceneConfig(null),
                new ArrayList<LimitRuleResult>(Arrays.asList(failResult(rule, "1200000")))));
        // OR 规则失败不参与拦截.
        assertEquals(true, result.isPassed());
        assertEquals(LimitResult.RESULT_PASS, result.getResultType());
    }

    @Test
    public void testAggregatorNotConfiguredPassesWithMessageByDefault() {
        LimitRule rule = rule("RULE-1", null, null, null, "1000000");
        LimitResult result = aggregate(context(sceneConfig(null),
                new ArrayList<LimitRuleResult>(Arrays.asList(
                        notConfiguredResult(rule, CommonLimitRuleExecutor.DEFAULT_NOT_CONFIGURED_MESSAGE)))));
        // 未配置默认放行并提示.
        assertEquals(true, result.isPassed());
        assertEquals(LimitResult.RESULT_PASS, result.getResultType());
        assertEquals(1, result.getMessages().size());
    }

    @Test
    public void testAggregatorNotConfiguredBlocksBySceneAttribute() {
        LimitRule rule = rule("RULE-1", null, null, null, "1000000");
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(CommonLimitResultAggregator.ATTRIBUTE_NOT_CONFIGURED_ACTION, "BLOCK");
        LimitResult result = aggregate(context(sceneConfig(attributes),
                new ArrayList<LimitRuleResult>(Arrays.asList(
                        notConfiguredResult(rule, CommonLimitRuleExecutor.DEFAULT_NOT_CONFIGURED_MESSAGE)))));
        // 场景属性翻转为拦截.
        assertEquals(false, result.isPassed());
        assertEquals(LimitResult.RESULT_BLOCK, result.getResultType());
    }

    @Test
    public void testAggregatorRendersPlaceholders() {
        LimitRule rule = rule("RULE-1", null, null, "已用${actualValue}，上限${thresholdValue}", "1000000");
        LimitResult result = aggregate(context(sceneConfig(null),
                new ArrayList<LimitRuleResult>(Arrays.asList(failResult(rule, "1200000")))));
        // 占位符以实际值与命中阈值渲染.
        assertEquals(1, result.getMessages().size());
        assertEquals("已用1200000，上限1000000", result.getMessages().get(0));
    }

    @Test
    public void testAggregatorEmptyResultsPass() {
        DefaultPipelineContext context = new DefaultPipelineContext("scene-1", null, null);
        context.setConfig(sceneConfig(null));
        LimitResult result = aggregate(context);
        // 无规则结果（如场景无启用规则）：整体 PASS.
        assertEquals(true, result.isPassed());
        assertEquals(LimitResult.RESULT_PASS, result.getResultType());
    }
    // endregion ======== 结果聚合 ========


    // region ======== 三件套端到端 ========
    /**
     * 最小具体引擎：配置装载置入内存配置，核心三阶段装配通用实现.
     */
    private static class CommonLimitEngine extends AbstractLimitEngine {
        /**
         * 场景配置.
         */
        private final LimitSceneConfig config;
        /**
         * 策略查找函数（统一返回固定实际值的策略）.
         */
        private final Function<String, LimitStrategy> lookup;

        private CommonLimitEngine(LimitSceneConfig config, Function<String, LimitStrategy> lookup) {

            this.config = config;
            this.lookup = lookup;
        }

        @Override
        protected Consumer<PipelineContext> getConfigLoader() {
            return new Consumer<PipelineContext>() {
                @Override
                public void accept(PipelineContext context) {

                    context.setConfig(config);
                }
            };
        }

        @Override
        protected Consumer<PipelineContext> getCandidateSelector() {

            return new CommonLimitCandidateSelector();
        }

        @Override
        protected Consumer<PipelineContext> getRuleExecutor() {

            return new CommonLimitRuleExecutor(lookup);
        }

        @Override
        protected Consumer<PipelineContext> getResultAggregator() {

            return new CommonLimitResultAggregator();
        }
    }

    /**
     * 端到端素材：备货货值规则（ORG 1001 阈值 1000000，默认 <= 与 BLOCK）.
     */
    private static LimitSceneConfig readyStockScene() {
        LimitRule rule = rule("READY-STOCK-VALUE", null, null, "已用${actualValue}，上限${thresholdValue}", "1000000");
        rule.setStrategyBean("readyStockValueStrategy");
        LimitRuleValue orgValue = new LimitRuleValue();
        orgValue.setDimensionType(LimitRuleValue.DIMENSION_ORG);
        orgValue.setDimensionKey("orgId");
        orgValue.setDimensionValue("1001");
        orgValue.setThresholdValue(new BigDecimal("1000000"));
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>(Arrays.asList(orgValue));
        rule.setValues(values);
        LimitSceneConfig config = sceneConfig(null);
        config.setRules(new ArrayList<LimitRule>(Arrays.asList(rule)));
        return config;
    }

    /**
     * 固定实际值的策略查找函数.
     */
    private static Function<String, LimitStrategy> lookupOf(final String actualValue) {
        return new Function<String, LimitStrategy>() {
            @Override
            public LimitStrategy apply(String name) {
                return new LimitStrategy() {
                    @Override
                    public LimitRuleResult evaluate(PipelineContext context
                            , LimitRule rule, LimitRuleValue value) {
                        LimitRuleResult result = new LimitRuleResult();
                        result.setActualValue(new BigDecimal(actualValue));
                        return result;
                    }
                };
            }
        };
    }

    @Test
    public void testEndToEndBlocksWhenOverThreshold() {
        CommonLimitEngine engine = new CommonLimitEngine(readyStockScene(), lookupOf("1200000"));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        dimensions.put("orgId", "1001");
        LimitResult result = (LimitResult) engine.execute("purchase.demand.ready-precheck", dimensions, null);
        // 维度命中 ORG 阈值 1000000，实际 1200000 超限：拦截并给出渲染话术.
        assertEquals(false, result.isPassed());
        assertEquals(LimitResult.RESULT_BLOCK, result.getResultType());
        assertEquals("已用1200000，上限1000000", result.getMessages().get(0));
    }

    @Test
    public void testEndToEndPassesWhenWithinThreshold() {
        CommonLimitEngine engine = new CommonLimitEngine(readyStockScene(), lookupOf("900000"));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        dimensions.put("orgId", "1001");
        LimitResult result = (LimitResult) engine.execute("purchase.demand.ready-precheck", dimensions, null);
        // 实际 900000 在阈值内：通过.
        assertEquals(true, result.isPassed());
        assertEquals(LimitResult.RESULT_PASS, result.getResultType());
    }
    // endregion ======== 三件套端到端 ========

}
