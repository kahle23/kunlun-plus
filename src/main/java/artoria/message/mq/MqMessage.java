package artoria.message.mq;

@Deprecated
public interface MqMessage {

    String getDestination();

    Object getPayload();

}
