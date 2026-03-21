package net.lab1024.sa.base.module.support.ai.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI聊天表单
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@Schema(description = "AI聊天表单")
public class AIChatForm {

    @Schema(description = "会话ID (首次对话可为空，系统自动生成)")
    private String conversationId;

    @Schema(description = "模型配置Key (为空则使用默认模型)")
    private String modelConfigKey;

    @Schema(description = "用户消息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "消息内容不能为空")
    private String message;

    @Schema(description = "温度参数 (0-2，可选，覆盖默认值)")
    private Double temperature;

    @Schema(description = "最大Token数 (可选，覆盖默认值)")
    private Integer maxTokens;

    @Schema(description = "系统提示词 (可选)")
    private String systemPrompt;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "是否流式输出 (默认true)")
    private Boolean stream = true;
}
