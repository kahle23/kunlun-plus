/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.engine.pipeline.limit;

import kunlun.engine.pipeline.PipelineContext;
import baibao.engine.pipeline.limit.model.LimitRule;
import baibao.engine.pipeline.limit.model.LimitRuleResult;
import baibao.engine.pipeline.limit.model.LimitRuleValue;

/**
 * 限制策略的契约：一条限制规则"怎么算"的载体.
 * <p>
 * 策略是纯计算角色：读业务输入（约定从 {@code context.getConvertedInput()} 取，
 * 如携带维度与单据数据的 Map），结合命中维度的阈值行算出实际值并给出判定；
 * 查库取数等副作用由实现方自理（实现方通常注入自己的数据服务）.
 * <p>
 * 判定的两种给法（{@code support} 子包通用核心求值阶段的消费约定）：
 * <ul>
 *     <li>直接判定：结果的状态（PASS/FAIL）由策略给出；</li>
 *     <li>只算实际值：结果的 actualValue 给出、状态留空，
 *     由核心求值阶段按命中阈值行的运算符（thresholdOperator，默认 {@code <=}）代为判定.</li>
 * </ul>
 * 策略按名经查找函数路由（{@code strategy_bean}，如 readyStockValueStrategy），
 * 实现应为无状态（每次调用仅依赖入参），同名多实现由实现方的容器裁决.
 *
 * @author Kahle
 */
public interface LimitStrategy {

    /**
     * 对单条规则求值：算实际值并给出（或留给框架判定）通过与否.
     *
     * @param context 管道引擎上下文（业务输入从 convertedInput 取）
     * @param rule 本次求值的限制规则
     * @param value 命中当前维度的阈值行（含阈值数值与运算符）
     * @return 单规则求值结果（不可为 null）
     */
    LimitRuleResult evaluate(PipelineContext context, LimitRule rule, LimitRuleValue value);

}
