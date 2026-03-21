package net.lab1024.sa.admin.module.system.fieldpermission.domain.vo;

import lombok.Data;

/**
 * 字段权限配置VO
 *
 * @Author
 * @Date 2025-11-27
 * @Wechat
 * @Email
 * @Copyright
 */
@Data
public class FieldPermissionConfigVO {

    /**
     * 字段名称（Java字段名）
     */
    private String fieldName;

    /**
     * 字段显示名称
     */
    private String fieldLabel;

    /**
     * 权限类型：1=可见，2=隐藏，3=脱敏，4=只读，5=可编辑
     */
    private Integer permissionType;

    /**
     * 脱敏类型：1=手机号，2=身份证，3=银行卡，4=邮箱，5=姓名，6=地址，7=自定义
     */
    private Integer maskType;

    /**
     * 是否敏感字段
     */
    private Boolean isSensitive;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}

