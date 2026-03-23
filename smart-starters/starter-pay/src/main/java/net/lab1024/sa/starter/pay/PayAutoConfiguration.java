package net.lab1024.sa.starter.pay;

import net.lab1024.sa.starter.pay.service.PayService;
import net.lab1024.sa.starter.pay.service.impl.AlipayServiceImpl;
import net.lab1024.sa.starter.pay.service.impl.WechatPayServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付自动配置
 * <p>触发条件：{@code smart.pay.enabled=true}</p>
 */
@Configuration
@EnableConfigurationProperties(PayProperties.class)
@ConditionalOnProperty(prefix = "smart.pay", name = "enabled", havingValue = "true")
public class PayAutoConfiguration {

    /**
     * 微信支付 Service
     * 当 spring.pay.wechat.app-id 不为空时自动注册
     */
    @Bean("wechatPayService")
    @ConditionalOnMissingBean(name = "wechatPayService")
    @ConditionalOnProperty(prefix = "smart.pay.wechat", name = "app-id")
    public PayService wechatPayService(PayProperties properties) {
        return new WechatPayServiceImpl(properties.getWechat());
    }

    /**
     * 支付宝 Service
     * 当 spring.pay.alipay.app-id 不为空时自动注册
     */
    @Bean("alipayService")
    @ConditionalOnMissingBean(name = "alipayService")
    @ConditionalOnProperty(prefix = "smart.pay.alipay", name = "app-id")
    public PayService alipayService(PayProperties properties) {
        return new AlipayServiceImpl(properties.getAlipay());
    }
}
