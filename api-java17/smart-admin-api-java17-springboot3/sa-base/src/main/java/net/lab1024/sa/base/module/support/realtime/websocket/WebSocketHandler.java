package net.lab1024.sa.base.module.support.realtime.websocket;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.module.support.realtime.domain.RealtimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.Resource;
import java.net.URI;
import java.util.Map;

/**
 * WebSocket处理器
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Resource
    private WebSocketSessionManager sessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从URL参数中获取用户ID和业务类型
        URI uri = session.getUri();
        if (uri == null) {
            session.close();
            return;
        }

        Map<String, String> params = parseQuery(uri.getQuery());
        String userIdStr = params.get("userId");
        String businessType = params.getOrDefault("businessType", "DEFAULT");

        if (StringUtils.isBlank(userIdStr)) {
            log.warn("WebSocket连接缺少userId参数");
            session.close();
            return;
        }

        Long userId = Long.parseLong(userIdStr);

        // 注册会话
        sessionManager.registerSession(userId, businessType, session);

        log.info("WebSocket连接建立: sessionId={}, userId={}, businessType={}", 
                session.getId(), userId, businessType);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("收到WebSocket消息: sessionId={}, payload={}", session.getId(), payload);

        try {
            // 解析消息
            RealtimeMessage realtimeMessage = JSON.parseObject(payload, RealtimeMessage.class);
            
            // 处理消息
            sessionManager.handleMessage(session.getId(), realtimeMessage);
        } catch (Exception e) {
            log.error("处理WebSocket消息失败: sessionId={}", session.getId(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionManager.unregisterSession(session.getId());
        log.info("WebSocket连接关闭: sessionId={}, status={}", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: sessionId={}", session.getId(), exception);
        sessionManager.handleError(session.getId(), exception);
    }

    /**
     * 解析URL查询参数
     */
    private Map<String, String> parseQuery(String query) {
        if (StringUtils.isBlank(query)) {
            return Map.of();
        }

        Map<String, String> params = new java.util.HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
}



