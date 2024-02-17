/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.message.support.mq.rocketmq;

import kunlun.exception.ExceptionUtils;
import kunlun.util.MapUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ConditionalOnClass(MessageListener.class)
public class RocketMqListenerAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(RocketMqListenerAutoConfiguration.class);
    private final Map<RocketMqListener, DefaultMQPushConsumer> map = new ConcurrentHashMap<RocketMqListener, DefaultMQPushConsumer>();

    public RocketMqListenerAutoConfiguration(ApplicationContext appContext) {
        Map<String, RocketMqListener> listenerMap = appContext.getBeansOfType(RocketMqListener.class);
        if (MapUtils.isEmpty(listenerMap)) { return; }
        for (RocketMqListener mqListener : listenerMap.values()) {
            if (mqListener == null) { continue; }
            try {
                DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer(mqListener.getConsumerGroup());
                mqPushConsumer.setNamesrvAddr(mqListener.getNameServerAddress());
                String subExpression = mqListener.getSubExpression();
                String topic = mqListener.getTopic();
                mqPushConsumer.subscribe(topic, subExpression);

                if (mqListener instanceof MessageListenerConcurrently) {
                    mqPushConsumer.registerMessageListener((MessageListenerConcurrently) mqListener);
                }
                else if (mqListener instanceof MessageListenerOrderly) {
                    mqPushConsumer.registerMessageListener((MessageListenerOrderly) mqListener);
                }
                else {
                    throw new UnsupportedOperationException();
                }
                mqListener.preProcess(mqPushConsumer);
                mqPushConsumer.start();
                log.info("RocketMQ subscribe {} {} by {} success. ", topic, subExpression, mqListener.getClass().getName());
                map.put(mqListener, mqPushConsumer);
            }
            catch (Exception e) {
                throw ExceptionUtils.wrap(e);
            }
        }
    }

    public Collection<DefaultMQPushConsumer> getMqConsumers() {

        return Collections.unmodifiableCollection(map.values());
    }

}
