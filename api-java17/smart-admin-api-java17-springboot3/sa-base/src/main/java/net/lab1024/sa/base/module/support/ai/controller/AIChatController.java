package net.lab1024.sa.base.module.support.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.lab1024.sa.base.common.controller.SupportBaseController;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.constant.SwaggerTagConst;
import net.lab1024.sa.base.module.support.ai.domain.form.AIChatForm;
import net.lab1024.sa.base.module.support.ai.domain.vo.AIChatVO;
import net.lab1024.sa.base.module.support.ai.domain.vo.AIConfigVO;
import net.lab1024.sa.base.module.support.ai.domain.vo.AIConversationVO;
import net.lab1024.sa.base.module.support.ai.service.AIChatService;
import net.lab1024.sa.base.module.support.ai.service.AIConfigService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * AI聊天Controller - 基于 Spring AI
 * 
 * @Author 1024创新实验室-主任:卓大
 * @Date 2025-12-01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
@RestController
@Tag(name = SwaggerTagConst.Support.AI_CHAT)
public class AIChatController extends SupportBaseController {

    @Resource
    private AIChatService aiChatService;

    @Resource
    private AIConfigService aiConfigService;

    @Operation(summary = "获取可用模型列表")
    @GetMapping("/ai/chat/models")
    public ResponseDTO<List<AIConfigVO>> getAvailableModels() {
        return ResponseDTO.ok(aiConfigService.getEnabledModels());
    }

    @Operation(summary = "获取用户会话列表")
    @GetMapping("/ai/chat/conversations")
    public ResponseDTO<List<AIConversationVO>> getConversations() {
        return aiChatService.getConversations();
    }

    @Operation(summary = "获取会话历史记录")
    @GetMapping("/ai/chat/conversations/{conversationId}")
    public ResponseDTO<List<net.lab1024.sa.base.module.support.ai.domain.vo.AIChatHistoryVO>> getConversationHistory(
            @PathVariable String conversationId) {
        return aiChatService.getConversationHistory(conversationId);
    }

    @Operation(summary = "AI聊天（同步）")
    @PostMapping("/ai/chat/send")
    public ResponseDTO<AIChatVO> chat(@RequestBody @Valid AIChatForm form) {
        return aiChatService.chat(form);
    }

    @Operation(summary = "AI聊天（流式SSE）")
    @PostMapping(value = "/ai/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody @Valid AIChatForm form) {
        return aiChatService.chatStream(form);
    }
}
