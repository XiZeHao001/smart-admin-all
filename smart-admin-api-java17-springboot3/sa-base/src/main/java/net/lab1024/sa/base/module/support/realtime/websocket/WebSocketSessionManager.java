package net.lab1024.sa.base.module.support.realtime.websocket;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.module.support.realtime.core.RealtimeEventListener;
import net.lab1024.sa.base.module.support.realtime.core.RealtimeSession;
import net.lab1024.sa.base.module.support.realtime.domain.RealtimeMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocket会话管理器
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Component
public class WebSocketSessionManager {

    /**
     * 存储所有WebSocket会话: sessionId -> RealtimeSession
     */
    private final Map<String, RealtimeSession> sessions = new ConcurrentHashMap<>();

    /**
     * 用户ID到会话ID的映射（一个用户可能有多个会话）
     */
    private final Map<Long, List<String>> userSessions = new ConcurrentHashMap<>();

    /**
     * 事件监听器列表
     */
    private final List<RealtimeEventListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * 注册WebSocket会话
     *
     * @param userId          用户ID
     * @param businessType    业务类型
     * @param webSocketSession WebSocket会话对象
     */
    public void registerSession(Long userId, String businessType, WebSocketSession webSocketSession) {
        String sessionId = webSocketSession.getId();

        // 创建WebSocket通道
        WebSocketChannel channel = new WebSocketChannel(sessionId, userId, businessType, webSocketSession);

        // 创建会话
        RealtimeSession session = RealtimeSession.builder()
                .sessionId(sessionId)
                .userId(userId)
                .businessType(businessType)
                .channel(channel)
                .build();

        // 保存会话
        sessions.put(sessionId, session);
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(sessionId);

        // 触发连接事件
        notifyConnect(session);

        log.info("WebSocket会话注册成功: sessionId={}, userId={}, businessType={}", sessionId, userId, businessType);
    }

    /**
     * 注销WebSocket会话
     *
     * @param sessionId 会话ID
     */
    public void unregisterSession(String sessionId) {
        RealtimeSession session = sessions.remove(sessionId);
        if (session != null) {
            // 从用户会话映射中移除
            List<String> userSessionList = userSessions.get(session.getUserId());
            if (userSessionList != null) {
                userSessionList.remove(sessionId);
                if (userSessionList.isEmpty()) {
                    userSessions.remove(session.getUserId());
                }
            }

            // 关闭通道
            if (session.getChannel() != null) {
                session.getChannel().close();
            }

            // 触发断开事件
            notifyDisconnect(session);

            log.info("WebSocket会话已注销: sessionId={}, userId={}", sessionId, session.getUserId());
        }
    }

    /**
     * 获取会话
     *
     * @param sessionId 会话ID
     * @return 会话对象
     */
    public RealtimeSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * 获取用户的所有会话
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    public List<RealtimeSession> getUserSessions(Long userId) {
        List<String> sessionIds = userSessions.get(userId);
        if (sessionIds == null) {
            return List.of();
        }
        return sessionIds.stream()
                .map(sessions::get)
                .filter(session -> session != null && session.isActive())
                .toList();
    }

    /**
     * 处理接收到的消息
     *
     * @param sessionId 会话ID
     * @param message   消息对象
     */
    public void handleMessage(String sessionId, RealtimeMessage message) {
        RealtimeSession session = sessions.get(sessionId);
        if (session != null) {
            session.updateLastActiveTime();
            notifyMessageReceived(session, message);
        }
    }

    /**
     * 处理错误
     *
     * @param sessionId 会话ID
     * @param error     异常对象
     */
    public void handleError(String sessionId, Throwable error) {
        RealtimeSession session = sessions.get(sessionId);
        if (session != null) {
            notifyError(session, error);
        }
        unregisterSession(sessionId);
    }

    /**
     * 关闭用户的所有会话
     *
     * @param userId 用户ID
     */
    public void closeUserSessions(Long userId) {
        List<String> sessionIds = userSessions.remove(userId);
        if (sessionIds != null) {
            sessionIds.forEach(this::unregisterSession);
        }
    }

    /**
     * 获取在线会话数
     *
     * @return 会话数量
     */
    public int getSessionCount() {
        return sessions.size();
    }

    /**
     * 获取在线用户数
     *
     * @return 用户数量
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }

    /**
     * 添加事件监听器
     *
     * @param listener 监听器
     */
    public void addListener(RealtimeEventListener listener) {
        this.listeners.add(listener);
    }

    /**
     * 移除事件监听器
     *
     * @param listener 监听器
     */
    public void removeListener(RealtimeEventListener listener) {
        this.listeners.remove(listener);
    }

    // ========== 私有方法 ==========

    private void notifyConnect(RealtimeSession session) {
        listeners.forEach(listener -> {
            try {
                listener.onConnect(session);
            } catch (Exception e) {
                log.error("事件监听器执行失败: listener={}, event=onConnect", listener.getListenerName(), e);
            }
        });
    }

    private void notifyDisconnect(RealtimeSession session) {
        listeners.forEach(listener -> {
            try {
                listener.onDisconnect(session);
            } catch (Exception e) {
                log.error("事件监听器执行失败: listener={}, event=onDisconnect", listener.getListenerName(), e);
            }
        });
    }

    private void notifyMessageReceived(RealtimeSession session, RealtimeMessage message) {
        listeners.forEach(listener -> {
            try {
                listener.onMessageReceived(session, message);
            } catch (Exception e) {
                log.error("事件监听器执行失败: listener={}, event=onMessageReceived", listener.getListenerName(), e);
            }
        });
    }

    private void notifyError(RealtimeSession session, Throwable error) {
        listeners.forEach(listener -> {
            try {
                listener.onError(session, error);
            } catch (Exception e) {
                log.error("事件监听器执行失败: listener={}, event=onError", listener.getListenerName(), e);
            }
        });
    }
}



