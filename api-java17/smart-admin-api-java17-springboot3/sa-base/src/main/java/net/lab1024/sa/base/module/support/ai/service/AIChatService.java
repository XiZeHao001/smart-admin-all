package net.lab1024.sa.base.module.support.ai.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.module.support.ai.dao.AIChatHistoryDao;
import net.lab1024.sa.base.module.support.ai.dao.AIConfigDao;
import net.lab1024.sa.base.module.support.ai.dao.AIConversationDao;
import net.lab1024.sa.base.module.support.ai.domain.entity.AIChatHistoryEntity;
import net.lab1024.sa.base.module.support.ai.domain.entity.AIConfigEntity;
import net.lab1024.sa.base.module.support.ai.domain.entity.AIConversationEntity;
import net.lab1024.sa.base.module.support.ai.domain.form.AIChatForm;
import net.lab1024.sa.base.module.support.ai.domain.vo.AIChatVO;
import net.lab1024.sa.base.module.support.ai.domain.vo.AIChatHistoryVO;
import net.lab1024.sa.base.module.support.ai.domain.vo.AIConversationVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AI聊天服务 - 基于 Spring AI
 * 
 * Spring AI 核心概念：
 * - ChatModel: 同步聊天模型接口
 * - StreamingChatModel: 流式聊天模型接口
 * - Prompt: 提示词，包含多个 Message
 * - Message: 消息，包括 SystemMessage, UserMessage, AssistantMessage
 * - ChatResponse: 聊天响应
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Service
public class AIChatService {

    @Resource
    private AIModelFactory aiModelFactory;

    @Resource
    private AIConfigDao aiConfigDao;

    @Resource
    private AIChatHistoryDao chatHistoryDao;

    @Resource
    private AIConversationDao conversationDao;

    /**
     * 同步聊天 (非流式)
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<AIChatVO> chat(AIChatForm form) {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 获取用户ID
            Long userId = SmartRequestUtil.getRequestUserId();

            // 2. 获取模型配置
            AIConfigEntity config = getModelConfig(form.getModelConfigKey());

            // 3. 获取或创建会话ID
            String conversationId = getOrCreateConversationId(form, userId, config);

            // 4. 创建 Spring AI ChatModel
            ChatModel chatModel = aiModelFactory.createChatModel(config);

            // 5. 构建对话历史上下文
            List<Message> messages = buildMessages(form, conversationId);

            // 6. 调用 Spring AI
            Prompt prompt = new Prompt(messages);
            ChatResponse response = chatModel.call(prompt);

            // 7. 提取响应内容
            String aiReply = response.getResult().getOutput().getContent();

            // 8. 保存历史记录
            saveHistory(conversationId, userId, config, form.getMessage(), aiReply, response, startTime);

            // 9. 构建响应 VO
            AIChatVO vo = buildChatVO(conversationId, config, aiReply, response, startTime);

            return ResponseDTO.ok(vo);

        } catch (Exception e) {
            log.error("AI聊天失败", e);
            return ResponseDTO.userErrorParam("AI聊天失败：" + e.getMessage());
        }
    }

    /**
     * 流式聊天 (SSE)
     */
    public SseEmitter chatStream(AIChatForm form) {
        // !!! 重要：在主线程中获取用户ID，不能在异步线程中获取 !!!
        final Long userId = SmartRequestUtil.getRequestUserId();
        if (userId == null) {
            SseEmitter emitter = new SseEmitter(0L);
            try {
                emitter.send(SseEmitter.event().data("错误：未登录或登录已过期"));
                emitter.complete();
            } catch (Exception e) {
                log.error("发送SSE错误", e);
            }
            return emitter;
        }

        // 创建 SSE Emitter
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L); // 5分钟超时

        // 异步处理
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            StringBuilder fullContent = new StringBuilder();

