package net.lab1024.sa.admin.module.system.datascope.constant;

import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 数据范围 类型
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/11/28  20:59:17
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
public enum DataScopeTypeEnum implements BaseEnum {

    /**
     * 系统通知
     */
    NOTICE(1, 20, "系统通知", "系统通知数据范围"),
    
    /**
     * 订单管理 - 数据范围控制订单的查看权限
     */
    ORDER(2, 30, "订单管理", "订单数据范围，控制用户可查看的订单范围"),
    
    /**
     * 企业管理 - 数据范围控制企业信息的查看权限
     */
    ENTERPRISE(3, 40, "企业管理", "企业数据范围，控制用户可查看的企业信息范围"),
    
    /**
     * 员工管理 - 数据范围控制员工信息的查看权限
     */
    EMPLOYEE_MANAGEMENT(4, 50, "员工管理", "员工数据范围，控制用户可查看的员工信息范围"),
    
    /**
     * 发票管理 - 数据范围控制发票信息的查看权限
     */
    INVOICE(5, 60, "发票管理", "发票数据范围，控制用户可查看的发票信息范围"),
    
    /**
     * 银行账户 - 数据范围控制银行账户信息的查看权限
     */
    BANK_ACCOUNT(6, 70, "银行账户", "银行账户数据范围，控制用户可查看的银行账户范围"),
    
    /**
     * 商品管理 - 数据范围控制商品信息的查看权限
     */
    GOODS(7, 80, "商品管理", "商品数据范围，控制用户可查看的商品信息范围"),
    
    /**
     * 操作日志 - 数据范围控制操作日志的查看权限
     */
    OPERATE_LOG(8, 90, "操作日志", "操作日志数据范围，控制用户可查看的日志范围"),
    
    /**
     * 登录日志 - 数据范围控制登录日志的查看权限
     */
    LOGIN_LOG(9, 100, "登录日志", "登录日志数据范围，控制用户可查看的登录日志范围"),
    
    /**
     * 数据追踪 - 数据范围控制数据变更记录的查看权限
     */
    DATA_TRACER(10, 110, "数据追踪", "数据追踪范围，控制用户可查看的数据变更记录范围"),
    ;

    private final Integer value;

    private final Integer sort;

    private final String name;

    private final String desc;

    DataScopeTypeEnum(Integer value, Integer sort, String name, String desc) {
        this.value = value;
        this.sort = sort;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public Integer getSort() {
        return sort;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }


}
