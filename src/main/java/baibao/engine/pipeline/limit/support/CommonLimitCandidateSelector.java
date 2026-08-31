/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.engine.pipeline.limit.support;

import kunlun.core.function.Consumer;
import kunlun.engine.pipeline.PipelineContext;
import baibao.engine.pipeline.limit.model.LimitRule;
import baibao.engine.pipeline.limit.model.LimitSceneConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 通用候选筛选阶段：从场景配置筛出本次参与的候选规则.
 * <p>
 * 筛选语义：场景启用（场景级总开关）且规则启用，按优先级升序稳定排序；
 * 筛选产物写入上下文 storage（键 {@link #STORAGE_KEY_CANDIDATES}），
 * 供核心求值阶段（{@link CommonLimitRuleExecutor}）消费.
 * <p>
 * 要求引擎配置为 {@link LimitSceneConfig} 类型（否则 IllegalStateException，
 * 同基座 CommonValidator 的防错语义）；筛选与排序拆在
 * {@link #selectCandidates(LimitSceneConfig)} 中，可覆写换筛选依据
 * （如按业务输入再过滤一层"仅备货采购生效"的规则）.
 *
 * @author Kahle
 */
public class CommonLimitCandidateSelector implements Consumer<PipelineContext> {

    /**
     * 存储键：候选规则列表（本阶段写入，核心求值阶段读取）.
     */
    public static final String STORAGE_KEY_CANDIDATES = "limit.candidates";

    @Override
    public void accept(PipelineContext context) {
        LimitSceneConfig config = requireConfig(context);
        context.getStorage().put(STORAGE_KEY_CANDIDATES, selectCandidates(config));
    }

    /**
     * 取引擎配置，并要求其为 {@link LimitSceneConfig} 类型.
     *
     * @param context 管道引擎上下文
     * @return 场景配置（已确保类型）
     * @throws IllegalStateException 引擎配置不是 LimitSceneConfig 类型时
     */
    protected LimitSceneConfig requireConfig(PipelineContext context) {
        Object config = context.getConfig();
        if (!(config instanceof LimitSceneConfig)) {
            throw new IllegalStateException(
                    "The CommonLimitCandidateSelector requires the config is LimitSceneConfig. ");
        }
        return (LimitSceneConfig) config;
    }

    /**
     * 从场景配置筛出候选规则：场景与规则均启用，按优先级升序.
     *
     * @param config 场景配置
     * @return 候选规则列表（无候选时为空列表）
     */
    protected List<LimitRule> selectCandidates(LimitSceneConfig config) {
        List<LimitRule> rules = config.getRules();
        List<LimitRule> candidates = new ArrayList<LimitRule>();
        if (config.isEnabled() && rules != null) {
            for (LimitRule rule : rules) {
                if (rule != null && rule.isEnabled()) { candidates.add(rule); }
            }
        }
        // 稳定排序：优先级相同时保持配置顺序.
        Collections.sort(candidates, new Comparator<LimitRule>() {
            @Override
            public int compare(LimitRule left, LimitRule right) {

                return left.getPriority() - right.getPriority();
            }
        });
        return candidates;
    }

}
