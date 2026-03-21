package net.lab1024.sa.base.module.support.ai.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * AI配置表单
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@Schema(description = "AI配置表单")
public class AIConfigForm {

    @Schema(description = "主键ID (编辑时必填)")
    private Long id;

    @Schema(description = "配置唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "配置Key不能为空")
    private String configKey;

    @Schema(description = "服务提供商", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "服务提供商不能为空")
    private String provider;

    @Schema(description = "模型名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "模型名称不能为空")
    private String modelName;

    @Schema(description = "模型显示名称")
    private String modelDisplayName;

    @Schema(description = "API密钥")
    private String apiKey;

    @Schema(description = "API密钥 (用于某些服务商)")
    private String apiSecret;

    @Schema(description = "API基础地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "API地址不能为空")
    private String baseUrl;

    @Schema(description = "最大Token数")
    private Integer maxTokens;

    @Schema(description = "温度参数 (0-2)")
    private BigDecimal temperature;

    @Schema(description = "是否启用")
    @NotNull(message = "是否启用不能为空")
    private Boolean enabledFlag;

    @Schema(description = "是否为默认模型")
    private Boolean defaultFlag;

    @Schema(description = "模型类型")
    private String modelType;

    @Schema(description = "是否支持流式输出")
    private Boolean supportStream;

    @Schema(description = "是否支持函数调用")
    private Boolean supportFunctionCall;

    @Schema(description = "是否支持视觉")
    private Boolean supportVision;

    @Schema(description = "输入价格 (每1K token)")
    private BigDecimal priceInput;

    @Schema(description = "输出价格 (每1K token)")
    private BigDecimal priceOutput;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;
}
