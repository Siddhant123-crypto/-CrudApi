package com.Siddhant.UserApp.kafka;
import com.Siddhant.UserApp.Config.KafkaTopicConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service
public class OrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    public OrderProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendOrder(String message) {
        kafkaTemplate.send(KafkaTopicConfig.ORDER_TOPIC, message);
    }
}