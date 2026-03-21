package net.lab1024.sa.base.module.support.ai.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI模型类型枚举
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Getter
@AllArgsConstructor
public enum AIModelTypeEnum {

    /**
     * 聊天模型
     */
    CHAT("CHAT", "聊天模型"),

    /**
     * 嵌入模型 (向量化)
     */
    EMBEDDING("EMBEDDING", "嵌入模型"),

    /**
     * 图片生成模型
     */
    IMAGE("IMAGE", "图片生成"),

    /**
     * 语音模型
     */
    AUDIO("AUDIO", "语音模型"),

    /**
     * 多模态模型
     */
    MULTIMODAL("MULTIMODAL", "多模态"),
    ;

    private final String value;
    private final String desc;

    /**
     * 根据值获取枚举
     */
    public static AIModelTypeEnum getByValue(String value) {
        for (AIModelTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
