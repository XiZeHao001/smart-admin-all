package net.lab1024.sa.base.module.support.realtime.websocket;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.module.support.realtime.constant.RealtimeChannelTypeEnum;
import net.lab1024.sa.base.module.support.realtime.core.RealtimeChannel;
import net.lab1024.sa.base.module.support.realtime.domain.RealtimeMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * WebSocket通道实现
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Slf4j
@Getter
public class WebSocketChannel implements RealtimeChannel {

    /**
     * 会话ID
     */
    private final String sessionId;

    /**
     * 用户ID
     */
    private final Long userId;

    /**
     * 业务类型
     */
    private final String businessType;

    /**
     * WebSocketSession对象
     */
    private final WebSocketSession session;

    /**
     * 是否已关闭
     */
    private volatile boolean closed = false;

    public WebSocketChannel(String sessionId, Long userId, String businessType, WebSocketSession session) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.businessType = businessType;
        this.session = session;
    }

    @Override
    public void send(RealtimeMessage message) throws IOException {
        if (closed || !session.isOpen()) {
            throw new IOException("WebSocket通道已关闭");
        }

        try {
            // 转换为JSON并发送
            String jsonData = JSON.toJSONString(message);
            session.sendMessage(new TextMessage(jsonData));
            
            log.debug("WebSocket消息发送成功: sessionId={}, messageType={}", sessionId, message.getMessageType());
        } catch (IOException e) {
            log.error("WebSocket消息发送失败: sessionId={}, error={}", sessionId, e.getMessage());
            this.close();
            throw e;
        }
    }

    @Override
    public boolean isActive() {
        return !closed && session.isOpen();
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            try {
                if (session.isOpen()) {
                    session.close();
                }
                log.info("WebSocket通道关闭: sessionId={}, userId={}", sessionId, userId);
            } catch (Exception e) {
                log.warn("WebSocket通道关闭异常: sessionId={}, error={}", sessionId, e.getMessage());
            }
        }
    }

    @Override
    public RealtimeChannelTypeEnum getChannelType() {
        return RealtimeChannelTypeEnum.WEBSOCKET;
    }
}



