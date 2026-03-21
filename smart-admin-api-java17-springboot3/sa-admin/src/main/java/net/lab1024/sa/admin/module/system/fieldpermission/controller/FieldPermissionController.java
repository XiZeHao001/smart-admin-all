package net.lab1024.sa.admin.module.system.fieldpermission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.system.fieldpermission.constant.FieldModuleCodeEnum;
import net.lab1024.sa.admin.module.system.fieldpermission.dao.FieldMetadataDao;
import net.lab1024.sa.admin.module.system.fieldpermission.dao.FieldPermissionDao;
import net.lab1024.sa.admin.module.system.fieldpermission.domain.entity.FieldMetadataEntity;
import net.lab1024.sa.admin.module.system.fieldpermission.domain.entity.FieldPermissionEntity;
import net.lab1024.sa.admin.module.system.fieldpermission.domain.form.FieldPermissionBatchSaveForm;
import net.lab1024.sa.admin.module.system.fieldpermission.domain.vo.FieldPermissionConfigVO;
import net.lab1024.sa.admin.module.system.fieldpermission.domain.vo.FieldPermissionModuleVO;
import net.lab1024.sa.admin.module.system.fieldpermission.service.FieldPermissionService;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字段权限Controller
 * 提供字段权限配置的增删改查接口
 *
 * @Author xzh
 * @Date 2025-11-27
 */
@RestController
@Tag(name = AdminSwaggerTagConst.System.SYSTEM_FIELD_PERMISSION)
public class FieldPermissionController {

    @Resource
    private FieldPermissionService fieldPermissionService;

    @Resource
    private FieldPermissionDao fieldPermissionDao;

    @Resource
    private FieldMetadataDao fieldMetadataDao;

    /**
     * 查询角色的字段权限配置
     */
    @Operation(summary = "查询角色字段权限配置 @author xzh")
    @GetMapping("/fieldPermission/query/{roleId}/{moduleCode}")
    public ResponseDTO<FieldPermissionModuleVO> queryFieldPermissions(@PathVariable Long roleId,
                                                                      @PathVariable String moduleCode) {
        
        // 1. 查询字段元数据
        FieldMetadataEntity metadata = fieldMetadataDao.selectByModuleCode(moduleCode);
        if (metadata == null) {
            return ResponseDTO.userErrorParam("模块不存在：" + moduleCode);
        }

        // 2. 查询角色的字段权限配置
        List<FieldPermissionEntity> permissionList = 
            fieldPermissionService.getFieldPermissionsByRole(roleId, moduleCode);
        
        Map<String, FieldPermissionEntity> permissionMap = permissionList.stream()
            .collect(Collectors.toMap(FieldPermissionEntity::getFieldName, p -> p, (v1, v2) -> v1));

        // 3. 构建返回VO
        FieldPermissionModuleVO moduleVO = new FieldPermissionModuleVO();
        moduleVO.setRoleId(roleId);
        moduleVO.setModuleCode(moduleCode);
        
        // 使用自定义的 getByCode 方法
        FieldModuleCodeEnum moduleEnum = FieldModuleCodeEnum.getByCode(moduleCode);
        moduleVO.setModuleName(moduleEnum != null ? moduleEnum.getDesc() : moduleCode);
        
        // TODO: 从metadata的fieldJson解析字段列表并映射权限配置
        // 这里简化处理，先返回已配置的权限
        List<FieldPermissionConfigVO> fieldConfigs = permissionList.stream()
            .map(entity -> {
                FieldPermissionConfigVO config = new FieldPermissionConfigVO();
                config.setFieldName(entity.getFieldName());
                config.setPermissionType(entity.getPermissionType());
                config.setMaskType(entity.getMaskType());
                config.setRemark(entity.getRemark());
                return config;
            })
            .collect(Collectors.toList());
        
        moduleVO.setFields(fieldConfigs);

        return ResponseDTO.ok(moduleVO);
    }

    /**
     * 批量保存字段权限配置
     */
    @Operation(summary = "批量保存字段权限配置 @author xzh")
    @PostMapping("/fieldPermission/batchSave")
    public ResponseDTO<String> batchSaveFieldPermissions(
            @Valid @RequestBody FieldPermissionBatchSaveForm form) {
        
        Long userId = SmartRequestUtil.getRequestUserId();
        String userName = SmartRequestUtil.getRequestUser().getUserName();

        // 转换Form为Entity
        List<FieldPermissionEntity> entityList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(form.getFieldPermissions())) {
            for (FieldPermissionConfigVO fieldConfig : form.getFieldPermissions()) {
                FieldPermissionEntity entity = new FieldPermissionEntity();
                entity.setRoleId(form.getRoleId());
                entity.setModuleCode(form.getModuleCode());
                entity.setFieldName(fieldConfig.getFieldName());
                entity.setPermissionType(fieldConfig.getPermissionType());
                entity.setMaskType(fieldConfig.getMaskType());
                entity.setCreateUserId(userId);
                entity.setCreateUserName(userName);
                entityList.add(entity);
            }
        }

        fieldPermissionService.saveFieldPermissions(
            form.getRoleId(), 
            form.getModuleCode(), 
            entityList, 
            userId, 
            userName
        );

        return ResponseDTO.ok();
    }

    /**
     * 删除角色的字段权限配置
     */
    @Operation(summary = "删除角色字段权限配置 @author xzh")
    @GetMapping("/fieldPermission/delete/{roleId}")
    public ResponseDTO<String> deleteFieldPermissions(@PathVariable Long roleId) {
        fieldPermissionService.deleteFieldPermissions(roleId);
        return ResponseDTO.ok();
    }

    /**
     * 清空字段权限缓存
     */
    @Operation(summary = "清空字段权限缓存 @author xzh")
    @PostMapping("/fieldPermission/clearCache")
    public ResponseDTO<String> clearCache() {
        fieldPermissionService.clearAllCache();
        return ResponseDTO.ok("缓存已清空");
    }

    /**
     * 获取所有支持的模块列表
     */
    @Operation(summary = "获取支持字段权限的模块列表 @author xzh")
    @GetMapping("/fieldPermission/modules")
    public ResponseDTO<List<Map<String, String>>> getModules() {
        List<Map<String, String>> modules = new ArrayList<>();
        for (FieldModuleCodeEnum moduleEnum : FieldModuleCodeEnum.values()) {
            Map<String, String> module = new java.util.HashMap<>();
            module.put("code", moduleEnum.getCode());  // ✅ 使用 getCode() 而不是 getValue()
            module.put("name", moduleEnum.getDesc());
            modules.add(module);
        }
        return ResponseDTO.ok(modules);
    }
}
