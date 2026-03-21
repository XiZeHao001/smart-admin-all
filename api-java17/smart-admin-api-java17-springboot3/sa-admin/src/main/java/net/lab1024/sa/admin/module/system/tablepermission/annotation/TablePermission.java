package net.lab1024.sa.admin.module.system.tablepermission.annotation;

import net.lab1024.sa.admin.module.system.tablepermission.constant.TableOperationEnum;

import java.lang.annotation.*;

/**
 * 表权限注解
 * 用于Controller方法上，标记需要检查的表操作权限
 *
 * 使用示例：
 * <pre>
 * @TablePermission(module = "employee", operation = TableOperationEnum.ADD)
 * public ResponseDTO<String> addEmployee(EmployeeAddForm form) {
 *     // 业务逻辑
 * }
 * </pre>
 *
 * @Author xzh
 * @Date 2025-11-28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TablePermission {

    /**
     * 模块代码（必填）
     * 例如: "employee", "enterprise", "goods"
     */
    String module();

    /**
     * 操作类型（必填）
     * 例如: TableOperationEnum.VIEW, TableOperationEnum.ADD
     */
    TableOperationEnum operation();

    /**
     * 无权限时的提示信息（可选）
     * 如果不设置，使用默认提示
     */
    String message() default "";
}








