package net.lab1024.sa.starter.pay.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.starter.pay.PayProperties;
import net.lab1024.sa.starter.pay.domain.PayOrderRequest;
import net.lab1024.sa.starter.pay.domain.PayOrderResult;
import net.lab1024.sa.starter.pay.domain.PayRefundRequest;
import net.lab1024.sa.starter.pay.domain.PayRefundResult;
import net.lab1024.sa.starter.pay.service.PayService;

/**
 * 支付宝实现
 * <p>
 * 依赖：com.alipay.sdk:alipay-sdk-java
 * TODO: 引入 AlipayClient 完成 PC / H5 / App 支付
 * 参考：https://opendocs.alipay.com/open/270/105898
 * </p>
 */
@Slf4j
public class AlipayServiceImpl implements PayService {

    private final PayProperties.Alipay config;

    public AlipayServiceImpl(PayProperties.Alipay config) {
        this.config = config;
        log.info("[SmartStarter-Pay] 支付宝已启用，AppId={}, sandbox={}", config.getAppId(), config.isSandbox());
    }

    @Override
    public PayOrderResult createOrder(PayOrderRequest request) {
        // TODO: 调用 alipay.trade.page.pay / alipay.trade.wap.pay / alipay.trade.app.pay
        throw new UnsupportedOperationException("请实现支付宝统一下单");
    }

    @Override
    public PayRefundResult refund(PayRefundRequest request) {
        // TODO: 调用 alipay.trade.refund
        throw new UnsupportedOperationException("请实现支付宝退款");
    }

    @Override
    public String parseNotify(String notifyBody) {
        // TODO: 验签回调报文，返回 out_trade_no
        throw new UnsupportedOperationException("请实现支付宝回调解析");
    }
}
