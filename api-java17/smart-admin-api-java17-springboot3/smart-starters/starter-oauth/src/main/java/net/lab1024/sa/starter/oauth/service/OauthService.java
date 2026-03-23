package net.lab1024.sa.starter.oauth.service;

import net.lab1024.sa.starter.oauth.domain.OauthUserInfo;

/**
 * 第三方登录服务统一抽象
 */
public interface OauthService {

    /**
     * 生成授权跳转 URL
     *
     * @param provider 平台标识：wechat / dingtalk / wecom
     * @param state    防 CSRF 随机串，建议存 Redis 校验
     */
    String buildAuthUrl(String provider, String state);

    /**
     * 用授权码换取用户信息
     *
     * @param provider 平台标识
     * @param code     OAuth 授权码
     * @param state    回调携带的 state，需与发起时一致
     */
    OauthUserInfo getUserInfo(String provider, String code, String state);
}
