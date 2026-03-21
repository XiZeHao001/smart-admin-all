package net.lab1024.sa.admin.module.system.tablepermission.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 表权限保存表单
 *
 * @Author xzh
 * @Date 2025-11-28
 */
@Data
@Schema(description = "表权限保存表单")
public class TablePermissionSaveForm {

    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "模块代码")
    @NotBlank(message = "模块代码不能为空")
    private String moduleCode;

    @Schema(description = "查看权限")
    @NotNull(message = "查看权限不能为空")
    private Boolean canView;

    @Schema(description = "新增权限")
    @NotNull(message = "新增权限不能为空")
    private Boolean canAdd;

    @Schema(description = "编辑权限")
    @NotNull(message = "编辑权限不能为空")
    private Boolean canEdit;

    @Schema(description = "删除权限")
    @NotNull(message = "删除权限不能为空")
    private Boolean canDelete;

    @Schema(description = "导出权限")
    @NotNull(message = "导出权限不能为空")
    private Boolean canExport;

    @Schema(description = "导入权限")
    @NotNull(message = "导入权限不能为空")
    private Boolean canImport;

    @Schema(description = "打印权限")
    @NotNull(message = "打印权限不能为空")
    private Boolean canPrint;

    @Schema(description = "备注")
    private String remark;
}








