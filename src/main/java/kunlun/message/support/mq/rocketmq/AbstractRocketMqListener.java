/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.message.support.mq.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

public abstract class AbstractRocketMqListener implements RocketMqListener {
    private String nameServerAddress;
    private String consumerGroup;
    private String topic;
    private String subExpression;

    public AbstractRocketMqListener(String nameSrvAddress, String consumerGroup, String topic, String subExpression) {
        this.nameServerAddress = nameSrvAddress;
        this.consumerGroup = consumerGroup;
        this.topic = topic;
        this.subExpression = subExpression;
    }

    public AbstractRocketMqListener(String nameSrvAddress, String consumerGroup) {
        this();
        this.nameServerAddress = nameSrvAddress;
        this.consumerGroup = consumerGroup;
    }

    public AbstractRocketMqListener() {

        this.subExpression = "*";
    }

    @Override
    public String getNameServerAddress() {

        return nameServerAddress;
    }

    @Override
    public String getConsumerGroup() {

        return consumerGroup;
    }

    @Override
    public String getTopic() {

        return topic;
    }

    @Override
    public String getSubExpression() {

        return subExpression;
    }

    @Override
    public void preProcess(DefaultMQPushConsumer mqConsumer) {

    }

}