            try {
                // 1. 用户ID已在主线程获取

                // 2. 获取或创建会话ID
                // 2. 获取模型配置
                AIConfigEntity config = getModelConfig(form.getModelConfigKey());

                // 3. 获取或创建会话ID
                String conversationId = getOrCreateConversationId(form, userId, config);

                // 4. 创建 Spring AI StreamingChatModel
                StreamingChatModel streamingChatModel = aiModelFactory.createStreamingChatModel(config);

                // 5. 构建对话历史上下文
                List<Message> messages = buildMessages(form, conversationId);

                // 6. 调用 Spring AI 流式接口
                Prompt prompt = new Prompt(messages);
                Flux<ChatResponse> flux = streamingChatModel.stream(prompt);

                // 7. 发送流式数据
                flux.subscribe(
                    chatResponse -> {
                        try {
                            String content = chatResponse.getResult().getOutput().getContent();
                            if (StringUtils.isNotBlank(content)) {
                                fullContent.append(content);
                                // 发送 SSE 事件（JSON 格式）
                                String jsonData = String.format("{\"content\":\"%s\"}", 
                                    content.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n"));
                                emitter.send(SseEmitter.event().data(jsonData));
                            }
                        } catch (Exception e) {
                            log.error("发送SSE数据失败", e);
                        }
                    },
                    error -> {
                        log.error("流式聊天失败", error);
                        try {
                            String errorMsg = error.getMessage().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
                            String jsonData = String.format("{\"error\":\"AI响应失败：%s\"}", errorMsg);
                            emitter.send(SseEmitter.event().data(jsonData));
                        } catch (Exception e) {
                            log.error("发送错误消息失败", e);
                        }
                        emitter.completeWithError(error);
                    },
                    () -> {
                        try {
                            // 流结束，保存历史记录
                            saveHistorySimple(conversationId, userId, config, 
                                form.getMessage(), fullContent.toString(), startTime);

                            // 发送完成信号（JSON 格式）
                            String jsonData = String.format("{\"done\":true,\"conversationId\":\"%s\"}", conversationId);
                            emitter.send(SseEmitter.event().data(jsonData));

                            emitter.complete();

                        } catch (Exception e) {
                            log.error("完成流式响应失败", e);
                            emitter.completeWithError(e);
                        }
                    }
                );

            } catch (Exception e) {
                log.error("启动流式聊天失败", e);
                try {
                    String errorMsg = e.getMessage().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
                    String jsonData = String.format("{\"error\":\"启动AI对话失败：%s\"}", errorMsg);
                    emitter.send(SseEmitter.event().data(jsonData));
                } catch (Exception ex) {
                    log.error("发送错误消息失败", ex);
                }
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    /**
     * 获取或创建会话ID
     */
    private String getOrCreateConversationId(AIChatForm form, Long userId, AIConfigEntity config) {
        String conversationId = form.getConversationId();

        if (StringUtils.isBlank(conversationId)) {
            // 创建新会话
            conversationId = UUID.randomUUID().toString().replace("-", "");

            AIConversationEntity conversation = new AIConversationEntity();
            conversation.setConversationId(conversationId);
            conversation.setUserId(userId);
            conversation.setTitle("新对话");
            conversation.setModel(config.getModelName());
            conversation.setProvider(config.getProvider());
            conversation.setMessageCount(0);
            conversation.setTotalTokens(0);
            conversation.setTotalCost(BigDecimal.ZERO);
            conversation.setLastMessageTime(LocalDateTime.now());
            conversation.setCreateTime(LocalDateTime.now());
            conversation.setUpdateTime(LocalDateTime.now());
            conversation.setDeletedFlag(false);

            conversationDao.insert(conversation);
        }

        return conversationId;
    }

    /**
     * 获取模型配置
     */
    private AIConfigEntity getModelConfig(String modelConfigKey) {
        AIConfigEntity config;

        if (StringUtils.isNotBlank(modelConfigKey)) {
            config = aiConfigDao.selectByConfigKey(modelConfigKey);
        } else {
            // 使用默认模型
            config = aiConfigDao.selectDefaultModel();
        }

        if (config == null) {
            throw new RuntimeException("模型配置不存在");
        }

        if (!Boolean.TRUE.equals(config.getEnabledFlag())) {
            throw new RuntimeException("该模型已禁用");
        }

        return config;
    }

    /**
     * 构建消息列表 (包含历史上下文)
     */
    private List<Message> buildMessages(AIChatForm form, String conversationId) {
        List<Message> messages = new ArrayList<>();

        // 1. 系统提示词
        if (StringUtils.isNotBlank(form.getSystemPrompt())) {
            messages.add(new SystemMessage(form.getSystemPrompt()));
        }

        // 2. 历史对话上下文 (最近10条)
        List<AIChatHistoryEntity> historyList = chatHistoryDao.selectByConversationId(conversationId, 10);
        for (AIChatHistoryEntity history : historyList) {
            if ("user".equals(history.getRole())) {
                messages.add(new UserMessage(history.getContent()));
            } else if ("assistant".equals(history.getRole())) {
                messages.add(new AssistantMessage(history.getContent()));
            } else if ("system".equals(history.getRole())) {
                messages.add(new SystemMessage(history.getContent()));
            }
        }

        // 3. 当前用户消息
        messages.add(new UserMessage(form.getMessage()));

        return messages;
    }

    /**
     * 保存历史记录 (完整版)
     */
    private void saveHistory(String conversationId, Long userId, AIConfigEntity config,
                            String userMessage, String aiReply, ChatResponse response, long startTime) {

        // 保存用户消息
        AIChatHistoryEntity userHistory = new AIChatHistoryEntity();
        userHistory.setConversationId(conversationId);
        userHistory.setUserId(userId);
        userHistory.setModel(config.getModelName());
        userHistory.setProvider(config.getProvider());
        userHistory.setRole("user");
        userHistory.setContent(userMessage);
        userHistory.setTokensInput(0);
        userHistory.setTokensOutput(0);
        userHistory.setTokensTotal(0);
        userHistory.setCostAmount(BigDecimal.ZERO);
        userHistory.setDurationMs(0);
        userHistory.setCreateTime(LocalDateTime.now());
        userHistory.setDeletedFlag(false);
        chatHistoryDao.insert(userHistory);

        // 保存AI回复
        AIChatHistoryEntity aiHistory = new AIChatHistoryEntity();
        aiHistory.setConversationId(conversationId);
        aiHistory.setUserId(userId);
        aiHistory.setModel(config.getModelName());
        aiHistory.setProvider(config.getProvider());
        aiHistory.setRole("assistant");
        aiHistory.setContent(aiReply);

        // 提取 Token 使用量
        if (response != null && response.getMetadata() != null && response.getMetadata().getUsage() != null) {
            aiHistory.setTokensInput(response.getMetadata().getUsage().getPromptTokens().intValue());
            aiHistory.setTokensOutput(response.getMetadata().getUsage().getGenerationTokens().intValue());
            aiHistory.setTokensTotal(response.getMetadata().getUsage().getTotalTokens().intValue());

            // 计算费用
            BigDecimal cost = calculateCost(config, 
                aiHistory.getTokensInput(), 
                aiHistory.getTokensOutput());
            aiHistory.setCostAmount(cost);
        } else {
            aiHistory.setTokensInput(0);
            aiHistory.setTokensOutput(0);
            aiHistory.setTokensTotal(0);
            aiHistory.setCostAmount(BigDecimal.ZERO);
        }

        aiHistory.setDurationMs((int) (System.currentTimeMillis() - startTime));
        aiHistory.setCreateTime(LocalDateTime.now());
        aiHistory.setDeletedFlag(false);
        chatHistoryDao.insert(aiHistory);
    }

    /**
     * 保存历史记录 (简化版，用于流式)
     */
    private void saveHistorySimple(String conversationId, Long userId, AIConfigEntity config,
                                   String userMessage, String aiReply, long startTime) {

        // 保存用户消息
        AIChatHistoryEntity userHistory = new AIChatHistoryEntity();
        userHistory.setConversationId(conversationId);
        userHistory.setUserId(userId);
        userHistory.setModel(config.getModelName());
        userHistory.setProvider(config.getProvider());
        userHistory.setRole("user");
        userHistory.setContent(userMessage);
        userHistory.setTokensInput(0);
        userHistory.setTokensOutput(0);
        userHistory.setTokensTotal(0);
        userHistory.setCostAmount(BigDecimal.ZERO);
        userHistory.setDurationMs(0);
        userHistory.setCreateTime(LocalDateTime.now());
        userHistory.setDeletedFlag(false);
        chatHistoryDao.insert(userHistory);

        // 保存AI回复
        AIChatHistoryEntity aiHistory = new AIChatHistoryEntity();
        aiHistory.setConversationId(conversationId);
        aiHistory.setUserId(userId);
        aiHistory.setModel(config.getModelName());
        aiHistory.setProvider(config.getProvider());
        aiHistory.setRole("assistant");
        aiHistory.setContent(aiReply);
        aiHistory.setTokensInput(0);
        aiHistory.setTokensOutput(estimateTokens(aiReply));
        aiHistory.setTokensTotal(aiHistory.getTokensOutput());
        aiHistory.setCostAmount(calculateCost(config, 0, aiHistory.getTokensOutput()));
        aiHistory.setDurationMs((int) (System.currentTimeMillis() - startTime));
        aiHistory.setCreateTime(LocalDateTime.now());
        aiHistory.setDeletedFlag(false);
        chatHistoryDao.insert(aiHistory);
    }

    /**
     * 构建响应 VO
     */
    private AIChatVO buildChatVO(String conversationId, AIConfigEntity config, 
                                 String aiReply, ChatResponse response, long startTime) {
        AIChatVO vo = new AIChatVO();
        vo.setConversationId(conversationId);
        vo.setContent(aiReply);
        vo.setModelName(config.getModelName());
        vo.setModelDisplayName(config.getModelDisplayName());
        vo.setProvider(config.getProvider());

        if (response != null && response.getMetadata() != null && response.getMetadata().getUsage() != null) {
            vo.setTokensInput(response.getMetadata().getUsage().getPromptTokens().intValue());
            vo.setTokensOutput(response.getMetadata().getUsage().getGenerationTokens().intValue());
            vo.setTokensTotal(response.getMetadata().getUsage().getTotalTokens().intValue());
            vo.setCostAmount(calculateCost(config, vo.getTokensInput(), vo.getTokensOutput()));
        }

        vo.setDurationMs((int) (System.currentTimeMillis() - startTime));

        return vo;
    }

    /**
     * 计算费用
     */
    private BigDecimal calculateCost(AIConfigEntity config, Integer inputTokens, Integer outputTokens) {
        if (config.getPriceInput() == null || config.getPriceOutput() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal inputCost = config.getPriceInput()
            .multiply(BigDecimal.valueOf(inputTokens))
            .divide(BigDecimal.valueOf(1000), 6, BigDecimal.ROUND_HALF_UP);

        BigDecimal outputCost = config.getPriceOutput()
            .multiply(BigDecimal.valueOf(outputTokens))
            .divide(BigDecimal.valueOf(1000), 6, BigDecimal.ROUND_HALF_UP);

        return inputCost.add(outputCost);
    }

    /**
     * 估算Token数 (简单估算：中文按字符数，英文按单词数的1.3倍)
     */
    private Integer estimateTokens(String text) {
        if (StringUtils.isBlank(text)) {
            return 0;
        }

        // 简单估算：平均每4个字符约等于1个Token
        return (int) Math.ceil(text.length() / 4.0);
    }

    /**
     * 获取用户的会话列表
     */
    public ResponseDTO<List<AIConversationVO>> getConversations() {
        Long userId = SmartRequestUtil.getRequestUserId();
        
        // 查询用户的所有会话（按最后消息时间倒序）
        List<AIConversationEntity> conversations = conversationDao.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AIConversationEntity>()
                .eq(AIConversationEntity::getUserId, userId)
                .eq(AIConversationEntity::getDeletedFlag, false)
                .orderByDesc(AIConversationEntity::getLastMessageTime)
        );

        List<AIConversationVO> voList = conversations.stream()
            .map(entity -> SmartBeanUtil.copy(entity, AIConversationVO.class))
            .collect(java.util.stream.Collectors.toList());

        return ResponseDTO.ok(voList);
    }

    /**
     * 获取会话的历史记录
     */
    public ResponseDTO<List<AIChatHistoryVO>> getConversationHistory(String conversationId) {
        // 查询会话历史（按时间正序）
        List<AIChatHistoryEntity> histories = chatHistoryDao.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AIChatHistoryEntity>()
                .eq(AIChatHistoryEntity::getConversationId, conversationId)
                .eq(AIChatHistoryEntity::getDeletedFlag, false)
                .orderByAsc(AIChatHistoryEntity::getCreateTime)
        );

        List<AIChatHistoryVO> voList = histories.stream()
            .map(entity -> SmartBeanUtil.copy(entity, AIChatHistoryVO.class))
            .collect(java.util.stream.Collectors.toList());

        return ResponseDTO.ok(voList);
    }
}
