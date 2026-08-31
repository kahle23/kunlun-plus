/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package baibao.engine.pipeline.limit.model;

import kunlun.engine.pipeline.base.AbstractPipelineConfig;

import java.util.List;

/**
 * 限制场景配置：场景壳 + 挂在其下的规则集合.
 * <p>
 * 场景壳只登记"在哪个业务钩子点触发"（场景码即钩子点标识），
 * {@code enabled} 是场景级总开关（停用即本次不做任何限制）；
 * 继承 {@link AbstractPipelineConfig} 而落位管道的配置槽位，
 * 基座的校验/处理阶段（入参校验、预/后处理脚本、渲染器名）随之可用.
 * <p>
 * 纯字段载体，无持久化痕迹；下游按场景码查库（通常带缓存）装配为本类.
 *
 * @author Kahle
 */
public class LimitSceneConfig extends AbstractPipelineConfig {

    /**
     * 是否启用（场景级总开关，默认启用）.
     */
    private boolean enabled = true;
    /**
     * 场景下的规则集合.
     */
    private List<LimitRule> rules;

    public boolean isEnabled() {

        return enabled;
    }

    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    public List<LimitRule> getRules() {

        return rules;
    }

    public void setRules(List<LimitRule> rules) {

        this.rules = rules;
    }

}
