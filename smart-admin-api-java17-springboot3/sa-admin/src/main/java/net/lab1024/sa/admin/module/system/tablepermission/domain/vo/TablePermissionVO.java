package net.lab1024.sa.admin.module.system.tablepermission.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 表权限配置VO
 *
 * @Author xzh
 * @Date 2025-11-28
 */
@Data
@Schema(description = "表权限配置VO")
public class TablePermissionVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "模块代码")
    private String moduleCode;

    @Schema(description = "模块名称")
    private String moduleName;

    @Schema(description = "查看权限")
    private Boolean canView;

    @Schema(description = "新增权限")
    private Boolean canAdd;

    @Schema(description = "编辑权限")
    private Boolean canEdit;

    @Schema(description = "删除权限")
    private Boolean canDelete;

    @Schema(description = "导出权限")
    private Boolean canExport;

    @Schema(description = "导入权限")
    private Boolean canImport;

    @Schema(description = "打印权限")
    private Boolean canPrint;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}








