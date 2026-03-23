package net.lab1024.sa.starter.pay.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.starter.pay.PayProperties;
import net.lab1024.sa.starter.pay.domain.PayOrderRequest;
import net.lab1024.sa.starter.pay.domain.PayOrderResult;
import net.lab1024.sa.starter.pay.domain.PayRefundRequest;
import net.lab1024.sa.starter.pay.domain.PayRefundResult;
import net.lab1024.sa.starter.pay.service.PayService;

/**
 * 微信支付 V3 实现
 * <p>
 * 依赖：com.github.wechatpay-apiv3:wechatpay-apache-httpclient
 * TODO: 引入 WechatPayHttpClientBuilder 完成 JSAPI/Native 支付
 * 参考：https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient
 * </p>
 */
@Slf4j
public class WechatPayServiceImpl implements PayService {

    private final PayProperties.Wechat config;

    public WechatPayServiceImpl(PayProperties.Wechat config) {
        this.config = config;
        log.info("[SmartStarter-Pay] 微信支付已启用，AppId={}", config.getAppId());
    }

    @Override
    public PayOrderResult createOrder(PayOrderRequest request) {
        // TODO: 调用微信支付 V3 统一下单 API
        throw new UnsupportedOperationException("请实现微信支付统一下单");
    }

    @Override
    public PayRefundResult refund(PayRefundRequest request) {
        // TODO: 调用微信支付 V3 退款 API
        throw new UnsupportedOperationException("请实现微信支付退款");
    }

    @Override
    public String parseNotify(String notifyBody) {
        // TODO: 验签 + 解密回调报文
        throw new UnsupportedOperationException("请实现微信支付回调解析");
    }
}
