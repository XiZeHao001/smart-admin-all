package net.lab1024.sa.starter.sms.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.starter.sms.SmsProperties;
import net.lab1024.sa.starter.sms.service.SmsService;

/**
 * 腾讯云短信实现
 * <p>
 * 依赖：com.tencentcloudapi:tencentcloud-sdk-java-sms
 * TODO: 引入 TencentSmsClient 完成真实调用
 * </p>
 */
@Slf4j
public class TencentSmsServiceImpl implements SmsService {

    private final SmsProperties properties;

    public TencentSmsServiceImpl(SmsProperties properties) {
        this.properties = properties;
        log.info("[SmartStarter-SMS] 使用腾讯云短信，SdkAppId={}", properties.getTencent().getSdkAppId());
    }

    @Override
    public void sendVerifyCode(String phone, String code, int expireMin) {
        sendTemplate(phone, "SMS_VERIFY_CODE_TEMPLATE", code, String.valueOf(expireMin));
    }

    @Override
    public void sendTemplate(String phone, String templateCode, String... params) {
        // TODO: 构建腾讯云 SendSmsRequest 并调用
        log.info("[TencentSMS] phone={}, templateCode={}, params={}", phone, templateCode, params);
        throw new UnsupportedOperationException(
            "请在 TencentSmsServiceImpl 中接入腾讯云 SDK，参考: https://cloud.tencent.com/document/product/382/43196"
        );
    }
}
