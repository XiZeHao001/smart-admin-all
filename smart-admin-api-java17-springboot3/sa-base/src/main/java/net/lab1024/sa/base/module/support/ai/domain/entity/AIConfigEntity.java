package net.lab1024.sa.base.module.support.ai.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI模型配置实体
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@TableName("t_ai_config")
public class AIConfigEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 配置唯一标识 (例如: OPENAI.gpt-4)
     */
    private String configKey;

    /**
     * 服务提供商 (OPENAI, AZURE_OPENAI, DEEPSEEK等)
     */
    private String provider;

    /**
     * 模型名称 (例如: gpt-4, gpt-3.5-turbo)
     */
    private String modelName;

    /**
     * 模型显示名称
     */
    private String modelDisplayName;

    /**
     * API密钥 (加密存储)
     */
    private String apiKey;

    /**
     * API密钥 (用于某些服务商)
     */
    private String apiSecret;

    /**
     * API基础地址
     */
    private String baseUrl;

    /**
     * 最大Token数
     */
    private Integer maxTokens;

    /**
     * 温度参数 (0-2)
     */
    private BigDecimal temperature;

    /**
     * 是否启用
     */
    private Boolean enabledFlag;

    /**
     * 是否为默认模型
     */
    private Boolean defaultFlag;

    /**
     * 模型类型 (CHAT, EMBEDDING, IMAGE等)
     */
    private String modelType;

    /**
     * 是否支持流式输出
     */
    private Boolean supportStream;

    /**
     * 是否支持函数调用
     */
    private Boolean supportFunctionCall;

    /**
     * 是否支持视觉 (图片理解)
     */
    private Boolean supportVision;

    /**
     * 输入价格 (每1K token)
     */
    private BigDecimal priceInput;

    /**
     * 输出价格 (每1K token)
     */
    private BigDecimal priceOutput;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;

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
