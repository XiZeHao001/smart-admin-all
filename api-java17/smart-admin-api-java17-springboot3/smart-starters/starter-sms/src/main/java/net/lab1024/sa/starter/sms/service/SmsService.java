package net.lab1024.sa.starter.sms.service;

/**
 * 短信服务统一抽象
 * <p>不同服务商实现此接口，业务层只依赖此接口，切换服务商零改动。</p>
 */
public interface SmsService {

    /**
     * 发送验证码
     *
     * @param phone      手机号
     * @param code       验证码
     * @param expireMin  有效分钟数
     */
    void sendVerifyCode(String phone, String code, int expireMin);

    /**
     * 发送模板短信
     *
     * @param phone        手机号
     * @param templateCode 模板 Code
     * @param params       模板参数（按顺序）
     */
    void sendTemplate(String phone, String templateCode, String... params);
}
