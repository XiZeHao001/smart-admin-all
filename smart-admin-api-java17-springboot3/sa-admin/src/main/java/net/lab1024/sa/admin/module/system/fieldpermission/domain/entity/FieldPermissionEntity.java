package net.lab1024.sa.admin.module.system.fieldpermission.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字段权限配置实体
 *
 * @Author
 * @Date 2025-11-27
 * @Wechat zhuoda1024
 * @Email
 * @Copyright
 */
@Data
@TableName("t_field_permission")
public class FieldPermissionEntity {

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
     * 模块代码（enterprise/employee/invoice等）
     */
    private String moduleCode;

    /**
     * 字段名称（如salary/phone/idCard）
     */
    private String fieldName;

    /**
     * 权限类型：1=可见，2=隐藏，3=脱敏，4=只读，5=可编辑
     * {@link net.lab1024.sa.admin.module.system.fieldpermission.constant.FieldPermissionTypeEnum}
     */
    private Integer permissionType;

    /**
     * 脱敏类型：1=手机号，2=身份证，3=银行卡，4=邮箱，5=姓名，6=地址，7=自定义
     * {@link net.lab1024.sa.admin.module.system.fieldpermission.constant.FieldMaskTypeEnum}
     */
    private Integer maskType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

