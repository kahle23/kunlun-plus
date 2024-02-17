/*
 * Copyright (c) 2018. the original author or authors.
 * Kunlun is licensed under the "LICENSE" file in the project's root directory.
 */

package kunlun.message.support.mq;

@Deprecated
public interface MqMessage {

    String getDestination();

    Object getPayload();

}
