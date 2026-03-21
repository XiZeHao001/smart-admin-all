package net.lab1024.sa.base.module.support.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI会话VO
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@Schema(description = "AI会话VO")
public class AIConversationVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "会话标题")
    private String title;

    @Schema(description = "使用的模型")
    private String model;

    @Schema(description = "AI提供商")
    private String provider;

    @Schema(description = "消息数量")
    private Integer messageCount;

    @Schema(description = "总Token数")
    private Integer totalTokens;

    @Schema(description = "总费用")
    private BigDecimal totalCost;

    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
