package net.lab1024.sa.base.module.support.ai.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

/**
 * AI配置查询表单
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@Schema(description = "AI配置查询表单")
public class AIConfigQueryForm extends PageParam {

    @Schema(description = "服务提供商")
    private String provider;

    @Schema(description = "模型类型")
    private String modelType;

    @Schema(description = "是否启用")
    private Boolean enabledFlag;

    @Schema(description = "搜索关键词 (模型名称/显示名称)")
    private String searchWord;
}
