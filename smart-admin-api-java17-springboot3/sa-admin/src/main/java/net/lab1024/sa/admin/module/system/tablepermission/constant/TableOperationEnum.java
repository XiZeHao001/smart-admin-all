package net.lab1024.sa.admin.module.system.tablepermission.constant;

import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * 表操作类型枚举
 * 定义7种表级操作权限
 *
 * @Author xzh
 * @Date 2025-11-28
 */
public enum TableOperationEnum implements BaseEnum {

    VIEW(1, "查看", "can_view"),
    ADD(2, "新增", "can_add"),
    EDIT(3, "编辑", "can_edit"),
    DELETE(4, "删除", "can_delete"),
    EXPORT(5, "导出", "can_export"),
    IMPORT(6, "导入", "can_import"),
    PRINT(7, "打印", "can_print");

    private final Integer value;
    private final String desc;
    private final String columnName; // 对应数据库字段名

    TableOperationEnum(Integer value, String desc, String columnName) {
        this.value = value;
        this.desc = desc;
        this.columnName = columnName;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public String getColumnName() {
        return columnName;
    }

    /**
     * 根据value获取枚举
     */
    public static TableOperationEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (TableOperationEnum operationEnum : TableOperationEnum.values()) {
            if (operationEnum.getValue().equals(value)) {
                return operationEnum;
            }
        }
        return null;
    }

    /**
     * 根据数据库字段名获取枚举
     */
    public static TableOperationEnum getByColumnName(String columnName) {
        if (columnName == null) {
            return null;
        }
        for (TableOperationEnum operationEnum : TableOperationEnum.values()) {
            if (operationEnum.getColumnName().equals(columnName)) {
                return operationEnum;
            }
        }
        return null;
    }
}








