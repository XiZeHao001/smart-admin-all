package net.lab1024.sa.admin.module.system.fieldpermission.service;

import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.system.employee.dao.EmployeeDao;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import net.lab1024.sa.admin.module.system.fieldpermission.constant.FieldMaskTypeEnum;
import net.lab1024.sa.admin.module.system.fieldpermission.constant.FieldPermissionTypeEnum;
import net.lab1024.sa.admin.module.system.fieldpermission.dao.FieldPermissionDao;
import net.lab1024.sa.admin.module.system.fieldpermission.domain.entity.FieldPermissionEntity;
import net.lab1024.sa.admin.module.system.role.dao.RoleEmployeeDao;
import net.lab1024.sa.admin.util.FieldMaskUtil;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字段权限Service
 * 核心功能：根据用户角色，过滤/脱敏返回数据中的字段
 *
 * @Author xzh
 * @Date 2025-11-27
 * @Wechat
 * @Email
 * @Copyright
 */
@Slf4j
@Service
public class FieldPermissionService {

    @Resource
    private FieldPermissionDao fieldPermissionDao;

    @Resource
    private RoleEmployeeDao roleEmployeeDao;

    @Resource
    private EmployeeDao employeeDao;

    /**
     * 字段权限缓存：Map<employeeId, Map<moduleCode, Map<fieldName, FieldPermissionConfig>>>
     * 为了性能考虑，实际使用时建议接入Redis或Caffeine缓存
     */
    private final Map<Long, Map<String, Map<String, FieldPermissionConfig>>> permissionCache = Maps.newConcurrentMap();

    /**
     * 获取用户的字段权限配置（带缓存）
     *
     * @param employeeId 用户ID
     * @param moduleCode 模块代码
     * @return 字段权限配置Map：key=fieldName, value=FieldPermissionConfig
     */
    public Map<String, FieldPermissionConfig> getFieldPermissions(Long employeeId, String moduleCode) {
        // 1. 检查是否为超级管理员
        EmployeeEntity employee = employeeDao.selectById(employeeId);
        if (employee != null && employee.getAdministratorFlag()) {
            // 超级管理员拥有所有字段的完整权限，返回空Map表示不限制
            return new HashMap<>();
        }

        // 2. 尝试从缓存获取
        Map<String, Map<String, FieldPermissionConfig>> modulePermissions = permissionCache.get(employeeId);
        if (modulePermissions != null && modulePermissions.containsKey(moduleCode)) {
            return modulePermissions.get(moduleCode);
        }

        // 3. 查询数据库
        List<Long> roleIdList = roleEmployeeDao.selectRoleIdByEmployeeId(employeeId);
        if (CollectionUtils.isEmpty(roleIdList)) {
            return new HashMap<>();  // 没有角色，不限制
        }

        List<FieldPermissionEntity> permissionList = fieldPermissionDao.selectByRoleIds(roleIdList);
        if (CollectionUtils.isEmpty(permissionList)) {
            return new HashMap<>();  // 没有配置字段权限，不限制
        }

        // 4. 按模块分组
        Map<String, List<FieldPermissionEntity>> groupedByModule = permissionList.stream()
                .collect(Collectors.groupingBy(FieldPermissionEntity::getModuleCode));

        // 5. 构建字段权限配置
        Map<String, Map<String, FieldPermissionConfig>> allModulePermissions = new HashMap<>();
        for (Map.Entry<String, List<FieldPermissionEntity>> entry : groupedByModule.entrySet()) {
            String module = entry.getKey();
            Map<String, FieldPermissionConfig> fieldPermissions = new HashMap<>();

            for (FieldPermissionEntity entity : entry.getValue()) {
                FieldPermissionConfig config = new FieldPermissionConfig();
                config.setPermissionType(SmartEnumUtil.getEnumByValue(entity.getPermissionType(), FieldPermissionTypeEnum.class));
                config.setMaskType(entity.getMaskType() != null ?
                        SmartEnumUtil.getEnumByValue(entity.getMaskType(), FieldMaskTypeEnum.class) : null);
                fieldPermissions.put(entity.getFieldName(), config);
            }

            allModulePermissions.put(module, fieldPermissions);
        }

        // 6. 缓存结果
        permissionCache.put(employeeId, allModulePermissions);

        return allModulePermissions.getOrDefault(moduleCode, new HashMap<>());
    }

    /**
     * 清除用户的字段权限缓存
     *
     * @param employeeId 用户ID
     */
    public void clearCache(Long employeeId) {
        permissionCache.remove(employeeId);
    }

    /**
     * 清除所有字段权限缓存
     */
    public void clearAllCache() {
        permissionCache.clear();
    }

