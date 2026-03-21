package net.lab1024.sa.admin.module.system.fieldpermission.constant;

import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 字段脱敏类型枚举
 *
 * @Author
 * @Date 2025-11-27
 * @Wechat
 * @Email
 * @Copyright
 */
public enum FieldMaskTypeEnum implements BaseEnum {

    /**
     * 手机号脱敏：138****5678
     */
    MOBILE(1, "手机号脱敏"),

    /**
     * 身份证号脱敏：110101****1234
     */
    ID_CARD(2, "身份证脱敏"),

    /**
     * 银行卡号脱敏：6217 **** **** 1234
     */
    BANK_CARD(3, "银行卡脱敏"),

    /**
     * 邮箱脱敏：abc***@example.com
     */
    EMAIL(4, "邮箱脱敏"),

    /**
     * 姓名脱敏：张*，欧阳**
     */
    NAME(5, "姓名脱敏"),

    /**
     * 地址脱敏：北京市朝阳区******
     */
    ADDRESS(6, "地址脱敏"),

    /**
     * 自定义脱敏：保留前后各N位，中间用*代替
     */
    CUSTOM(7, "自定义脱敏"),
    ;

    private final Integer value;
    private final String desc;

    FieldMaskTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}

