package net.lab1024.sa.starter.oauth.domain;

import lombok.Data;

/** 第三方登录后返回的用户信息（统一结构） */
@Data
public class OauthUserInfo {

    /** 平台标识：wechat / dingtalk / wecom */
    private String provider;

    /** 平台唯一 ID（openId / unionId / userId） */
    private String openId;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatar;

    /** 手机号（部分平台可获取） */
    private String phone;

    /** 邮箱（部分平台可获取） */
    private String email;
}
