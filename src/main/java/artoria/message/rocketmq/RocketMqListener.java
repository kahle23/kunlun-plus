package artoria.message.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;

public interface RocketMqListener extends MessageListener {

    String getNameServerAddress();

    String getConsumerGroup();

    String getTopic();

    String getSubExpression();

    void preProcess(DefaultMQPushConsumer mqConsumer);

}
