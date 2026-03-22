package net.lab1024.sa.starter.pay.domain;

import lombok.Data;

/** 退款结果 */
@Data
public class PayRefundResult {

    /** 退款单号（服务商侧） */
    private String refundId;

    /** 退款状态：SUCCESS / PROCESSING / ABNORMAL */
    private String status;
}
