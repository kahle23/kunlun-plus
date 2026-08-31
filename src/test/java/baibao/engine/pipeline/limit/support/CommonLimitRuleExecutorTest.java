/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.engine.pipeline.limit.support;

import kunlun.core.function.Function;
import kunlun.engine.pipeline.PipelineContext;
import kunlun.engine.pipeline.base.DefaultPipelineContext;
import baibao.engine.pipeline.limit.LimitStrategy;
import baibao.engine.pipeline.limit.model.LimitRule;
import baibao.engine.pipeline.limit.model.LimitRuleResult;
import baibao.engine.pipeline.limit.model.LimitRuleValue;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * The common limit rule executor test.
 *
 * @author Kahle
 */
public class CommonLimitRuleExecutorTest {

    /**
     * 测试策略：只回实际值（状态留空，交给运算符代判），并记录收到的阈值行.
     */
    private static class FixedActualStrategy implements LimitStrategy {
        /**
         * 要返回的实际值.
         */
        private final Number actualValue;
        /**
         * 收到的阈值行.
         */
        private LimitRuleValue receivedValue;

        private FixedActualStrategy(Number actualValue) {

            this.actualValue = actualValue;
        }

        @Override
        public LimitRuleResult evaluate(PipelineContext context, LimitRule rule, LimitRuleValue value) {

            receivedValue = value;
            LimitRuleResult result = new LimitRuleResult();
            result.setActualValue(actualValue);
            return result;
        }
    }

    /**
     * 构建阈值行.
     */
    private static LimitRuleValue value(String dimensionType, String dimensionKey
            , String dimensionValue, String thresholdValue, String operator, boolean enabled) {
        LimitRuleValue value = new LimitRuleValue();
        value.setDimensionType(dimensionType);
        value.setDimensionKey(dimensionKey);
        value.setDimensionValue(dimensionValue);
        value.setThresholdValue(new BigDecimal(thresholdValue));
        value.setThresholdOperator(operator);
        value.setEnabled(enabled);
        return value;
    }

    /**
     * 构建规则（JAVA 策略 testStrategy，挂给定阈值行）.
     */
    private static LimitRule rule(List<LimitRuleValue> values) {
        LimitRule rule = new LimitRule();
        rule.setRuleCode("READY-STOCK-VALUE");
        rule.setStrategyBean("testStrategy");
        rule.setValues(values);
        return rule;
    }

    /**
     * 构建上下文（转换后输入 = 维度表）并把候选规则放进 storage.
     */
    private static PipelineContext context(Map<String, Object> dimensions, LimitRule rule) {
        DefaultPipelineContext context = new DefaultPipelineContext("scene-1", dimensions, null);
        context.setConvertedInput(dimensions);
        List<LimitRule> candidates = new ArrayList<LimitRule>();
        candidates.add(rule);
        context.getStorage().put(CommonLimitCandidateSelector.STORAGE_KEY_CANDIDATES, candidates);
        return context;
    }

    /**
     * 构建执行器（查找函数记录收到的策略 Bean 名）.
     */
    private static CommonLimitRuleExecutor executor(
            final FixedActualStrategy strategy, final List<String> lookupNames) {
        return new CommonLimitRuleExecutor(new Function<String, LimitStrategy>() {
            @Override
            public LimitStrategy apply(String name) {

                lookupNames.add(name);
                return strategy;
            }
        });
    }

    /**
     * 执行单规则并取第一条结果.
     */
    private static LimitRuleResult run(CommonLimitRuleExecutor executor, PipelineContext context) {
        executor.accept(context);
        Object results = context.getStorage().get(CommonLimitRuleExecutor.STORAGE_KEY_RULE_RESULTS);
        return ((List<LimitRuleResult>) results).get(0);
    }

