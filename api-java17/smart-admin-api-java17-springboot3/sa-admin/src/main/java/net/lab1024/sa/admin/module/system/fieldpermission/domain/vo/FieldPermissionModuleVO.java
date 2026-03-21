package net.lab1024.sa.admin.module.system.fieldpermission.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 模块字段权限配置VO
 * 用于返回某个模块的完整字段权限配置
 *
 * @Author xzh
 * @Date 2025-11-27
 */
@Data
public class FieldPermissionModuleVO {

    /**
     * 模块代码
     */
    private String moduleCode;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 字段权限配置列表
     */
    private List<FieldPermissionConfigVO> fields;
}








