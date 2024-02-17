/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.message.support.mq.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;

public interface RocketMqListener extends MessageListener {

    String getNameServerAddress();

    String getConsumerGroup();

    String getTopic();

    String getSubExpression();

    void preProcess(DefaultMQPushConsumer mqConsumer);

}