    @Test
    public void testUserHasPriorityOverOrgAndGlobal() {
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>();
        values.add(value(LimitRuleValue.DIMENSION_GLOBAL, null, null, "5000000", null, true));
        values.add(value(LimitRuleValue.DIMENSION_ORG, "orgId", "1001", "3000000", null, true));
        values.add(value(LimitRuleValue.DIMENSION_USER, "userId", "88", "1000000", null, true));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        dimensions.put("orgId", "1001");
        dimensions.put("userId", "88");
        FixedActualStrategy strategy = new FixedActualStrategy(new BigDecimal("900000"));
        LimitRuleResult result = run(executor(strategy, new ArrayList<String>()), context(dimensions, rule(values)));
        // USER 精确命中：用个人阈值 1000000.
        assertEquals(new BigDecimal("1000000"), result.getMatchedValue().getThresholdValue());
        assertEquals(LimitRuleResult.STATUS_PASS, result.getStatus());
    }

    @Test
    public void testOrgMatchedWhenUserNotHit() {
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>();
        values.add(value(LimitRuleValue.DIMENSION_USER, "userId", "99", "1000000", null, true));
        values.add(value(LimitRuleValue.DIMENSION_ORG, "orgId", "1001", "3000000", null, true));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        dimensions.put("userId", "88");
        dimensions.put("orgId", "1001");
        FixedActualStrategy strategy = new FixedActualStrategy(new BigDecimal("3200000"));
        LimitRuleResult result = run(executor(strategy, new ArrayList<String>()), context(dimensions, rule(values)));
        // USER 未命中（88 != 99）：ORG 命中 3000000，实际 3200000 超限.
        assertEquals(new BigDecimal("3000000"), result.getMatchedValue().getThresholdValue());
        assertEquals(LimitRuleResult.STATUS_FAIL, result.getStatus());
    }

    @Test
    public void testGlobalFallbackWhenNothingHit() {
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>();
        values.add(value(LimitRuleValue.DIMENSION_ORG, "orgId", "1001", "3000000", null, true));
        values.add(value(LimitRuleValue.DIMENSION_GLOBAL, null, null, "5000000", null, true));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        dimensions.put("orgId", "9999");
        FixedActualStrategy strategy = new FixedActualStrategy(new BigDecimal("4000000"));
        LimitRuleResult result = run(executor(strategy, new ArrayList<String>()), context(dimensions, rule(values)));
        // 精确维度都未命中：GLOBAL 兜底 5000000.
        assertEquals(new BigDecimal("5000000"), result.getMatchedValue().getThresholdValue());
        assertEquals(LimitRuleResult.STATUS_PASS, result.getStatus());
    }

    @Test
    public void testNoMatchReturnsNotConfigured() {
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>();
        values.add(value(LimitRuleValue.DIMENSION_USER, "userId", "99", "1000000", null, true));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        dimensions.put("orgId", "1001");
        FixedActualStrategy strategy = new FixedActualStrategy(new BigDecimal("100"));
        LimitRuleResult result = run(executor(strategy, new ArrayList<String>()), context(dimensions, rule(values)));
        // 三级都未命中：NOT_CONFIGURED + 默认话术.
        assertEquals(LimitRuleResult.STATUS_NOT_CONFIGURED, result.getStatus());
        assertEquals(CommonLimitRuleExecutor.DEFAULT_NOT_CONFIGURED_MESSAGE, result.getMessage());
    }

    @Test
    public void testDisabledValueSkipped() {
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>();
        values.add(value(LimitRuleValue.DIMENSION_USER, "userId", "88", "1000000", null, false));
        values.add(value(LimitRuleValue.DIMENSION_ORG, "orgId", "1001", "3000000", null, true));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        dimensions.put("userId", "88");
        dimensions.put("orgId", "1001");
        FixedActualStrategy strategy = new FixedActualStrategy(new BigDecimal("100"));
        LimitRuleResult result = run(executor(strategy, new ArrayList<String>()), context(dimensions, rule(values)));
        // 未启用的 USER 行跳过：命中 ORG.
        assertEquals(new BigDecimal("3000000"), result.getMatchedValue().getThresholdValue());
    }

