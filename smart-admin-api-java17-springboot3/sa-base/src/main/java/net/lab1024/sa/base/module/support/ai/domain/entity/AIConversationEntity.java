package net.lab1024.sa.base.module.support.ai.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI会话实体
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@TableName("t_ai_conversation")
public class AIConversationEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID (UUID)
     */
    private String conversationId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * AI提供商
     */
    private String provider;

    /**
     * 消息数量
     */
    private Integer messageCount;

    /**
     * 总Token数
     */
    private Integer totalTokens;

    /**
     * 总费用
     */
    private java.math.BigDecimal totalCost;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    private Boolean deletedFlag;
}
