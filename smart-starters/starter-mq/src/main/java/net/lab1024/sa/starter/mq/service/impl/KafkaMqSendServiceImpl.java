package net.lab1024.sa.starter.mq.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.starter.mq.service.MqSendService;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Kafka 实现
 */
@Slf4j
public class KafkaMqSendServiceImpl implements MqSendService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMqSendServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        log.info("[SmartStarter-MQ] 使用 Kafka");
    }

    @Override
    public void send(String destination, String message) {
        kafkaTemplate.send(destination, message);
        log.debug("[Kafka] send -> topic={}", destination);
    }

    @Override
    public void sendDelay(String destination, String message, long delayMillis) {
        // Kafka 原生不支持延迟消息，可通过时间轮或外部调度实现
        throw new UnsupportedOperationException("Kafka 不支持原生延迟消息，请使用 RabbitMQ 或引入延迟方案");
    }
}
