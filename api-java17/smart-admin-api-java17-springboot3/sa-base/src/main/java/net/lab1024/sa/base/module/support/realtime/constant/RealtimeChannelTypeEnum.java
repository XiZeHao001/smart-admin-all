package net.lab1024.sa.base.module.support.realtime.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 实时通讯通道类型枚举
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Getter
@AllArgsConstructor
public enum RealtimeChannelTypeEnum implements BaseEnum {

    /**
     * SSE（Server-Sent Events）单向推送
     */
    SSE("SSE", "服务端推送"),

    /**
     * WebSocket 双向通信
     */
    WEBSOCKET("WEBSOCKET", "WebSocket双向通信"),

    /**
     * 长轮询（降级方案）
     */
    LONG_POLLING("LONG_POLLING", "长轮询");

    private final String value;
    private final String desc;

    public static RealtimeChannelTypeEnum getByValue(String value) {
        for (RealtimeChannelTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}



