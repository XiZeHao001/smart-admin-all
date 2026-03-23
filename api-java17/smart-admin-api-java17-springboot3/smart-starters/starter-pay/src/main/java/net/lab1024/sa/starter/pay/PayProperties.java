package net.lab1024.sa.starter.pay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 支付配置
 * <pre>
 * smart:
 *   pay:
 *     enabled: true
 *     wechat:
 *       app-id: wx...
 *       mch-id: 1234567890
 *       private-key-path: classpath:cert/apiclient_key.pem
 *       mch-serial-no: xxx
 *       api-v3-key: xxx
 *       notify-url: https://your-domain/pay/wechat/notify
 *     alipay:
 *       app-id: 2021...
 *       private-key: xxx
 *       alipay-public-key: xxx
 *       notify-url: https://your-domain/pay/alipay/notify
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "smart.pay")
public class PayProperties {

    private boolean enabled = false;

    private Wechat wechat = new Wechat();
    private Alipay alipay = new Alipay();

    @Data
    public static class Wechat {
        private String appId;
        private String mchId;
        /** 商户私钥文件路径，支持 classpath: 前缀 */
        private String privateKeyPath;
        private String mchSerialNo;
        private String apiV3Key;
        private String notifyUrl;
    }

    @Data
    public static class Alipay {
        private String appId;
        private String privateKey;
        private String alipayPublicKey;
        private String notifyUrl;
        /** 沙箱模式 */
        private boolean sandbox = false;
    }
}
