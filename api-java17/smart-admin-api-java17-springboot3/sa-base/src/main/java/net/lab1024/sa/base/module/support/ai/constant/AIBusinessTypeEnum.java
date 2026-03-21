package net.lab1024.sa.base.module.support.ai.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI业务类型枚举
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Getter
@AllArgsConstructor
public enum AIBusinessTypeEnum {

    /**
     * AI聊天
     */
    AI_CHAT("AI_CHAT", "AI聊天"),

    /**
     * 文档问答 (RAG)
     */
    DOC_QA("DOC_QA", "文档问答"),

    /**
     * 代码生成
     */
    CODE_GEN("CODE_GEN", "代码生成"),

    /**
     * 内容创作
     */
    CONTENT_CREATE("CONTENT_CREATE", "内容创作"),

    /**
     * 数据分析
     */
    DATA_ANALYSIS("DATA_ANALYSIS", "数据分析"),

    /**
     * 智能客服
     */
    CUSTOMER_SERVICE("CUSTOMER_SERVICE", "智能客服"),
    ;

    private final String value;
    private final String desc;

    /**
     * 根据值获取枚举
     */
    public static AIBusinessTypeEnum getByValue(String value) {
        for (AIBusinessTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
