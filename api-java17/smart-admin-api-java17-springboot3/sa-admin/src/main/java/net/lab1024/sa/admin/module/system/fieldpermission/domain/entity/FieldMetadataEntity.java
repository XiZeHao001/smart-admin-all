package net.lab1024.sa.admin.module.system.fieldpermission.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字段元数据实体
 * 用于存储可配置的字段信息
 *
 * @Author 1024创新实验室
 * @Date 2025-11-27
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Data
@TableName("t_field_metadata")
public class FieldMetadataEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模块代码
     */
    private String moduleCode;

    /**
     * 字段名称（Java字段名）
     */
    private String fieldName;

    /**
     * 字段显示名称
     */
    private String fieldLabel;

    /**
     * 字段类型（String/Integer/BigDecimal等）
     */
    private String fieldType;

    /**
     * 是否敏感字段
     */
    private Boolean isSensitive;

    /**
     * 默认脱敏类型
     * {@link net.lab1024.sa.admin.module.system.fieldpermission.constant.FieldMaskTypeEnum}
     */
    private Integer defaultMaskType;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否启用
     */
    private Boolean enabledFlag;

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
}

