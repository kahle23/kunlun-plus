/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

/**
 * 限制引擎家族包（baibao 侧）：限制领域的词汇与通用实现.
 * <p>
 * 执行骨架在 kunlun（{@code kunlun.engine.pipeline.limit.AbstractLimitEngine}，
 * 管道家族与基座见 {@code kunlun.engine.pipeline}）；本包承载限制领域的共享词汇：
 * 三层概念（场景壳 → 规则 → 维度阈值）的数据形态在 {@code model} 子包，
 * 通用求值语义（启停与优先级筛选、USER&gt;ORG&gt;GLOBAL 维度匹配、AND/OR 组合、
 * BLOCK/ALLOW/TIP 效果、提示语占位符渲染）在 {@code support} 子包.
 * <p>
 * 组成：
 * <ul>
 *   <li>{@code LimitStrategy}：策略契约——一条限制规则"怎么算"的载体
 *   （读业务输入算实际值，直接判定或留给框架按运算符判定）；</li>
 *   <li>{@code model}：实体——{@code LimitSceneConfig}（场景配置，
 *   落位管道配置槽位）、{@code LimitRule}（规则）、{@code LimitRuleValue}
 *   （维度阈值行）、{@code LimitRuleResult}（单规则结果）、
 *   {@code LimitResult}（整体结果），均纯字段 POJO，无持久化痕迹；</li>
 *   <li>{@code support}：通用阶段实现——{@code CommonLimitCandidateSelector}
 *   （启停过滤 + 优先级升序）、{@code CommonLimitRuleExecutor}
 *   （维度匹配 + 策略路由 + 运算符判定）、{@code CommonLimitResultAggregator}
 *   （AND/OR 组合 + BLOCK/ALLOW/TIP 效果 + 提示语渲染），
 *   各自的存储键与覆写点见类内说明.</li>
 * </ul>
 * 业务项目接入：继承 kunlun 的 {@code AbstractLimitEngine}，
 * 实现四个 getXxx 口子（配置装载、候选筛选、核心求值、结果聚合）——
 * 后三者直接装配本包通用实现即可，配置装载（查库 + 缓存）与业务策略（实现
 * {@code LimitStrategy}）由业务项目自己提供；
 * 限制校验是 check 类语义：超限以结果对象表达、不以异常表达.
 */
package baibao.engine.pipeline.limit;
