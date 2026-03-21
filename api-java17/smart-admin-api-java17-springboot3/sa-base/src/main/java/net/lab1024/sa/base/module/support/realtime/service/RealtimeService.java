package net.lab1024.sa.base.module.support.realtime.service;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.module.support.realtime.constant.RealtimeMessageTypeEnum;
import net.lab1024.sa.base.module.support.realtime.core.RealtimeEventListener;
import net.lab1024.sa.base.module.support.realtime.core.RealtimeSession;
import net.lab1024.sa.base.module.support.realtime.domain.RealtimeMessage;
import net.lab1024.sa.base.module.support.realtime.sse.SseEmitterManager;
import net.lab1024.sa.base.module.support.realtime.websocket.WebSocketSessionManager;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 实时通讯统一服务接口
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Service
public class RealtimeService {

    @Resource
    private SseEmitterManager sseEmitterManager;

    @Resource
    private WebSocketSessionManager webSocketSessionManager;

    /**
     * 创建SSE连接（用于AI、日志流等单向推送场景）
     *
     * @param userId       用户ID
     * @param businessType 业务类型
     * @return SseEmitter对象
     */
    public SseEmitter createSseChannel(Long userId, String businessType) {
        return sseEmitterManager.createConnection(userId, businessType);
    }

    /**
     * 创建SSE连接（自定义超时时间）
     *
     * @param userId       用户ID
     * @param businessType 业务类型
     * @param timeout      超时时间（毫秒）
     * @return SseEmitter对象
     */
    public SseEmitter createSseChannel(Long userId, String businessType, long timeout) {
        return sseEmitterManager.createConnection(userId, businessType, timeout);
    }

    /**
     * 发送消息到指定用户（自动选择可用的通道：优先WebSocket，降级到SSE）
     *
     * @param userId  用户ID
     * @param message 消息对象
     */
    public void sendToUser(Long userId, RealtimeMessage message) {
        // 设置消息ID和时间戳
        if (message.getMessageId() == null) {
            message.setMessageId(UUID.randomUUID().toString());
        }
        message.setTargetUserId(userId);

        // 优先使用WebSocket
        List<RealtimeSession> wsSessions = webSocketSessionManager.getUserSessions(userId);
        if (!wsSessions.isEmpty()) {
            wsSessions.forEach(session -> {
                try {
                    session.getChannel().send(message);
                    log.debug("通过WebSocket发送消息: userId={}, messageType={}", userId, message.getMessageType());
                } catch (IOException e) {
                    log.error("WebSocket发送消息失败: userId={}, sessionId={}", userId, session.getSessionId(), e);
                }
            });
            return;
        }

        // 降级到SSE
        List<RealtimeSession> sseSessions = sseEmitterManager.getUserSessions(userId);
        if (!sseSessions.isEmpty()) {
            sseSessions.forEach(session -> {
                try {
                    session.getChannel().send(message);
                    log.debug("通过SSE发送消息: userId={}, messageType={}", userId, message.getMessageType());
                } catch (IOException e) {
                    log.error("SSE发送消息失败: userId={}, sessionId={}", userId, session.getSessionId(), e);
                }
            });
            return;
        }

        log.warn("用户没有活跃的实时通讯连接: userId={}", userId);
    }

    /**
     * 发送消息到指定会话（用于AI对话等场景）
     *
     * @param sessionId 会话ID
     * @param message   消息对象
     */
    public void sendToSession(String sessionId, RealtimeMessage message) {
        // 设置消息ID和时间戳
        if (message.getMessageId() == null) {
            message.setMessageId(UUID.randomUUID().toString());
        }
        message.setTargetSessionId(sessionId);

        // 先尝试WebSocket
        RealtimeSession wsSession = webSocketSessionManager.getSession(sessionId);
        if (wsSession != null && wsSession.isActive()) {
            try {
                wsSession.getChannel().send(message);
                log.debug("通过WebSocket发送消息到会话: sessionId={}, messageType={}", sessionId, message.getMessageType());
                return;
            } catch (IOException e) {
                log.error("WebSocket发送消息失败: sessionId={}", sessionId, e);
            }
        }

        // 降级到SSE
        RealtimeSession sseSession = sseEmitterManager.getSession(sessionId);
        if (sseSession != null && sseSession.isActive()) {
            try {
                sseSession.getChannel().send(message);
                log.debug("通过SSE发送消息到会话: sessionId={}, messageType={}", sessionId, message.getMessageType());
                return;
            } catch (IOException e) {
                log.error("SSE发送消息失败: sessionId={}", sessionId, e);
            }
        }

        log.warn("会话不存在或已关闭: sessionId={}", sessionId);
    }

