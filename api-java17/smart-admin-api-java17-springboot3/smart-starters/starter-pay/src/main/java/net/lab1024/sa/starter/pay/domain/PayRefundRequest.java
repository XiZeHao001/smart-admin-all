package net.lab1024.sa.starter.pay.domain;

import lombok.Data;

import java.math.BigDecimal;

/** 退款请求 */
@Data
public class PayRefundRequest {

    /** 原业务订单号 */
    private String outTradeNo;

    /** 退款单号（唯一） */
    private String outRefundNo;

    /** 退款金额（元） */
    private BigDecimal refundAmount;

    /** 退款原因 */
    private String reason;
}
