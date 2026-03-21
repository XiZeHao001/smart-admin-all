package net.lab1024.sa.admin.module.system.fieldpermission.constant;

import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 字段权限类型枚举
 *
 * @Author
 * @Date 2025-11-27
 * @Wechat
 * @Email
 * @Copyright
 */
public enum FieldPermissionTypeEnum implements BaseEnum {

    /**
     * 可见 - 字段正常显示
     */
    VISIBLE(1, "可见"),

    /**
     * 隐藏 - 字段不返回给前端
     */
    HIDDEN(2, "隐藏"),

    /**
     * 脱敏 - 字段部分隐藏（如手机号138****5678）
     */
    MASKED(3, "脱敏"),

    /**
     * 只读 - 可见但不可编辑
     */
    READONLY(4, "只读"),

    /**
     * 可编辑 - 可见且可编辑
     */
    EDITABLE(5, "可编辑"),
    ;

    private final Integer value;
    private final String desc;

    FieldPermissionTypeEnum(Integer value, String desc) {
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

