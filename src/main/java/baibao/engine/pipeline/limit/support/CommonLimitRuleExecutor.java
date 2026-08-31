/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.engine.pipeline.limit.support;

import kunlun.core.function.Consumer;
import kunlun.core.function.Function;
import kunlun.engine.pipeline.PipelineContext;
import kunlun.util.Assert;
import kunlun.util.StrUtil;
import baibao.engine.pipeline.limit.LimitStrategy;
import baibao.engine.pipeline.limit.model.LimitRule;
import baibao.engine.pipeline.limit.model.LimitRuleResult;
import baibao.engine.pipeline.limit.model.LimitRuleValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 通用核心求值阶段：对候选规则逐条"维度匹配 → 策略求值 → 判定".
 * <p>
 * 逐条语义（单规则失败按 check 类语义记入结果，不抛异常、不短路——全评全留痕）：
 * <ul>
 *     <li>维度匹配（{@link #matchValue}，可覆写）：USER 精确 &gt; ORG 精确 &gt; GLOBAL 兜底；
 *     精确行以维度键从上下文维度表取值、与配置的维度值按字符串相等比较，
 *     未启用的阈值行跳过，CUSTOM 维度预留（默认不参与匹配）；</li>
 *     <li>未命中任何阈值行 → NOT_CONFIGURED 结果（默认话术 {@link #getNotConfiguredMessage()} 可覆写），
 *     放行与否由聚合阶段按场景属性裁决；</li>
 *     <li>策略路由（{@link #resolveStrategy}，可覆写）：默认按规则的策略 Bean 名
 *     经构造注入的查找函数取策略（SCRIPT 策略二期由下游覆写或注册脚本策略）；</li>
 *     <li>判定：策略给出了状态即用；只给出实际值（actualValue）时，
 *     由 {@link #decideByOperator} 按命中阈值行的运算符代判（默认 {@code <=}，
 *     未知运算符抛 IllegalArgumentException 让配置错误在执行期暴露）.</li>
 * </ul>
 * 上下文维度表取法（{@link #getDimensions}，可覆写）：默认约定转换后的输入即维度表
 * （如 {@code {orgId:1001, userId:88}}），非 Map 输入请覆写本方法自行取维度.
 * 逐条结果写入上下文 storage（键 {@link #STORAGE_KEY_RULE_RESULTS}），
 * 供结果聚合阶段消费与执行留痕读取.
 *
 * @author Kahle
 */
public class CommonLimitRuleExecutor implements Consumer<PipelineContext> {

    /**
     * 存储键：逐规则求值结果列表（本阶段写入，结果聚合阶段与留痕读取）.
     */
    public static final String STORAGE_KEY_RULE_RESULTS = "limit.rule-results";
    /**
     * 未配置（三级维度都未命中）时的默认话术.
     */
    public static final String DEFAULT_NOT_CONFIGURED_MESSAGE = "未配置限制值，请联系管理员配置！";
    /**
     * 策略查找函数：按策略 Bean 名取策略（下游用容器查找或自备注册表适配）.
     */
    private final Function<String, LimitStrategy> strategyLookup;

    /**
     * 以策略查找函数构建通用核心求值阶段.
     *
     * @param strategyLookup 策略查找函数（按名取策略，不可为 null）
     */
    public CommonLimitRuleExecutor(Function<String, LimitStrategy> strategyLookup) {

        this.strategyLookup = Assert.notNull(strategyLookup, "Parameter \"strategyLookup\" must not null. ");
    }

    @Override
    public void accept(PipelineContext context) {
        List<LimitRuleResult> results = new ArrayList<LimitRuleResult>();
        Object candidates = context.getStorage().get(CommonLimitCandidateSelector.STORAGE_KEY_CANDIDATES);
        if (candidates instanceof List) {
            for (Object candidate : (List<?>) candidates) {
                results.add(evaluateRule(context, (LimitRule) candidate));
            }
        }
        context.getStorage().put(STORAGE_KEY_RULE_RESULTS, results);
    }

    /**
     * 对单条规则求值：维度匹配 → 策略求值 → 判定（含快照归属回填）.
     *
     * @param context 管道引擎上下文
     * @param rule 候选规则
     * @return 单规则求值结果
     */
    protected LimitRuleResult evaluateRule(PipelineContext context, LimitRule rule) {
        LimitRuleValue matchedValue = matchValue(rule.getValues(), getDimensions(context));
        if (matchedValue == null) {
            // 三级维度都未命中：NOT_CONFIGURED，放行与否由聚合阶段裁决.
            LimitRuleResult result = new LimitRuleResult();
            result.setRule(rule);
            result.setStatus(LimitRuleResult.STATUS_NOT_CONFIGURED);
            result.setMessage(getNotConfiguredMessage());
            return result;
        }
        LimitStrategy strategy = resolveStrategy(rule);
        LimitRuleResult result = strategy.evaluate(context, rule, matchedValue);
        Assert.state(result != null, "The limit strategy must return a result. ");
        // 快照归属回填：策略只管算，规则与命中阈值行由本阶段兜底.
        result.setRule(rule);
        result.setMatchedValue(matchedValue);
        if (result.getStatus() == null) {
            // 策略只算了实际值：按命中阈值行的运算符代判.
            decideByOperator(result);
        }
        return result;
    }

    /**
     * 取上下文的维度表（维度匹配的数据来源）.
     * <p>
     * 默认约定：转换后的输入即维度表（如 {@code {orgId:1001, userId:88}}）；
     * 输入是别的载体（专用上下文对象等）时覆写本方法自行取维度.
     *
     * @param context 管道引擎上下文
     * @return 维度表（无维度时为空表）
     */
    protected Map<String, Object> getDimensions(PipelineContext context) {
        Object input = context.getConvertedInput();
        if (input instanceof Map) { return (Map<String, Object>) input; }
        // 非约定的 Map 输入又未覆写本方法：视为无维度（只剩 GLOBAL 兜底可命中）.
        return Collections.emptyMap();
    }

    /**
     * 维度匹配：为规则挑选命中当前上下文的阈值行.
     * <p>
     * 取值优先级：USER 精确 &gt; ORG 精确 &gt; GLOBAL 兜底；
     * CUSTOM 维度预留，默认不参与匹配（有此需要时覆写本方法）.
     *
     * @param values 规则的阈值行集合（可为 null）
     * @param dimensions 上下文维度表
     * @return 命中的阈值行（未命中为 null）
     */
    protected LimitRuleValue matchValue(List<LimitRuleValue> values, Map<String, Object> dimensions) {
        if (values == null) { return null; }
        LimitRuleValue matched = matchByType(values, dimensions, LimitRuleValue.DIMENSION_USER);
        if (matched == null) { matched = matchByType(values, dimensions, LimitRuleValue.DIMENSION_ORG); }
        if (matched == null) { matched = matchByType(values, dimensions, LimitRuleValue.DIMENSION_GLOBAL); }
        return matched;
    }

    /**
     * 按单一维度类型挑选第一行命中的阈值行（未启用的跳过）.
     *
     * @param values 规则的阈值行集合
     * @param dimensions 上下文维度表
     * @param dimensionType 维度类型（USER / ORG / GLOBAL）
     * @return 命中的阈值行（未命中为 null）
     */
    protected LimitRuleValue matchByType(List<LimitRuleValue> values
            , Map<String, Object> dimensions, String dimensionType) {
        for (LimitRuleValue value : values) {
            if (value == null || !value.isEnabled()) { continue; }
            if (!dimensionType.equals(value.getDimensionType())) { continue; }
            // GLOBAL 兜底行键值为空，直接命中.
            if (LimitRuleValue.DIMENSION_GLOBAL.equals(dimensionType)) { return value; }
            // 精确行：维度键从上下文取值，与配置的维度值按字符串相等比较.
            Object contextValue = dimensions.get(value.getDimensionKey());
            if (contextValue == null || value.getDimensionValue() == null) { continue; }
            if (String.valueOf(contextValue).equals(value.getDimensionValue())) { return value; }
        }
        return null;
    }

    /**
     * 策略路由：按规则取要执行的策略.
     * <p>
     * 默认按规则的策略 Bean 名经查找函数取；SCRIPT 策略二期启用时
     * 覆写本方法（或由查找函数路由到脚本策略）.
     *
     * @param rule 候选规则
     * @return 要执行的策略
     */
    protected LimitStrategy resolveStrategy(LimitRule rule) {
        String strategyBean = rule.getStrategyBean();
        Assert.notBlank(strategyBean, "Parameter \"strategyBean\" must not blank. ");
        LimitStrategy strategy = strategyLookup.apply(strategyBean);
        Assert.state(strategy != null,
                "Cannot resolve the limit strategy \"" + strategyBean + "\". ");
        return strategy;
    }

    /**
     * 按命中阈值行的运算符代判（策略只给出了实际值时）.
     * <p>
     * 运算符为空按默认 {@code <=}；实际值或阈值缺失属策略/配置错误，抛出 IllegalStateException.
     *
     * @param result 单规则求值结果（含实际值与命中阈值行）
     */
    protected void decideByOperator(LimitRuleResult result) {
        Number actualValue = result.getActualValue();
        LimitRuleValue matchedValue = result.getMatchedValue();
        if (actualValue == null || matchedValue == null || matchedValue.getThresholdValue() == null) {
            throw new IllegalStateException(
                    "The strategy must set the status or the actualValue with threshold. ");
        }
        BigDecimal actual = toBigDecimal(actualValue);
        BigDecimal threshold = matchedValue.getThresholdValue();
        int compared = actual.compareTo(threshold);
        String operator = matchedValue.getThresholdOperator();
        boolean passed;
        if (StrUtil.isBlank(operator) || LimitRuleValue.OPERATOR_LTE.equals(operator)) {
            passed = compared <= 0;
        }
        else if (LimitRuleValue.OPERATOR_LT.equals(operator)) { passed = compared < 0; }
        else if (LimitRuleValue.OPERATOR_GTE.equals(operator)) { passed = compared >= 0; }
        else if (LimitRuleValue.OPERATOR_GT.equals(operator)) { passed = compared > 0; }
        else if (LimitRuleValue.OPERATOR_EQ.equals(operator)) { passed = compared == 0; }
        else {
            throw new IllegalArgumentException(
                    "Unsupported threshold operator \"" + operator + "\". ");
        }
        result.setStatus(passed ? LimitRuleResult.STATUS_PASS : LimitRuleResult.STATUS_FAIL);
    }

    /**
     * 未配置（三级维度都未命中）时的提示话术.
     *
     * @return 提示话术
     */
    protected String getNotConfiguredMessage() {

        return DEFAULT_NOT_CONFIGURED_MESSAGE;
    }

    private BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal) { return (BigDecimal) number; }
        // 经字符串构造避免浮点数的二进制误差（如 0.1 的 double 表示）.
        return new BigDecimal(number.toString());
    }

}
