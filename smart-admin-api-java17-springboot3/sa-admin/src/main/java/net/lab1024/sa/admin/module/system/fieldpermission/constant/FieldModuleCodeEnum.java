package net.lab1024.sa.admin.module.system.fieldpermission.constant;

import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 字段权限模块代码枚举
 * 定义哪些业务模块支持字段权限控制
 *
 * @Author
 * @Date 2025-11-27
 * @Wechat
 * @Email
 * @Copyright
 */
public enum FieldModuleCodeEnum implements BaseEnum {

    /**
     * 员工管理模块
     */
    EMPLOYEE("employee", "员工管理"),

    /**
     * 企业管理模块
     */
    ENTERPRISE("enterprise", "企业管理"),

    /**
     * 发票管理模块
     */
    INVOICE("invoice", "发票管理"),

    /**
     * 银行账户模块
     */
    BANK("bank", "银行账户"),

    /**
     * 订单管理模块
     */
    ORDER("order", "订单管理"),

    /**
     * 商品管理模块
     */
    GOODS("goods", "商品管理"),

    /**
     * 客户管理模块
     */
    CUSTOMER("customer", "客户管理"),
    ;

    private final String code;
    private final String desc;

    FieldModuleCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取模块代码
     */
    public String getCode() {
        return code;
    }

    @Override
    public Integer getValue() {
        return this.ordinal();
    }

    @Override
    public String getDesc() {
        return desc;
    }

    /**
     * 根据code获取枚举
     */
    public static FieldModuleCodeEnum getByCode(String code) {
        for (FieldModuleCodeEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}

