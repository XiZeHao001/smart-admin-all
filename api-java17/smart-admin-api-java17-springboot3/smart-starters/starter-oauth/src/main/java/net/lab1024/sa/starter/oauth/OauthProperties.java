package net.lab1024.sa.starter.oauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录配置
 * <pre>
 * smart:
 *   oauth:
 *     enabled: true
 *     providers:
 *       wechat:
 *         client-id: wx...
 *         client-secret: xxx
 *         redirect-uri: https://your-domain/oauth/wechat/callback
 *       dingtalk:
 *         client-id: dingxxx
 *         client-secret: xxx
 *         redirect-uri: https://your-domain/oauth/dingtalk/callback
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "smart.oauth")
public class OauthProperties {

    private boolean enabled = false;

    /** key = 平台标识（wechat / dingtalk / wecom），value = 该平台配置 */
    private Map<String, ProviderConfig> providers = new HashMap<>();

    @Data
    public static class ProviderConfig {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        /** 扩展参数，不同平台差异字段放这里 */
        private Map<String, String> extra = new HashMap<>();
    }
}
