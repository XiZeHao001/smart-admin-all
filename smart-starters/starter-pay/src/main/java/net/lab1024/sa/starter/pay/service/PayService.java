package net.lab1024.sa.starter.pay.service;

import net.lab1024.sa.starter.pay.domain.PayOrderRequest;
import net.lab1024.sa.starter.pay.domain.PayOrderResult;
import net.lab1024.sa.starter.pay.domain.PayRefundRequest;
import net.lab1024.sa.starter.pay.domain.PayRefundResult;

/**
 * 支付服务统一抽象
 */
public interface PayService {

    /**
     * 统一下单，返回拉起支付所需参数（prepayId / payUrl 等）
     */
    PayOrderResult createOrder(PayOrderRequest request);

    /**
     * 申请退款
     */
    PayRefundResult refund(PayRefundRequest request);

    /**
     * 验证并解析回调通知，返回业务订单号
     *
     * @param notifyBody 原始回调报文
     * @return 业务订单号
     */
    String parseNotify(String notifyBody);
}