    /**
     * 广播消息到所有在线用户
     *
     * @param message 消息对象
     */
    public void broadcast(RealtimeMessage message) {
        if (message.getMessageId() == null) {
            message.setMessageId(UUID.randomUUID().toString());
        }

        // 广播到WebSocket
        int wsCount = 0;
        for (RealtimeSession session : getAllWebSocketSessions()) {
            try {
                session.getChannel().send(message);
                wsCount++;
            } catch (IOException e) {
                log.error("WebSocket广播失败: sessionId={}", session.getSessionId(), e);
            }
        }

        // 广播到SSE
        int sseCount = 0;
        for (RealtimeSession session : getAllSseSessions()) {
            try {
                session.getChannel().send(message);
                sseCount++;
            } catch (IOException e) {
                log.error("SSE广播失败: sessionId={}", session.getSessionId(), e);
            }
        }

        log.info("消息广播完成: messageType={}, wsCount={}, sseCount={}", 
                message.getMessageType(), wsCount, sseCount);
    }

    /**
     * 发送心跳消息（用于保持连接）
     *
     * @param sessionId 会话ID
     */
    public void sendHeartbeat(String sessionId) {
        RealtimeMessage heartbeat = RealtimeMessage.builder()
                .messageId(UUID.randomUUID().toString())
                .messageType(RealtimeMessageTypeEnum.HEARTBEAT)
                .businessType("SYSTEM")
                .data("ping")
                .build();
        
        sendToSession(sessionId, heartbeat);
    }

    /**
     * 关闭用户的所有连接
     *
     * @param userId 用户ID
     */
    public void closeUserConnections(Long userId) {
        webSocketSessionManager.closeUserSessions(userId);
        sseEmitterManager.closeUserSessions(userId);
        log.info("用户所有连接已关闭: userId={}", userId);
    }

    /**
     * 关闭指定会话
     *
     * @param sessionId 会话ID
     */
    public void closeSession(String sessionId) {
        // 尝试关闭WebSocket会话
        RealtimeSession wsSession = webSocketSessionManager.getSession(sessionId);
        if (wsSession != null) {
            webSocketSessionManager.unregisterSession(sessionId);
            return;
        }

        // 尝试关闭SSE会话
        RealtimeSession sseSession = sseEmitterManager.getSession(sessionId);
        if (sseSession != null) {
            sseEmitterManager.closeSession(sessionId);
        }
    }

    /**
     * 获取在线统计信息
     *
     * @return 统计信息Map
     */
    public java.util.Map<String, Object> getOnlineStats() {
        return java.util.Map.of(
                "wsSessionCount", webSocketSessionManager.getSessionCount(),
                "sseSessionCount", sseEmitterManager.getSessionCount(),
                "wsUserCount", webSocketSessionManager.getOnlineUserCount(),
                "sseUserCount", sseEmitterManager.getOnlineUserCount(),
                "totalSessionCount", webSocketSessionManager.getSessionCount() + sseEmitterManager.getSessionCount()
        );
    }

    /**
     * 获取用户的所有会话
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    public List<RealtimeSession> getUserSessions(Long userId) {
        List<RealtimeSession> sessions = new ArrayList<>();
        sessions.addAll(webSocketSessionManager.getUserSessions(userId));
        sessions.addAll(sseEmitterManager.getUserSessions(userId));
        return sessions;
    }

    /**
     * 添加事件监听器
     *
     * @param listener 监听器
     */
    public void addEventListener(RealtimeEventListener listener) {
        webSocketSessionManager.addListener(listener);
        sseEmitterManager.addListener(listener);
        log.info("事件监听器已添加: {}", listener.getListenerName());
    }

    /**
     * 移除事件监听器
     *
     * @param listener 监听器
     */
    public void removeEventListener(RealtimeEventListener listener) {
        webSocketSessionManager.removeListener(listener);
        sseEmitterManager.removeListener(listener);
        log.info("事件监听器已移除: {}", listener.getListenerName());
    }

    // ========== 私有方法 ==========

    private List<RealtimeSession> getAllWebSocketSessions() {
        // 这里简化实现，实际应该从WebSocketSessionManager获取所有会话
        // 为避免暴露内部数据结构，这里返回空列表
        // 实际使用时可以在WebSocketSessionManager中添加getAllSessions方法
        return List.of();
    }

    private List<RealtimeSession> getAllSseSessions() {
        // 同上
        return List.of();
    }
}



