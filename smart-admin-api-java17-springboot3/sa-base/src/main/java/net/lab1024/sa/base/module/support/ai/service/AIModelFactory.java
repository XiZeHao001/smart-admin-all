package net.lab1024.sa.base.module.support.ai.service;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.module.support.ai.constant.AIProviderEnum;
import net.lab1024.sa.base.module.support.ai.domain.entity.AIConfigEntity;
import net.lab1024.sa.base.module.support.ai.util.ApiKeyEncryptUtil;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * AI模型工厂
 * 根据配置动态创建 Spring AI 模型实例
 * 
 * Spring AI 统一抽象：
 * - ChatModel: 同步聊天模型接口
 * - StreamingChatModel: 流式聊天模型接口
 * - EmbeddingModel: 嵌入模型接口
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Component
public class AIModelFactory {

    /**
     * 创建聊天模型 (同步)
     */
    public ChatModel createChatModel(AIConfigEntity config) {
        String provider = config.getProvider();
        AIProviderEnum providerEnum = AIProviderEnum.getByValue(provider);

        if (providerEnum == null) {
            throw new BusinessException("不支持的AI提供商：" + provider);
        }

        // 解密 API Key
        String apiKey = ApiKeyEncryptUtil.decrypt(config.getApiKey());

        switch (providerEnum) {
            case OPENAI:
                return createOpenAiChatModel(config, apiKey);

            case AZURE_OPENAI:
                return createAzureOpenAiChatModel(config, apiKey);

            case DEEPSEEK:
            case MOONSHOT:
            case GROQ:
            case TOGETHER:
            case CUSTOM:
                // 这些服务商兼容 OpenAI API，使用自定义 baseUrl
                return createOpenAiCompatibleChatModel(config, apiKey);

            case OLLAMA:
                return createOllamaChatModel(config);

            default:
                throw new BusinessException("暂不支持的AI提供商：" + provider + 
                    "，请使用 OpenAI 兼容的自定义配置");
        }
    }

    /**
     * 创建流式聊天模型
     * 注意：Spring AI 的 ChatModel 和 StreamingChatModel 是同一个实例
     */
    public StreamingChatModel createStreamingChatModel(AIConfigEntity config) {
        ChatModel chatModel = createChatModel(config);
        if (chatModel instanceof StreamingChatModel) {
            return (StreamingChatModel) chatModel;
        }
        throw new BusinessException("该模型不支持流式输出");
    }

    /**
     * 创建 OpenAI 聊天模型
     */
    private ChatModel createOpenAiChatModel(AIConfigEntity config, String apiKey) {
        try {
            // 创建 OpenAI API 实例
            OpenAiApi openAiApi = new OpenAiApi(apiKey);

            // 构建选项
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(config.getModelName())
                .withTemperature(config.getTemperature().doubleValue())
                .withMaxTokens(config.getMaxTokens())
                .build();

            // 创建 ChatModel
            return new OpenAiChatModel(openAiApi, options);

        } catch (Exception e) {
            log.error("创建 OpenAI ChatModel 失败", e);
            throw new BusinessException("创建 OpenAI 模型失败：" + e.getMessage());
        }
    }

    /**
     * 创建 Azure OpenAI 聊天模型
     */
    private ChatModel createAzureOpenAiChatModel(AIConfigEntity config, String apiKey) {
        try {
            // Azure OpenAI 配置
            AzureOpenAiChatOptions options = AzureOpenAiChatOptions.builder()
                .withDeploymentName(config.getModelName())
                .withTemperature(config.getTemperature().doubleValue())
                .withMaxTokens(config.getMaxTokens())
                .build();

            // 创建 Azure OpenAI ClientBuilder（不调用 buildClient()）
            com.azure.ai.openai.OpenAIClientBuilder azureClientBuilder = new com.azure.ai.openai.OpenAIClientBuilder()
                .credential(new com.azure.core.credential.AzureKeyCredential(apiKey))
                .endpoint(config.getBaseUrl());

            // 创建 Azure OpenAI ChatModel（传入 Builder）
            return new AzureOpenAiChatModel(azureClientBuilder, options);

        } catch (Exception e) {
            log.error("创建 Azure OpenAI ChatModel 失败", e);
            throw new BusinessException("创建 Azure OpenAI 模型失败：" + e.getMessage());
        }
    }

    /**
     * 创建 OpenAI 兼容的聊天模型 (DeepSeek, Moonshot, Groq等)
     */
    private ChatModel createOpenAiCompatibleChatModel(AIConfigEntity config, String apiKey) {
        try {
            // OpenAiApi 构造器会自动在 baseUrl 后拼接 "/v1/chat/completions"
            // 所以这里的 baseUrl 只需要到域名+版本号，不要包含 /chat/completions
            // 例如：https://api.deepseek.com/v1 （正确）
            //      https://api.deepseek.com/v1/chat/completions （错误，会重复）
            
            OpenAiApi openAiApi = new OpenAiApi(
                config.getBaseUrl(),  // baseUrl 应该是 https://api.deepseek.com/v1
                apiKey
            );

            // 构建选项
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(config.getModelName())
                .withTemperature(config.getTemperature().doubleValue())
                .withMaxTokens(config.getMaxTokens())
                .build();

            return new OpenAiChatModel(openAiApi, options);

        } catch (Exception e) {
            log.error("创建 OpenAI 兼容 ChatModel 失败: provider={}", config.getProvider(), e);
            throw new BusinessException("创建模型失败：" + e.getMessage());
        }
    }

    /**
     * 创建 Ollama 本地模型
     */
    private ChatModel createOllamaChatModel(AIConfigEntity config) {
        try {
            // 创建 Ollama API
            OllamaApi ollamaApi = new OllamaApi(config.getBaseUrl());

            // 构建选项
            OllamaOptions options = OllamaOptions.create()
                .withModel(config.getModelName())
                .withTemperature(config.getTemperature().doubleValue());

            // Spring AI 1.0.0-M4 的 OllamaChatModel 需要更多参数
            return new OllamaChatModel(
                ollamaApi, 
                options,
                null,  // FunctionCallbackContext
                java.util.Collections.emptyList(),  // FunctionCallbacks
                io.micrometer.observation.ObservationRegistry.NOOP,  // ObservationRegistry
                null   // ModelManagementOptions
            );

        } catch (Exception e) {
            log.error("创建 Ollama ChatModel 失败", e);
            throw new BusinessException("创建 Ollama 模型失败：" + e.getMessage());
        }
    }
}

