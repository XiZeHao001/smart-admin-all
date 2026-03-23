package net.lab1024.sa.starter.pay.domain;

import lombok.Data;

/** 统一下单结果 */
@Data
public class PayOrderResult {

    /** 预支付 ID（微信）或支付 URL（支付宝） */
    private String prepayId;

    /** 拉起支付的完整参数（JSON，前端直接使用） */
    private String payParams;

    /** 二维码链接（Native 支付） */
    private String codeUrl;
}
