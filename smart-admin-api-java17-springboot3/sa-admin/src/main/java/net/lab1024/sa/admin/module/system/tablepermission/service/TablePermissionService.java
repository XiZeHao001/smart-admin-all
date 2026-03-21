package net.lab1024.sa.admin.module.system.tablepermission.service;

import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.system.employee.domain.vo.EmployeeVO;
import net.lab1024.sa.admin.module.system.role.dao.RoleEmployeeDao;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleVO;
import net.lab1024.sa.admin.module.system.tablepermission.constant.TableOperationEnum;
import net.lab1024.sa.admin.module.system.tablepermission.dao.TablePermissionDao;
import net.lab1024.sa.admin.module.system.tablepermission.domain.entity.TablePermissionEntity;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表权限Service
 * 提供表权限的查询、保存、删除、校验功能
 *
 * @Author xzh
 * @Date 2025-11-28
 */
@Service
@Slf4j
public class TablePermissionService {

    @Resource
    private TablePermissionDao tablePermissionDao;

    @Resource
    private RoleEmployeeDao roleEmployeeDao;

    /**
     * 缓存结构：Map<employeeId, Map<moduleCode, TablePermissionConfig>>
     * 例如：{70: {"employee": {canView:true, canAdd:false, ...}}}
     */
    private final Map<Long, Map<String, TablePermissionConfig>> permissionCache = Maps.newConcurrentMap();

    /**
     * 获取指定用户对指定模块的表权限配置
     *
     * @param employeeId 员工ID
     * @param moduleCode 模块代码
     * @return 表权限配置，如果没有配置返回null
     */
    public TablePermissionConfig getTablePermission(Long employeeId, String moduleCode) {
        if (employeeId == null || moduleCode == null) {
            return null;
        }

        // 超级管理员拥有所有权限
        if (employeeId == 1L) {
            return TablePermissionConfig.fullPermission();
        }

        // 1. 从缓存获取
        Map<String, TablePermissionConfig> modulePermissionMap = permissionCache.get(employeeId);
        if (modulePermissionMap != null && modulePermissionMap.containsKey(moduleCode)) {
            return modulePermissionMap.get(moduleCode);
        }

        // 2. 从数据库查询
        List<RoleVO> roleList = roleEmployeeDao.selectRoleByEmployeeId(employeeId);
        if (CollectionUtils.isEmpty(roleList)) {
            log.warn("员工ID={} 没有任何角色", employeeId);
            return null;
        }

        List<Long> roleIdList = roleList.stream().map(RoleVO::getRoleId).collect(Collectors.toList());
        List<TablePermissionEntity> permissionList = tablePermissionDao.selectByRoleIds(roleIdList);

        // 3. 聚合多个角色的权限（取并集：任一角色有权限即可）
        Map<String, TablePermissionConfig> aggregatedPermissions = new HashMap<>();
        for (TablePermissionEntity entity : permissionList) {
            String code = entity.getModuleCode();
            TablePermissionConfig config = aggregatedPermissions.getOrDefault(code, new TablePermissionConfig());

            // 任一角色有权限，则该用户有权限（OR逻辑）
            config.setCanView(config.getCanView() || Boolean.TRUE.equals(entity.getCanView()));
            config.setCanAdd(config.getCanAdd() || Boolean.TRUE.equals(entity.getCanAdd()));
            config.setCanEdit(config.getCanEdit() || Boolean.TRUE.equals(entity.getCanEdit()));
            config.setCanDelete(config.getCanDelete() || Boolean.TRUE.equals(entity.getCanDelete()));
            config.setCanExport(config.getCanExport() || Boolean.TRUE.equals(entity.getCanExport()));
            config.setCanImport(config.getCanImport() || Boolean.TRUE.equals(entity.getCanImport()));
            config.setCanPrint(config.getCanPrint() || Boolean.TRUE.equals(entity.getCanPrint()));

            aggregatedPermissions.put(code, config);
        }

        // 4. 更新缓存
        permissionCache.put(employeeId, aggregatedPermissions);

        return aggregatedPermissions.get(moduleCode);
    }

    /**
     * 检查当前登录用户是否有指定模块的指定操作权限
     *
     * @param moduleCode 模块代码
     * @param operation  操作类型
     * @return true=有权限, false=无权限
     */
    public boolean hasPermission(String moduleCode, TableOperationEnum operation) {
        Long employeeId = SmartRequestUtil.getRequestUserId();
        return hasPermission(employeeId, moduleCode, operation);
    }

    /**
     * 检查指定用户是否有指定模块的指定操作权限
     *
     * @param employeeId 员工ID
     * @param moduleCode 模块代码
     * @param operation  操作类型
     * @return true=有权限, false=无权限
     */
    public boolean hasPermission(Long employeeId, String moduleCode, TableOperationEnum operation) {
        if (employeeId == null || moduleCode == null || operation == null) {
            return false;
        }

        // 超级管理员拥有所有权限
        if (employeeId == 1L) {
            return true;
        }

        TablePermissionConfig config = getTablePermission(employeeId, moduleCode);
        if (config == null) {
            log.warn("员工ID={} 对模块={} 没有配置表权限", employeeId, moduleCode);
            return false;
        }

        return switch (operation) {
            case VIEW -> Boolean.TRUE.equals(config.getCanView());
            case ADD -> Boolean.TRUE.equals(config.getCanAdd());
            case EDIT -> Boolean.TRUE.equals(config.getCanEdit());
            case DELETE -> Boolean.TRUE.equals(config.getCanDelete());
            case EXPORT -> Boolean.TRUE.equals(config.getCanExport());
            case IMPORT -> Boolean.TRUE.equals(config.getCanImport());
            case PRINT -> Boolean.TRUE.equals(config.getCanPrint());
        };
    }

