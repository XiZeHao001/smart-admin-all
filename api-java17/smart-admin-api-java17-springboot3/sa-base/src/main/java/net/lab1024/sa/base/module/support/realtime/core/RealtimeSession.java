package net.lab1024.sa.base.module.support.realtime.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lab1024.sa.base.module.support.realtime.constant.RealtimeChannelTypeEnum;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 实时通讯会话信息
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
public class RealtimeSession {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 通道类型
     */
    private RealtimeChannelTypeEnum channelType;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 通道对象
     */
    private RealtimeChannel channel;

    /**
     * 创建时间
     */
    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 最后活跃时间
     */
    @Builder.Default
    private LocalDateTime lastActiveTime = LocalDateTime.now();

    /**
     * 扩展属性
     */
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * 更新最后活跃时间
     */
    public void updateLastActiveTime() {
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 设置属性
     */
    public void setAttribute(String key, Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }

    /**
     * 获取属性
     */
    public Object getAttribute(String key) {
        return this.attributes != null ? this.attributes.get(key) : null;
    }

    /**
     * 判断会话是否活跃
     */
    public boolean isActive() {
        return this.channel != null && this.channel.isActive();
    }
}