    @Test
    public void testOperatorDecidesWhenStatusMissing() {
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>();
        values.add(value(LimitRuleValue.DIMENSION_ORG, "orgId", "1001", "1000000", "<=", true));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        dimensions.put("orgId", "1001");
        // 超阈值：FAIL.
        FixedActualStrategy over = new FixedActualStrategy(new BigDecimal("1200000"));
        LimitRuleResult overResult = run(executor(over, new ArrayList<String>()), context(dimensions, rule(values)));
        assertEquals(LimitRuleResult.STATUS_FAIL, overResult.getStatus());
        // 等于阈值：<= 判定为 PASS.
        FixedActualStrategy equal = new FixedActualStrategy(new BigDecimal("1000000"));
        LimitRuleResult equalResult = run(executor(equal, new ArrayList<String>()), context(dimensions, rule(values)));
        assertEquals(LimitRuleResult.STATUS_PASS, equalResult.getStatus());
    }

    @Test
    public void testBlankOperatorDefaultsToLte() {
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>();
        values.add(value(LimitRuleValue.DIMENSION_ORG, "orgId", "1001", "1000000", null, true));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        dimensions.put("orgId", "1001");
        FixedActualStrategy strategy = new FixedActualStrategy(new BigDecimal("1000000"));
        LimitRuleResult result = run(executor(strategy, new ArrayList<String>()), context(dimensions, rule(values)));
        // 运算符为空：按默认 <= 判定（等于阈值通过）.
        assertEquals(LimitRuleResult.STATUS_PASS, result.getStatus());
    }

    @Test
    public void testStrategyStatusWinsOverOperator() {
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>();
        values.add(value(LimitRuleValue.DIMENSION_ORG, "orgId", "1001", "1000000", "<=", true));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        dimensions.put("orgId", "1001");
        final LimitRuleResult decided = new LimitRuleResult();
        decided.setStatus(LimitRuleResult.STATUS_PASS);
        decided.setActualValue(new BigDecimal("1200000"));
        final LimitStrategy decidedStrategy = new LimitStrategy() {
            @Override
            public LimitRuleResult evaluate(PipelineContext context, LimitRule rule, LimitRuleValue value) {

                return decided;
            }
        };
        CommonLimitRuleExecutor executor = new CommonLimitRuleExecutor(
                new Function<String, LimitStrategy>() {
            @Override
            public LimitStrategy apply(String name) {

                return decidedStrategy;
            }
        });
        LimitRuleResult result = run(executor, context(dimensions, rule(values)));
        // 策略已给状态：即使实际值超阈值也不改判.
        assertEquals(LimitRuleResult.STATUS_PASS, result.getStatus());
    }

    @Test
    public void testUnknownOperatorThrows() {
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>();
        values.add(value(LimitRuleValue.DIMENSION_ORG, "orgId", "1001", "1000000", "!=", true));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        dimensions.put("orgId", "1001");
        FixedActualStrategy strategy = new FixedActualStrategy(new BigDecimal("100"));
        try {
            run(executor(strategy, new ArrayList<String>()), context(dimensions, rule(values)));
            fail("Expected IllegalArgumentException for unknown operator. ");
        }
        catch (IllegalArgumentException ignore) { }
    }

    @Test
    public void testResolveStrategyByBeanName() {
        List<LimitRuleValue> values = new ArrayList<LimitRuleValue>();
        values.add(value(LimitRuleValue.DIMENSION_GLOBAL, null, null, "1000000", null, true));
        Map<String, Object> dimensions = new HashMap<String, Object>();
        FixedActualStrategy strategy = new FixedActualStrategy(new BigDecimal("100"));
        List<String> lookupNames = new ArrayList<String>();
        LimitRule limitRule = rule(values);
        limitRule.setStrategyBean("readyStockValueStrategy");
        run(executor(strategy, lookupNames), context(dimensions, limitRule));
        // 策略按 Bean 名经查找函数路由.
        assertEquals(1, lookupNames.size());
        assertEquals("readyStockValueStrategy", lookupNames.get(0));
    }

}
