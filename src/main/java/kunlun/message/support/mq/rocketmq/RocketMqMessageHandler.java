/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.message.support.mq.rocketmq;

import kunlun.exception.ExceptionUtils;
import kunlun.message.support.AbstractClassicMessageHandler;
import kunlun.util.Assert;
import kunlun.util.CollectionUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RocketMqMessageHandler extends AbstractClassicMessageHandler {
    private final DefaultMQProducer mqProducer;

    public RocketMqMessageHandler(DefaultMQProducer mqProducer) {

        this.mqProducer = mqProducer;
    }

    protected Message convert(Object input) {
        if (input instanceof Message) {
            return (Message) input;
        }
        else if (input instanceof RocketMqMessage) {
            RocketMqMessage mqMessage = (RocketMqMessage) input;
            Collection<String> keys = mqMessage.getKeys();
            String topic = mqMessage.getTopic();
            String tags = mqMessage.getTags();
            Integer flag = mqMessage.getFlag();
            byte[] body = mqMessage.getBody();
            Message result = new Message(topic, tags, null, body);
            if (CollectionUtils.isNotEmpty(keys)) {
                result.setKeys(keys);
            }
            if (flag != null) {
                result.setFlag(flag);
            }
            return result;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    protected Object result(SendResult sendResult, Class<?> clazz) {
        if (SendResult.class.equals(clazz)
                || Object.class.equals(clazz)) {
            return sendResult;
        }
        else if (String.class.equals(clazz)) {
            return sendResult.getMsgId();
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    protected Object send(Object input, Class<?> clazz) {
        Assert.notNull(input, "Parameter \"input\" must not null. ");
        Assert.notNull(clazz, "Parameter \"clazz\" must not null. ");
        try {
            if (input instanceof List) {
                return batchSend((List<?>) input, clazz);
            }
            Message mqMessage = convert(input);
            SendResult sendResult = mqProducer.send(mqMessage);
            return result(sendResult, clazz);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    protected Object batchSend(List<?> messages, Class<?> clazz) {
        Assert.notEmpty(messages, "Parameter \"messages\" must not empty. ");
        Assert.notNull(clazz, "Parameter \"clazz\" must not null. ");
        try {
            List<Message> messageList = new ArrayList<Message>();
            for (Object message : messages) {
                messageList.add(convert(message));
            }
            SendResult sendResult = mqProducer.send(messageList);
            return result(sendResult, clazz);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public Object operate(Object input, String name, Class<?> clazz) {
        if ("send".equals(name)) {
            return send(input, clazz);
        }
        else if ("batchSend".equals(name)) {
            Assert.isInstanceOf(List.class, input
                    , "Parameter \"input\" must instance of list. ");
            return batchSend((List<?>) input, clazz);
        }
        else {
            throw new UnsupportedOperationException(
                    "Unsupported operation name \"" + name + "\"! "
            );
        }
    }

}