    /**
     * 应用字段权限到单个对象
     *
     * @param obj        待处理的对象
     * @param moduleCode 模块代码
     */
    public void applyFieldPermissions(Object obj, String moduleCode) {
        if (obj == null) {
            return;
        }

        Long employeeId = SmartRequestUtil.getRequestUserId();
        if (employeeId == null) {
            return;  // 未登录，不处理
        }

        Map<String, FieldPermissionConfig> fieldPermissions = getFieldPermissions(employeeId, moduleCode);
        if (fieldPermissions.isEmpty()) {
            return;  // 没有配置字段权限，不处理
        }

        applyFieldPermissionsToObject(obj, fieldPermissions);
    }

    /**
     * 应用字段权限到对象列表
     *
     * @param list       待处理的对象列表
     * @param moduleCode 模块代码
     */
    public void applyFieldPermissionsList(List<?> list, String moduleCode) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        Long employeeId = SmartRequestUtil.getRequestUserId();
        if (employeeId == null) {
            return;
        }

        Map<String, FieldPermissionConfig> fieldPermissions = getFieldPermissions(employeeId, moduleCode);
        if (fieldPermissions.isEmpty()) {
            return;
        }

        for (Object obj : list) {
            applyFieldPermissionsToObject(obj, fieldPermissions);
        }
    }

    /**
     * 应用字段权限到对象（核心方法）
     *
     * @param obj              待处理的对象
     * @param fieldPermissions 字段权限配置
     */
    private void applyFieldPermissionsToObject(Object obj, Map<String, FieldPermissionConfig> fieldPermissions) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            FieldPermissionConfig config = fieldPermissions.get(fieldName);

            if (config == null) {
                continue;  // 该字段没有权限配置，跳过
            }

            try {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value == null) {
                    continue;
                }

                // 根据权限类型处理
                FieldPermissionTypeEnum permissionType = config.getPermissionType();

                if (permissionType == FieldPermissionTypeEnum.HIDDEN) {
                    // 隐藏：设置为null
                    field.set(obj, null);
                } else if (permissionType == FieldPermissionTypeEnum.MASKED) {
                    // 脱敏：仅处理String类型
                    if (value instanceof String) {
                        String maskedValue = FieldMaskUtil.mask((String) value, config.getMaskType());
                        field.set(obj, maskedValue);
                    }
                }
                // VISIBLE、READONLY、EDITABLE：不做处理，直接返回原值

            } catch (IllegalAccessException e) {
                log.error("Failed to apply field permission for field: {}", fieldName, e);
            }
        }
    }

    /**
     * 字段权限配置内部类
     */
    public static class FieldPermissionConfig {
        private FieldPermissionTypeEnum permissionType;
        private FieldMaskTypeEnum maskType;

        public FieldPermissionTypeEnum getPermissionType() {
            return permissionType;
        }

        public void setPermissionType(FieldPermissionTypeEnum permissionType) {
            this.permissionType = permissionType;
        }

        public FieldMaskTypeEnum getMaskType() {
            return maskType;
        }

        public void setMaskType(FieldMaskTypeEnum maskType) {
            this.maskType = maskType;
        }
    }

    /**
     * 保存字段权限配置
     *
     * @param roleId              角色ID
     * @param moduleCode          模块代码
     * @param fieldPermissions    字段权限配置列表
     * @param createUserId        创建人ID
     * @param createUserName      创建人姓名
     */
    public void saveFieldPermissions(Long roleId, String moduleCode, List<FieldPermissionEntity> fieldPermissions,
                                     Long createUserId, String createUserName) {
        if (CollectionUtils.isEmpty(fieldPermissions)) {
            return;
        }

        // 设置创建人信息
        for (FieldPermissionEntity entity : fieldPermissions) {
            entity.setRoleId(roleId);
            entity.setModuleCode(moduleCode);
            entity.setCreateUserId(createUserId);
            entity.setCreateUserName(createUserName);
        }

        // 批量插入或更新
        fieldPermissionDao.batchInsertOrUpdate(fieldPermissions);

        // 清除相关用户的缓存
        clearAllCache();
    }

    /**
     * 查询角色的字段权限配置
     *
     * @param roleId     角色ID
     * @param moduleCode 模块代码
     * @return 字段权限配置列表
     */
    public List<FieldPermissionEntity> getFieldPermissionsByRole(Long roleId, String moduleCode) {
        return fieldPermissionDao.selectByRoleAndModule(roleId, moduleCode);
    }

    /**
     * 删除角色的字段权限配置
     *
     * @param roleId 角色ID
     */
    public void deleteFieldPermissions(Long roleId) {
        fieldPermissionDao.deleteByRoleId(roleId);
        clearAllCache();
    }
}

