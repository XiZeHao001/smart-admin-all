package net.lab1024.sa.base.module.support.ai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 基础配置
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Configuration
public class SpringAIConfig {

    /**
     * 聊天记忆 (用于保持上下文)
     * 默认使用内存存储，可以切换到 Redis 等持久化存储
     */
    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }
}


