package net.lab1024.sa.starter.sms;

import net.lab1024.sa.starter.sms.service.SmsService;
import net.lab1024.sa.starter.sms.service.impl.AliyunSmsServiceImpl;
import net.lab1024.sa.starter.sms.service.impl.TencentSmsServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信自动配置
 * <p>
 * 触发条件：{@code smart.sms.enabled=true}
 * 若业务方已自定义 {@link SmsService} Bean，则本配置不生效（{@code @ConditionalOnMissingBean}）。
 * </p>
 */
@Configuration
@EnableConfigurationProperties(SmsProperties.class)
@ConditionalOnProperty(prefix = "smart.sms", name = "enabled", havingValue = "true")
public class SmsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SmsService.class)
    @ConditionalOnProperty(prefix = "smart.sms", name = "provider", havingValue = "aliyun", matchIfMissing = true)
    public SmsService aliyunSmsService(SmsProperties properties) {
        return new AliyunSmsServiceImpl(properties);
    }

    @Bean
    @ConditionalOnMissingBean(SmsService.class)
    @ConditionalOnProperty(prefix = "smart.sms", name = "provider", havingValue = "tencent")
    public SmsService tencentSmsService(SmsProperties properties) {
        return new TencentSmsServiceImpl(properties);
    }
}
