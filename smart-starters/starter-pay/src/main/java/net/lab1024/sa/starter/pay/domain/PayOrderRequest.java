package net.lab1024.sa.starter.pay.domain;

import lombok.Data;

import java.math.BigDecimal;

/** 统一下单请求 */
@Data
public class PayOrderRequest {

    /** 业务订单号（唯一） */
    private String outTradeNo;

    /** 支付金额（元） */
    private BigDecimal amount;

    /** 商品描述 */
    private String description;

    /** 支付方式：wechat_jsapi / wechat_native / wechat_app / alipay_page / alipay_app */
    private String payType;

    /** 微信 JSAPI 支付时需要 openId */
    private String openId;
}
