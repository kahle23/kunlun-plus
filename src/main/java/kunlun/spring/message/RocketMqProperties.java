package kunlun.spring.message;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("kunlun.message.rocketmq")
public class RocketMqProperties {
    private Boolean enabled;
    private Map<String, HandlerConfig> configs;

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public Map<String, HandlerConfig> getConfigs() {

        return configs;
    }

    public void setConfigs(Map<String, HandlerConfig> configs) {

        this.configs = configs;
    }

    public static class HandlerConfig {
        /**
         * 名称服务器地址.
         */
        private String nameServerAddress;
        /**
         * 生产者组.
         */
        private String producerGroup;
        /**
         * 消费者组.
         */
        private String consumerGroup;
        /**
         * 主题.
         */
        private String topic;
        /**
         * 订阅的表达式.
         */
        private String subExpression;

        public String getNameServerAddress() {
            return nameServerAddress;
        }

        public void setNameServerAddress(String nameServerAddress) {
            this.nameServerAddress = nameServerAddress;
        }

        public String getProducerGroup() {
            return producerGroup;
        }

        public void setProducerGroup(String producerGroup) {
            this.producerGroup = producerGroup;
        }

        public String getConsumerGroup() {
            return consumerGroup;
        }

        public void setConsumerGroup(String consumerGroup) {
            this.consumerGroup = consumerGroup;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getSubExpression() {
            return subExpression;
        }

        public void setSubExpression(String subExpression) {
            this.subExpression = subExpression;
        }
    }

}
