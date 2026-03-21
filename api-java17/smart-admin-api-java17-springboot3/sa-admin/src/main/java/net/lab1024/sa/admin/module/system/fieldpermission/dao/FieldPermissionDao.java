package net.lab1024.sa.admin.module.system.fieldpermission.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.system.fieldpermission.domain.entity.FieldPermissionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字段权限DAO
 *
 * @Author
 * @Date 2025-11-27
 * @Wechat
 * @Email
 * @Copyright
 */
@Mapper
public interface FieldPermissionDao extends BaseMapper<FieldPermissionEntity> {

    /**
     * 根据角色ID列表查询字段权限配置
     *
     * @param roleIdList 角色ID列表
     * @return 字段权限配置列表
     */
    List<FieldPermissionEntity> selectByRoleIds(@Param("roleIdList") List<Long> roleIdList);

    /**
     * 根据角色ID、模块代码查询字段权限配置
     *
     * @param roleId     角色ID
     * @param moduleCode 模块代码
     * @return 字段权限配置列表
     */
    List<FieldPermissionEntity> selectByRoleAndModule(@Param("roleId") Long roleId, @Param("moduleCode") String moduleCode);

    /**
     * 删除角色的字段权限配置
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入或更新字段权限配置
     *
     * @param fieldPermissionList 字段权限配置列表
     * @return 影响行数
     */
    int batchInsertOrUpdate(@Param("list") List<FieldPermissionEntity> fieldPermissionList);
}

