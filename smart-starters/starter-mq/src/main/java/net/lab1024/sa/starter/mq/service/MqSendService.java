package net.lab1024.sa.starter.mq.service;

/**
 * 消息队列统一发送抽象
 * <p>业务层只依赖此接口，底层切换 RabbitMQ / Kafka 无感知。</p>
 */
public interface MqSendService {

    /**
     * 发送消息到指定目标
     *
     * @param destination RabbitMQ 时为 routingKey，Kafka 时为 topic
     * @param message     消息体（建议传 JSON 字符串）
     */
    void send(String destination, String message);

    /**
     * 延迟发送（仅 RabbitMQ 支持，Kafka 实现可忽略或抛出 UnsupportedOperationException）
     *
     * @param destination  目标
     * @param message      消息体
     * @param delayMillis  延迟毫秒数
     */
    void sendDelay(String destination, String message, long delayMillis);
}
