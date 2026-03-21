package net.lab1024.sa.base.module.support.realtime.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lab1024.sa.base.module.support.realtime.constant.RealtimeMessageTypeEnum;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 实时消息统一封装
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeMessage {

    /**
     * 消息ID（唯一标识）
     */
    private String messageId;

    /**
     * 消息类型
     */
    private RealtimeMessageTypeEnum messageType;

    /**
     * 业务类型（用于区分不同业务模块）
     */
    private String businessType;

    /**
     * 消息内容
     */
    private Object data;

    /**
     * 目标用户ID
     */
    private Long targetUserId;

    /**
     * 目标会话ID（用于AI对话、WebSocket房间等场景）
     */
    private String targetSessionId;

    /**
     * 时间戳
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 扩展字段
     */
    @Builder.Default
    private Map<String, Object> extra = new HashMap<>();

    /**
     * 添加扩展字段
     */
    public RealtimeMessage putExtra(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.put(key, value);
        return this;
    }

    /**
     * 获取扩展字段
     */
    public Object getExtra(String key) {
        return this.extra != null ? this.extra.get(key) : null;
    }
}



