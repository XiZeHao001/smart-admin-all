package net.lab1024.sa.base.module.support.ai.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI配置VO
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@Schema(description = "AI配置VO")
public class AIConfigVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "配置唯一标识")
    private String configKey;

    @Schema(description = "服务提供商")
    private String provider;

    @Schema(description = "服务提供商显示名称")
    private String providerDisplayName;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "模型显示名称")
    private String modelDisplayName;

    @Schema(description = "API密钥 (脱敏)")
    private String apiKeyMasked;

    @Schema(description = "是否配置了API Key")
    private Boolean hasApiKey;

    @Schema(description = "API基础地址")
    private String baseUrl;

    @Schema(description = "最大Token数")
    private Integer maxTokens;

    @Schema(description = "温度参数")
    private String temperature;

    @Schema(description = "是否启用")
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
    private String priceInput;

    @Schema(description = "输出价格 (每1K token)")
    private String priceOutput;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
