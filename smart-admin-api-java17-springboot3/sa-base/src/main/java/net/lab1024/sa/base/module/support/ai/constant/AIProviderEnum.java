package net.lab1024.sa.base.module.support.ai.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI服务提供商枚举
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Getter
@AllArgsConstructor
public enum AIProviderEnum {

    /**
     * OpenAI (GPT-4, GPT-3.5)
     */
    OPENAI("OPENAI", "OpenAI"),

    /**
     * Azure OpenAI
     */
    AZURE_OPENAI("AZURE_OPENAI", "Azure OpenAI"),

    /**
     * DeepSeek (国产，兼容 OpenAI API)
     */
    DEEPSEEK("DEEPSEEK", "DeepSeek"),

    /**
     * 阿里云通义千问
     */
    DASHSCOPE("DASHSCOPE", "阿里云通义千问"),

    /**
     * 百度文心一言
     */
    BAIDU("BAIDU", "百度文心"),

    /**
     * 智谱AI (ChatGLM)
     */
    ZHIPU("ZHIPU", "智谱AI"),

    /**
     * 月之暗面 (Moonshot, Kimi)
     */
    MOONSHOT("MOONSHOT", "月之暗面"),

    /**
     * 讯飞星火
     */
    XFYUN("XFYUN", "讯飞星火"),

    /**
     * 腾讯混元
     */
    HUNYUAN("HUNYUAN", "腾讯混元"),

    /**
     * MiniMax
     */
    MINIMAX("MINIMAX", "MiniMax"),

    /**
     * 零一万物
     */
    YI("YI", "零一万物"),

    /**
     * Anthropic (Claude)
     */
    ANTHROPIC("ANTHROPIC", "Anthropic"),

    /**
     * Google (Gemini)
     */
    GOOGLE("GOOGLE", "Google"),

    /**
     * Ollama (本地部署)
     */
    OLLAMA("OLLAMA", "Ollama本地"),

    /**
     * Groq
     */
    GROQ("GROQ", "Groq"),

    /**
     * Together AI
     */
    TOGETHER("TOGETHER", "Together AI"),

    /**
     * Replicate
     */
    REPLICATE("REPLICATE", "Replicate"),

    /**
     * Cohere
     */
    COHERE("COHERE", "Cohere"),

    /**
     * Mistral AI
     */
    MISTRAL("MISTRAL", "Mistral AI"),

    /**
     * Stability AI
     */
    STABILITY("STABILITY", "Stability AI"),

    /**
     * 其他 (自定义兼容 OpenAI API)
     */
    CUSTOM("CUSTOM", "自定义"),
    ;

    private final String value;
    private final String desc;

    /**
     * 根据值获取枚举
     */
    public static AIProviderEnum getByValue(String value) {
        for (AIProviderEnum provider : values()) {
            if (provider.getValue().equals(value)) {
                return provider;
            }
        }
        return null;
    }
}
