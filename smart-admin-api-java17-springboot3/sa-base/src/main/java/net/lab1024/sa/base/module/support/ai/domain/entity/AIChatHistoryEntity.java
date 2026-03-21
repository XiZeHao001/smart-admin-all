package net.lab1024.sa.base.module.support.ai.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI聊天历史实体
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@TableName("t_ai_chat_history")
public class AIChatHistoryEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
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
     * 消息角色 (system, user, assistant, function)
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * AI提供商
     */
    private String provider;

    /**
     * 输入Token数
     */
    private Integer tokensInput;

    /**
     * 输出Token数
     */
    private Integer tokensOutput;

    /**
     * 总Token数
     */
    private Integer tokensTotal;

    /**
     * 费用
     */
    private BigDecimal costAmount;

    /**
     * 耗时(毫秒)
     */
    private Integer durationMs;

    /**
     * 业务类型
     */
    @TableField(exist = false)
    private String businessType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 删除标记
     */
    private Boolean deletedFlag;
}
