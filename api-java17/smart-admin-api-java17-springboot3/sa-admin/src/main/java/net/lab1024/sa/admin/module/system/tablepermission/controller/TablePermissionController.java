package net.lab1024.sa.admin.module.system.tablepermission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.system.fieldpermission.constant.FieldModuleCodeEnum;
import net.lab1024.sa.admin.module.system.tablepermission.domain.entity.TablePermissionEntity;
import net.lab1024.sa.admin.module.system.tablepermission.domain.form.TablePermissionSaveForm;
import net.lab1024.sa.admin.module.system.tablepermission.domain.vo.TablePermissionVO;
import net.lab1024.sa.admin.module.system.tablepermission.service.TablePermissionService;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表权限Controller
 * 提供表权限配置的增删改查接口
 *
 * @Author xzh
 * @Date 2025-11-28
 */
@RestController
@Tag(name = AdminSwaggerTagConst.System.SYSTEM_TABLE_PERMISSION)
public class TablePermissionController {

    @Resource
    private TablePermissionService tablePermissionService;

    /**
     * 查询指定角色和模块的表权限配置
     */
    @Operation(summary = "查询角色模块表权限配置")
    @GetMapping("/system/tablePermission/query/{roleId}/{moduleCode}")
    public ResponseDTO<TablePermissionVO> queryTablePermission(
            @PathVariable Long roleId,
            @PathVariable String moduleCode) {

        TablePermissionEntity entity = tablePermissionService.getByRoleAndModule(roleId, moduleCode);
        
        if (entity == null) {
            // 返回默认配置（全部false）
            TablePermissionVO vo = new TablePermissionVO();
            vo.setRoleId(roleId);
            vo.setModuleCode(moduleCode);
            vo.setModuleName(getModuleName(moduleCode));
            vo.setCanView(false);
            vo.setCanAdd(false);
            vo.setCanEdit(false);
            vo.setCanDelete(false);
            vo.setCanExport(false);
            vo.setCanImport(false);
            vo.setCanPrint(false);
            return ResponseDTO.ok(vo);
        }

        TablePermissionVO vo = SmartBeanUtil.copy(entity, TablePermissionVO.class);
        vo.setModuleName(getModuleName(moduleCode));
        return ResponseDTO.ok(vo);
    }

    /**
     * 查询所有表权限配置（包含角色名称）
     */
    @Operation(summary = "查询所有表权限配置")
    @GetMapping("/system/tablePermission/listAll")
    public ResponseDTO<List<TablePermissionVO>> listAll() {
        List<TablePermissionEntity> entityList = tablePermissionService.getAllWithRoleName();
        
        List<TablePermissionVO> voList = entityList.stream()
                .map(entity -> {
                    TablePermissionVO vo = SmartBeanUtil.copy(entity, TablePermissionVO.class);
                    vo.setModuleName(getModuleName(entity.getModuleCode()));
                    return vo;
                })
                .collect(Collectors.toList());
        
        return ResponseDTO.ok(voList);
    }

    /**
     * 保存表权限配置
     */
    @Operation(summary = "保存表权限配置")
    @PostMapping("/system/tablePermission/save")
    public ResponseDTO<String> saveTablePermission(@Valid @RequestBody TablePermissionSaveForm form) {
        Long userId = SmartRequestUtil.getRequestUserId();
        String userName = SmartRequestUtil.getRequestUser().getUserName();

        TablePermissionService.TablePermissionConfig config = new TablePermissionService.TablePermissionConfig();
        config.setCanView(form.getCanView());
        config.setCanAdd(form.getCanAdd());
        config.setCanEdit(form.getCanEdit());
        config.setCanDelete(form.getCanDelete());
        config.setCanExport(form.getCanExport());
        config.setCanImport(form.getCanImport());
        config.setCanPrint(form.getCanPrint());
        config.setRemark(form.getRemark());

        tablePermissionService.saveTablePermission(
                form.getRoleId(),
                form.getModuleCode(),
                config,
                userId,
                userName
        );

        return ResponseDTO.ok();
    }

    /**
     * 删除指定角色的所有表权限
     */
    @Operation(summary = "删除角色表权限配置")
    @GetMapping("/system/tablePermission/delete/{roleId}")
    public ResponseDTO<String> deleteTablePermissions(@PathVariable Long roleId) {
        tablePermissionService.deleteByRoleId(roleId);
        return ResponseDTO.ok();
    }

    /**
     * 清空表权限缓存
     */
    @Operation(summary = "清空表权限缓存")
    @PostMapping("/system/tablePermission/clearCache")
    public ResponseDTO<String> clearCache() {
        tablePermissionService.clearAllCache();
        return ResponseDTO.ok("缓存已清空");
    }

    /**
     * 获取所有支持的模块列表
     */
    @Operation(summary = "获取支持表权限的模块列表")
    @GetMapping("/system/tablePermission/modules")
    public ResponseDTO<List<Map<String, String>>> getModules() {
        List<Map<String, String>> modules = new ArrayList<>();
        
        for (FieldModuleCodeEnum moduleEnum : FieldModuleCodeEnum.values()) {
            Map<String, String> module = new java.util.HashMap<>();
            module.put("code", moduleEnum.getCode());
            module.put("name", moduleEnum.getDesc());
            modules.add(module);
        }
        
        return ResponseDTO.ok(modules);
    }

    /**
     * 根据模块代码获取模块名称
     */
    private String getModuleName(String moduleCode) {
        FieldModuleCodeEnum moduleEnum = FieldModuleCodeEnum.getByCode(moduleCode);
        return moduleEnum != null ? moduleEnum.getDesc() : moduleCode;
    }
}








