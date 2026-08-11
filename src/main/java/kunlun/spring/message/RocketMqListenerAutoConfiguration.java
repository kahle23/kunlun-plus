/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.spring.message;

import cn.hutool.core.collection.CollUtil;
import kunlun.action.ActionUtil;
import kunlun.action.message.support.rocketmq.RocketMqListener;
import kunlun.action.message.support.rocketmq.RocketMqMessageHandler;
import kunlun.exception.ExceptionUtil;
import kunlun.message.model.Subscribe;
import kunlun.util.MapUtil;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

import static kunlun.spring.message.RocketMqProperties.HandlerConfig;

@Configuration
@ConditionalOnClass({MessageListener.class})
@EnableConfigurationProperties({RocketMqProperties.class})
@ConditionalOnProperty(name = "kunlun.message.rocketmq.enabled", havingValue = "true")
public class RocketMqListenerAutoConfiguration implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(RocketMqListenerAutoConfiguration.class);

    @Resource
    private RocketMqProperties rocketMqProperties;
    @Resource
    private ApplicationContext appContext;

    @Override
    public void afterPropertiesSet() {
        Map<String, RocketMqListener> beansOfType = appContext.getBeansOfType(RocketMqListener.class);
        Map<String, Subscribe> listenerMap = new LinkedHashMap<String, Subscribe>();
        if (MapUtil.isNotEmpty(listenerMap)) {
            for (RocketMqListener listener : beansOfType.values()) {
                if (listener == null) { continue; }
                Subscribe subscribe = Subscribe.Builder.of(listener.getTopic())
                        .setMessageListener(listener)
                        .build();
                String key = String.format("%s-%s-%s-%s", listener.getNameServerAddress()
                        , listener.getConsumerGroup(), listener.getTopic(), listener.getSubExpression());
                listenerMap.put(key, subscribe);
            }
        }
        //
        Map<String, HandlerConfig> configs = rocketMqProperties.getConfigs();
        if (CollUtil.isEmpty(configs)) { return; }
        for (Map.Entry<String, HandlerConfig> entry : configs.entrySet()) {
            HandlerConfig config = entry.getValue();
            String name = entry.getKey();
            String key = String.format("%s-%s-%s-%s", config.getNameServerAddress()
                    , config.getConsumerGroup(), config.getTopic(), config.getSubExpression());
            try {
                DefaultMQProducer mqProducer = new DefaultMQProducer(config.getProducerGroup());
                mqProducer.setNamesrvAddr(config.getNameServerAddress());
                mqProducer.start();
                RocketMqMessageHandler messageHandler = new RocketMqMessageHandler(mqProducer);
                ActionUtil.registerAction(name, messageHandler);
                //
                Subscribe subscribe = listenerMap.get(key);
                if (subscribe != null) {
                    messageHandler.subscribe(subscribe);
                }
            } catch (Exception e) { throw ExceptionUtil.wrap(e); }
        }
    }

}
