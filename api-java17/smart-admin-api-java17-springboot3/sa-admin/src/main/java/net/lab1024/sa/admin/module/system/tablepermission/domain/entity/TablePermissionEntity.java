package net.lab1024.sa.admin.module.system.tablepermission.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表权限实体类
 * 对应表 t_table_permission
 *
 * @Author xzh
 * @Date 2025-11-28
 */
@Data
@TableName("t_table_permission")
public class TablePermissionEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 模块编码（如：employee, enterprise）
     */
    private String moduleCode;

    /**
     * 查看权限
     */
    private Boolean canView;

    /**
     * 新增权限
     */
    private Boolean canAdd;

    /**
     * 编辑权限
     */
    private Boolean canEdit;

    /**
     * 删除权限
     */
    private Boolean canDelete;

    /**
     * 导出权限
     */
    private Boolean canExport;

    /**
     * 导入权限
     */
    private Boolean canImport;

    /**
     * 打印权限
     */
    private Boolean canPrint;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;
}








