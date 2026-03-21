package net.lab1024.sa.base.module.support.realtime.config;

import net.lab1024.sa.base.module.support.realtime.websocket.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import jakarta.annotation.Resource;

/**
 * WebSocket配置
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册WebSocket处理器
        registry.addHandler(webSocketHandler, "/ws/realtime")
                .setAllowedOrigins("*"); // 生产环境应该配置具体的域名
    }
}



