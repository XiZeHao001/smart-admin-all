package net.lab1024.sa.base.module.support.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI聊天历史VO
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@Schema(description = "AI聊天历史VO")
public class AIChatHistoryVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "使用的模型")
    private String model;

    @Schema(description = "AI提供商")
    private String provider;

    @Schema(description = "输入Token数")
    private Integer tokensInput;

    @Schema(description = "输出Token数")
    private Integer tokensOutput;

    @Schema(description = "总Token数")
    private Integer tokensTotal;

    @Schema(description = "费用")
    private BigDecimal costAmount;

    @Schema(description = "耗时(毫秒)")
    private Integer durationMs;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}


