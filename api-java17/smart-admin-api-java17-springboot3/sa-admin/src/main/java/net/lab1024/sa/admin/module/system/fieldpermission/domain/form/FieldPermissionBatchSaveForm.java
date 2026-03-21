package net.lab1024.sa.admin.module.system.fieldpermission.domain.form;

import lombok.Data;
import net.lab1024.sa.admin.module.system.fieldpermission.domain.vo.FieldPermissionConfigVO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 字段权限批量保存表单
 *
 * @Author
 * @Date 2025-11-27
 * @Wechat
 * @Email
 * @Copyright
 */
@Data
public class FieldPermissionBatchSaveForm {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /**
     * 模块代码
     */
    @NotBlank(message = "模块代码不能为空")
    private String moduleCode;

    /**
     * 字段权限配置列表
     */
    @NotEmpty(message = "字段权限配置不能为空")
    private List<FieldPermissionConfigVO> fieldPermissions;
}

