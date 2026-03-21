package net.lab1024.sa.base.module.support.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AI模型配置VO (简化版，用于下拉选择)
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@Schema(description = "AI模型配置VO (简化版)")
public class AIModelConfigVO {

    @Schema(description = "配置唯一标识")
    private String configKey;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "模型显示名称")
    private String modelDisplayName;

    @Schema(description = "服务提供商")
    private String provider;

    @Schema(description = "服务提供商显示名称")
    private String providerDisplayName;

    @Schema(description = "模型类型")
    private String modelType;

    @Schema(description = "是否为默认模型")
    private Boolean defaultFlag;

    @Schema(description = "是否支持流式输出")
    private Boolean supportStream;

    @Schema(description = "是否支持函数调用")
    private Boolean supportFunctionCall;

    @Schema(description = "是否支持视觉")
    private Boolean supportVision;
}
