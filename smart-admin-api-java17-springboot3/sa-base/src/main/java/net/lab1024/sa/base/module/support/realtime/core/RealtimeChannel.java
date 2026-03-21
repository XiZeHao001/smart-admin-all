package net.lab1024.sa.base.module.support.realtime.core;

import net.lab1024.sa.base.module.support.realtime.constant.RealtimeChannelTypeEnum;
import net.lab1024.sa.base.module.support.realtime.domain.RealtimeMessage;

import java.io.IOException;

/**
 * 实时通讯通道抽象接口
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
public interface RealtimeChannel {

    /**
     * 发送消息
     *
     * @param message 消息对象
     * @throws IOException IO异常
     */
    void send(RealtimeMessage message) throws IOException;

    /**
     * 判断通道是否活跃
     *
     * @return true-活跃 false-已关闭
     */
    boolean isActive();

    /**
     * 关闭通道
     */
    void close();

    /**
     * 获取通道类型
     *
     * @return 通道类型
     */
    RealtimeChannelTypeEnum getChannelType();

    /**
     * 获取会话ID
     *
     * @return 会话ID
     */
    String getSessionId();

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    Long getUserId();

    /**
     * 获取业务类型
     *
     * @return 业务类型
     */
    String getBusinessType();
}



