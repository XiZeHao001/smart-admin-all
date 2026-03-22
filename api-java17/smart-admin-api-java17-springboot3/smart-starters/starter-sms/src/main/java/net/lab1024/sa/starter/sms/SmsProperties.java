package net.lab1024.sa.starter.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 短信配置
 * <pre>
 * smart:
 *   sms:
 *     enabled: true
 *     provider: aliyun   # aliyun | tencent
 *     aliyun:
 *       access-key-id: xxx
 *       access-key-secret: xxx
 *       sign-name: SmartAdmin
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "smart.sms")
public class SmsProperties {

    /** 是否启用 */
    private boolean enabled = false;

    /** 短信服务商：aliyun / tencent */
    private String provider = "aliyun";

    private Aliyun aliyun = new Aliyun();
    private Tencent tencent = new Tencent();

    @Data
    public static class Aliyun {
        private String accessKeyId;
        private String accessKeySecret;
        private String signName;
    }

    @Data
    public static class Tencent {
        private String secretId;
        private String secretKey;
        private String sdkAppId;
        private String signName;
    }
}
