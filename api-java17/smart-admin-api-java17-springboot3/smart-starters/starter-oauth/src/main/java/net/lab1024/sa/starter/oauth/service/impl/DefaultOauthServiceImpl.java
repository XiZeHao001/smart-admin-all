package net.lab1024.sa.starter.oauth.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.starter.oauth.OauthProperties;
import net.lab1024.sa.starter.oauth.domain.OauthUserInfo;
import net.lab1024.sa.starter.oauth.service.OauthService;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 默认 OAuth 实现（空骨架，各平台参考注释实现）
 * <p>
 * 微信：https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html
 * 钉钉：https://open.dingtalk.com/document/orgapp/obtain-identity-credentials
 * 企业微信：https://developer.work.weixin.qq.com/document/path/91335
 * </p>
 */
@Slf4j
public class DefaultOauthServiceImpl implements OauthService {

    private final OauthProperties properties;
    private final WebClient webClient;

    public DefaultOauthServiceImpl(OauthProperties properties, WebClient webClient) {
        this.properties = properties;
        this.webClient = webClient;
        log.info("[SmartStarter-OAuth] 已启用，配置的平台: {}", properties.getProviders().keySet());
    }

    @Override
    public String buildAuthUrl(String provider, String state) {
        var config = getConfig(provider);
        return switch (provider) {
            case "wechat" -> String.format(
                "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=%s#wechat_redirect",
                config.getClientId(), config.getRedirectUri(), state);
            case "dingtalk" -> String.format(
                "https://login.dingtalk.com/oauth2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=openid&state=%s&prompt=consent",
                config.getClientId(), config.getRedirectUri(), state);
            default -> throw new UnsupportedOperationException("暂不支持的 OAuth 平台: " + provider);
        };
    }

    @Override
    public OauthUserInfo getUserInfo(String provider, String code, String state) {
        // TODO: 各平台分别实现 code 换 token，token 换 userInfo
        throw new UnsupportedOperationException("请实现 " + provider + " 的 getUserInfo 逻辑");
    }

    private OauthProperties.ProviderConfig getConfig(String provider) {
        var config = properties.getProviders().get(provider);
        if (config == null) {
            throw new IllegalArgumentException("未配置 OAuth 平台: " + provider);
        }
        return config;
    }
}
