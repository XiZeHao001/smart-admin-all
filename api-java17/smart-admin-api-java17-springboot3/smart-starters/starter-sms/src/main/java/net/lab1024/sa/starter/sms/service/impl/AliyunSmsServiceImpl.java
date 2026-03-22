package net.lab1024.sa.starter.sms.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.starter.sms.SmsProperties;
import net.lab1024.sa.starter.sms.service.SmsService;

/**
 * 阿里云短信实现
 * <p>
 * 依赖：com.aliyun:dysmsapi20170525
 * TODO: 引入 AliyunSmsClient 完成真实调用
 * </p>
 */
@Slf4j
public class AliyunSmsServiceImpl implements SmsService {

    private final SmsProperties properties;

    public AliyunSmsServiceImpl(SmsProperties properties) {
        this.properties = properties;
        log.info("[SmartStarter-SMS] 使用阿里云短信，SignName={}", properties.getAliyun().getSignName());
    }

    @Override
    public void sendVerifyCode(String phone, String code, int expireMin) {
        // TODO: 使用 AliyunSmsClient 调用 SendSms 接口
        // 模板参数：{"code":"xxxx","minute":"5"}
        sendTemplate(phone, "SMS_VERIFY_CODE_TEMPLATE", code, String.valueOf(expireMin));
    }

    @Override
    public void sendTemplate(String phone, String templateCode, String... params) {
        // TODO: 构建阿里云 SendSmsRequest 并调用
        log.info("[AliyunSMS] phone={}, templateCode={}, params={}", phone, templateCode, params);
        throw new UnsupportedOperationException(
            "请在 AliyunSmsServiceImpl 中接入阿里云 SDK，参考: https://help.aliyun.com/document_detail/419273.html"
        );
    }
}
