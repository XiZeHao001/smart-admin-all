package net.lab1024.sa.starter.mq.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.starter.mq.service.MqSendService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * RabbitMQ 实现
 */
@Slf4j
public class RabbitMqSendServiceImpl implements MqSendService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqSendServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        log.info("[SmartStarter-MQ] 使用 RabbitMQ");
    }

    @Override
    public void send(String destination, String message) {
        // destination 作为 routingKey，exchange 使用默认（""）
        rabbitTemplate.convertAndSend("", destination, message);
        log.debug("[RabbitMQ] send -> routingKey={}", destination);
    }

    @Override
    public void sendDelay(String destination, String message, long delayMillis) {
        // 需要 RabbitMQ 安装 rabbitmq_delayed_message_exchange 插件
        // TODO: 配置延迟交换机后取消注释
        // MessagePostProcessor processor = msg -> {
        //     msg.getMessageProperties().setDelay((int) delayMillis);
        //     return msg;
        // };
        // rabbitTemplate.convertAndSend("smart.delay.exchange", destination, message, processor);
        throw new UnsupportedOperationException(
            "延迟消息需安装 rabbitmq_delayed_message_exchange 插件，并配置延迟交换机后实现"
        );
    }
}
