package net.lab1024.sa.starter.mq;

import net.lab1024.sa.starter.mq.service.MqSendService;
import net.lab1024.sa.starter.mq.service.impl.KafkaMqSendServiceImpl;
import net.lab1024.sa.starter.mq.service.impl.RabbitMqSendServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 消息队列自动配置
 * <p>触发条件：{@code smart.mq.enabled=true}</p>
 */
@Configuration
@EnableConfigurationProperties(MqProperties.class)
@ConditionalOnProperty(prefix = "smart.mq", name = "enabled", havingValue = "true")
public class MqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MqSendService.class)
    @ConditionalOnProperty(prefix = "smart.mq", name = "provider", havingValue = "rabbitmq", matchIfMissing = true)
    @ConditionalOnClass(RabbitTemplate.class)
    public MqSendService rabbitMqSendService(RabbitTemplate rabbitTemplate) {
        return new RabbitMqSendServiceImpl(rabbitTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(MqSendService.class)
    @ConditionalOnProperty(prefix = "smart.mq", name = "provider", havingValue = "kafka")
    @ConditionalOnClass(KafkaTemplate.class)
    public MqSendService kafkaMqSendService(KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaMqSendServiceImpl(kafkaTemplate);
    }
}
