package net.lab1024.sa.starter.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息队列配置
 * <pre>
 * smart:
 *   mq:
 *     enabled: true
 *     provider: rabbitmq   # rabbitmq | kafka
 * </pre>
 * RabbitMQ / Kafka 连接参数复用 Spring Boot 原生配置（spring.rabbitmq / spring.kafka）
 */
@Data
@ConfigurationProperties(prefix = "smart.mq")
public class MqProperties {

    private boolean enabled = false;

    /** 消息中间件类型：rabbitmq / kafka */
    private String provider = "rabbitmq";
}
