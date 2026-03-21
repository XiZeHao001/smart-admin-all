package net.lab1024.sa.base.module.support.realtime.sse;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.module.support.realtime.core.RealtimeEventListener;
import net.lab1024.sa.base.module.support.realtime.core.RealtimeSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SSE连接管理器
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Component
public class SseEmitterManager {

    /**
     * 默认超时时间: 30分钟
     */
    private static final long DEFAULT_TIMEOUT = 30 * 60 * 1000L;

    /**
     * 存储所有SSE会话: sessionId -> RealtimeSession
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
     * 创建SSE连接
     *
     * @param userId       用户ID
     * @param businessType 业务类型
     * @return SseEmitter对象
     */
    public SseEmitter createConnection(Long userId, String businessType) {
        return createConnection(userId, businessType, DEFAULT_TIMEOUT);
    }

    /**
     * 创建SSE连接
     *
     * @param userId       用户ID
     * @param businessType 业务类型
     * @param timeout      超时时间（毫秒）
     * @return SseEmitter对象
     */
    public SseEmitter createConnection(Long userId, String businessType, long timeout) {
        String sessionId = UUID.randomUUID().toString();
        SseEmitter emitter = new SseEmitter(timeout);

        // 创建SSE通道
        SseChannel channel = new SseChannel(sessionId, userId, businessType, emitter);

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

        // 设置回调
        emitter.onCompletion(() -> handleCompletion(sessionId));
        emitter.onTimeout(() -> handleTimeout(sessionId));
        emitter.onError(throwable -> handleError(sessionId, throwable));

        // 触发连接事件
        notifyConnect(session);

        log.info("SSE连接创建成功: sessionId={}, userId={}, businessType={}", sessionId, userId, businessType);

        // 发送连接成功消息
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("{\"message\":\"连接成功\",\"sessionId\":\"" + sessionId + "\"}")
                    .build());
        } catch (IOException e) {
            log.error("发送连接成功消息失败: sessionId={}", sessionId, e);
        }

        return emitter;
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
     * 关闭指定会话
     *
     * @param sessionId 会话ID
     */
    public void closeSession(String sessionId) {
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

            log.info("SSE会话已关闭: sessionId={}, userId={}", sessionId, session.getUserId());
        }
    }

    /**
     * 关闭用户的所有会话
     *
     * @param userId 用户ID
     */
    public void closeUserSessions(Long userId) {
        List<String> sessionIds = userSessions.remove(userId);
        if (sessionIds != null) {
            sessionIds.forEach(this::closeSession);
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

    private void handleCompletion(String sessionId) {
        log.info("SSE连接正常关闭: sessionId={}", sessionId);
        closeSession(sessionId);
    }

    private void handleTimeout(String sessionId) {
        log.warn("SSE连接超时: sessionId={}", sessionId);
        closeSession(sessionId);
    }

    private void handleError(String sessionId, Throwable error) {
        log.error("SSE连接错误: sessionId={}, error={}", sessionId, error.getMessage());
        RealtimeSession session = sessions.get(sessionId);
        if (session != null) {
            notifyError(session, error);
        }
        closeSession(sessionId);
    }

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



