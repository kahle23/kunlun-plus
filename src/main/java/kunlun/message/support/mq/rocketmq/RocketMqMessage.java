/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.message.support.mq.rocketmq;

import kunlun.data.json.JsonUtils;
import kunlun.exception.ExceptionUtils;
import kunlun.io.util.IOUtils;
import kunlun.message.support.mq.MqMessage;
import kunlun.util.Assert;
import kunlun.util.ClassUtils;
import kunlun.util.CloseUtils;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;

import static kunlun.common.constant.Charsets.UTF_8;

public class RocketMqMessage implements MqMessage {

    public static RocketMqMessage of(String topic, Object body) {

        return new RocketMqMessage().setTopic(topic).setBody(body);
    }

    public static RocketMqMessage of(String topic) {

        return new RocketMqMessage().setTopic(topic);
    }

    public static RocketMqMessage of() {

        return new RocketMqMessage();
    }

    private Collection<String> keys;
    private Integer flag;
    private String topic;
    private String tags;
    private byte[] body;

    public Collection<String> getKeys() {

        return keys;
    }

    public RocketMqMessage setKeys(Collection<String> keys) {
        this.keys = keys;
        return this;
    }

    public Integer getFlag() {

        return flag;
    }

    public RocketMqMessage setFlag(Integer flag) {
        this.flag = flag;
        return this;
    }

    public String getTopic() {

        return topic;
    }

    public RocketMqMessage setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getTags() {

        return tags;
    }

    public RocketMqMessage setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public byte[] getBody() {

        return body;
    }

    public RocketMqMessage setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public RocketMqMessage setBody(Object body) {

        return setBody(body, UTF_8);
    }

    public RocketMqMessage setBody(Object body, Charset charset) {
        Assert.notNull(charset, "Parameter \"charset\" must not null. ");
        Assert.notNull(body, "Parameter \"body\" must not null. ");
        if (body instanceof byte[]) { return setBody((byte[]) body); }
        if (ClassUtils.isSimpleValueType(body.getClass())) {
            String valueOf = String.valueOf(body);
            return setBody(valueOf.getBytes(charset));
        }
        if (body instanceof InputStream) {
            try {
                return setBody(IOUtils.toByteArray((InputStream) body));
            }
            catch (Exception e) { throw ExceptionUtils.wrap(e); }
            finally { CloseUtils.closeQuietly((InputStream) body); }
        }
        if (body instanceof Reader) {
            try {
                return setBody(IOUtils.toByteArray((Reader) body, charset.name()));
            }
            catch (Exception e) { throw ExceptionUtils.wrap(e); }
            finally { CloseUtils.closeQuietly((Reader) body); }
        }
        //
        String jsonString = JsonUtils.toJsonString(body);
        return setBody(jsonString.getBytes(charset));
    }

    @Override
    public String getDestination() {

        return topic;
    }

    @Override
    public byte[] getPayload() {

        return body;
    }

}