    /**
     * 批量保存表权限配置
     *
     * @param roleId         角色ID
     * @param moduleCode     模块代码
     * @param config         权限配置
     * @param createUserId   创建人ID
     * @param createUserName 创建人姓名
     */
    public void saveTablePermission(Long roleId, String moduleCode, TablePermissionConfig config,
                                    Long createUserId, String createUserName) {
        TablePermissionEntity entity = new TablePermissionEntity();
        entity.setRoleId(roleId);
        entity.setModuleCode(moduleCode);
        entity.setCanView(config.getCanView());
        entity.setCanAdd(config.getCanAdd());
        entity.setCanEdit(config.getCanEdit());
        entity.setCanDelete(config.getCanDelete());
        entity.setCanExport(config.getCanExport());
        entity.setCanImport(config.getCanImport());
        entity.setCanPrint(config.getCanPrint());
        entity.setRemark(config.getRemark());
        entity.setCreateUserId(createUserId);
        entity.setCreateUserName(createUserName);

        tablePermissionDao.batchInsertOrUpdate(List.of(entity));

        // 清空相关缓存
        clearCacheByRole(roleId);
    }

    /**
     * 批量保存多个模块的表权限
     *
     * @param permissionList 权限列表
     */
    public void batchSaveTablePermissions(List<TablePermissionEntity> permissionList) {
        if (CollectionUtils.isEmpty(permissionList)) {
            return;
        }

        tablePermissionDao.batchInsertOrUpdate(permissionList);

        // 清空相关缓存
        permissionList.stream()
                .map(TablePermissionEntity::getRoleId)
                .distinct()
                .forEach(this::clearCacheByRole);
    }

    /**
     * 删除指定角色的所有表权限
     *
     * @param roleId 角色ID
     */
    public void deleteByRoleId(Long roleId) {
        tablePermissionDao.deleteByRoleId(roleId);
        clearCacheByRole(roleId);
    }

    /**
     * 查询指定角色和模块的表权限
     */
    public TablePermissionEntity getByRoleAndModule(Long roleId, String moduleCode) {
        return tablePermissionDao.selectByRoleAndModule(roleId, moduleCode);
    }

    /**
     * 查询所有表权限配置（包含角色名称）
     */
    public List<TablePermissionEntity> getAllWithRoleName() {
        return tablePermissionDao.selectAllWithRoleName();
    }

    /**
     * 清空指定角色相关的缓存
     */
    private void clearCacheByRole(Long roleId) {
        // 查询该角色下的所有员工
        List<EmployeeVO> employeeList = roleEmployeeDao.selectEmployeeByRoleId(roleId);
        if (CollectionUtils.isEmpty(employeeList)) {
            return;
        }

        // 清空这些员工的缓存
        employeeList.stream()
                .map(EmployeeVO::getEmployeeId)
                .forEach(permissionCache::remove);

        log.info("清空角色ID={} 的表权限缓存，影响员工数={}", roleId, employeeList.size());
    }

    /**
     * 清空所有缓存
     */
    public void clearAllCache() {
        permissionCache.clear();
        log.info("已清空所有表权限缓存");
    }

    /**
     * 表权限配置内部类
     */
    public static class TablePermissionConfig {
        private Boolean canView = false;
        private Boolean canAdd = false;
        private Boolean canEdit = false;
        private Boolean canDelete = false;
        private Boolean canExport = false;
        private Boolean canImport = false;
        private Boolean canPrint = false;
        private String remark;

        public static TablePermissionConfig fullPermission() {
            TablePermissionConfig config = new TablePermissionConfig();
            config.setCanView(true);
            config.setCanAdd(true);
            config.setCanEdit(true);
            config.setCanDelete(true);
            config.setCanExport(true);
            config.setCanImport(true);
            config.setCanPrint(true);
            return config;
        }

        // Getters and Setters
        public Boolean getCanView() {
            return canView;
        }

        public void setCanView(Boolean canView) {
            this.canView = canView;
        }

        public Boolean getCanAdd() {
            return canAdd;
        }

        public void setCanAdd(Boolean canAdd) {
            this.canAdd = canAdd;
        }

        public Boolean getCanEdit() {
            return canEdit;
        }

        public void setCanEdit(Boolean canEdit) {
            this.canEdit = canEdit;
        }

        public Boolean getCanDelete() {
            return canDelete;
        }

        public void setCanDelete(Boolean canDelete) {
            this.canDelete = canDelete;
        }

        public Boolean getCanExport() {
            return canExport;
        }

        public void setCanExport(Boolean canExport) {
            this.canExport = canExport;
        }

        public Boolean getCanImport() {
            return canImport;
        }

        public void setCanImport(Boolean canImport) {
            this.canImport = canImport;
        }

        public Boolean getCanPrint() {
            return canPrint;
        }

        public void setCanPrint(Boolean canPrint) {
            this.canPrint = canPrint;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}

