package net.lab1024.sa.base.module.support.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * AI聊天响应VO
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@Schema(description = "AI聊天响应VO")
public class AIChatVO {

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "AI回复内容")
    private String content;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "模型显示名称")
    private String modelDisplayName;

    @Schema(description = "服务提供商")
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
}
