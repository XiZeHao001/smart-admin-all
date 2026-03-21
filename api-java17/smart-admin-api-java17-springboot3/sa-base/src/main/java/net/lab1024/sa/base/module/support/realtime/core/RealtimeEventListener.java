package net.lab1024.sa.base.module.support.realtime.core;

import net.lab1024.sa.base.module.support.realtime.constant.RealtimeEventTypeEnum;
import net.lab1024.sa.base.module.support.realtime.domain.RealtimeMessage;

/**
 * 实时通讯事件监听器接口
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
public interface RealtimeEventListener {

    /**
     * 连接建立事件
     *
     * @param session 会话信息
     */
    default void onConnect(RealtimeSession session) {
        // 子类可选实现
    }

    /**
     * 连接断开事件
     *
     * @param session 会话信息
     */
    default void onDisconnect(RealtimeSession session) {
        // 子类可选实现
    }

    /**
     * 消息发送事件
     *
     * @param session 会话信息
     * @param message 消息对象
     */
    default void onMessageSent(RealtimeSession session, RealtimeMessage message) {
        // 子类可选实现
    }

    /**
     * 消息接收事件（用于WebSocket）
     *
     * @param session 会话信息
     * @param message 消息对象
     */
    default void onMessageReceived(RealtimeSession session, RealtimeMessage message) {
        // 子类可选实现
    }

    /**
     * 错误事件
     *
     * @param session 会话信息
     * @param error   异常对象
     */
    default void onError(RealtimeSession session, Throwable error) {
        // 子类可选实现
    }

    /**
     * 获取监听器名称
     *
     * @return 监听器名称
     */
    String getListenerName();
}



