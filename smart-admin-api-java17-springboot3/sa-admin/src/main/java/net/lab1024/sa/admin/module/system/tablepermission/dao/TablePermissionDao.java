package net.lab1024.sa.admin.module.system.tablepermission.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.system.tablepermission.domain.entity.TablePermissionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表权限DAO
 *
 * @Author xzh
 * @Date 2025-11-28
 */
@Mapper
public interface TablePermissionDao extends BaseMapper<TablePermissionEntity> {

    /**
     * 根据角色ID列表查询表权限
     */
    List<TablePermissionEntity> selectByRoleIds(@Param("roleIdList") List<Long> roleIdList);

    /**
     * 根据角色ID和模块代码查询
     */
    TablePermissionEntity selectByRoleAndModule(@Param("roleId") Long roleId, @Param("moduleCode") String moduleCode);

    /**
     * 根据角色ID删除所有表权限
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入或更新表权限
     */
    int batchInsertOrUpdate(@Param("list") List<TablePermissionEntity> permissionList);

    /**
     * 查询所有表权限配置（包含角色名称）
     */
    List<TablePermissionEntity> selectAllWithRoleName();
}








