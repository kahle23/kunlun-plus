/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.message.support.delay;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * The simple delay message.
 * @author Kahle
 */
public class DelayMessage implements Serializable {
    private String topic;
    private Object body;
    private Long     delayTime;
    private TimeUnit timeUnit;
    private Long     timestamp;

    public DelayMessage(String topic, Object body, Long delayTime, TimeUnit timeUnit) {
        this.delayTime = delayTime;
        this.timeUnit = timeUnit;
        this.topic = topic;
        this.body = body;
    }

    public DelayMessage() {

    }

    public String getTopic() {

        return topic;
    }

    public void setTopic(String topic) {

        this.topic = topic;
    }

    public Object getBody() {

        return body;
    }

    public void setBody(Object body) {

        this.body = body;
    }

    public Long getDelayTime() {

        return delayTime;
    }

    public void setDelayTime(Long delayTime) {

        this.delayTime = delayTime;
    }

    public TimeUnit getTimeUnit() {

        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {

        this.timeUnit = timeUnit;
    }

    public Long getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(Long timestamp) {

        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DelayMessage{" +
                "topic='" + topic + '\'' +
                ", body=" + body +
                ", delayTime=" + delayTime +
                ", timeUnit=" + timeUnit +
                ", timestamp=" + timestamp +
                '}';
    }

}
