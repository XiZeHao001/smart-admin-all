package net.lab1024.sa.base.module.support.realtime.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 实时通讯事件类型枚举
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Getter
@AllArgsConstructor
public enum RealtimeEventTypeEnum implements BaseEnum {

    /**
     * 连接建立
     */
    CONNECT("CONNECT", "连接建立"),

    /**
     * 连接断开
     */
    DISCONNECT("DISCONNECT", "连接断开"),

    /**
     * 消息发送
     */
    MESSAGE_SENT("MESSAGE_SENT", "消息发送"),

    /**
     * 消息接收
     */
    MESSAGE_RECEIVED("MESSAGE_RECEIVED", "消息接收"),

    /**
     * 连接错误
     */
    ERROR("ERROR", "连接错误"),

    /**
     * 心跳检测
     */
    HEARTBEAT("HEARTBEAT", "心跳检测");

    private final String value;
    private final String desc;

    public static RealtimeEventTypeEnum getByValue(String value) {
        for (RealtimeEventTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}



