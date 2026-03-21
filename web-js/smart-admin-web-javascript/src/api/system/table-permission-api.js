/*
 * 表权限
 *
 * @Author:    xzh
 * @Date:      2025-11-28
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import { getRequest, postRequest } from '/@/lib/axios';

export const tablePermissionApi = {
  /**
   * @description: 查询指定角色和模块的表权限配置
   * @param {Long} roleId - 角色ID
   * @param {String} moduleCode - 模块代码
   */
  queryTablePermission: (roleId, moduleCode) => {
    return getRequest(`/system/tablePermission/query/${roleId}/${moduleCode}`);
  },

  /**
   * @description: 查询所有表权限配置（包含角色名称）
   */
  listAll: () => {
    return getRequest('/system/tablePermission/listAll');
  },

  /**
   * @description: 获取支持表权限的模块列表
   */
  getModules: () => {
    return getRequest('/system/tablePermission/modules');
  },

  /**
   * @description: 保存表权限配置
   * @param {Object} data - 表权限配置
   * @param {Long} data.roleId - 角色ID
   * @param {String} data.moduleCode - 模块代码
   * @param {Boolean} data.canView - 查看权限
   * @param {Boolean} data.canAdd - 新增权限
   * @param {Boolean} data.canEdit - 编辑权限
   * @param {Boolean} data.canDelete - 删除权限
   * @param {Boolean} data.canExport - 导出权限
   * @param {Boolean} data.canImport - 导入权限
   * @param {Boolean} data.canPrint - 打印权限
   * @param {String} data.remark - 备注
   */
  saveTablePermission: (data) => {
    return postRequest('/system/tablePermission/save', data);
  },

  /**
   * @description: 删除角色的所有表权限配置
   * @param {Long} roleId - 角色ID
   */
  deleteTablePermissions: (roleId) => {
    return getRequest(`/system/tablePermission/delete/${roleId}`);
  },

  /**
   * @description: 清空表权限缓存
   */
  clearCache: () => {
    return postRequest('/system/tablePermission/clearCache');
  },
};
