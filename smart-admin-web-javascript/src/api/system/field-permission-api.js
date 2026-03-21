/*
 * 字段权限
 *
 * @Author:    xzh
 * @Date:      2025-11-28
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import { getRequest, postRequest } from '/@/lib/axios';

export const fieldPermissionApi = {
  /**
   * @description: 获取支持字段权限的模块列表
   */
  getModules: () => {
    return getRequest('/fieldPermission/modules');
  },

  /**
   * @description: 查询角色的字段权限配置
   */
  queryFieldPermissions: (roleId, moduleCode) => {
    return getRequest(`/fieldPermission/query/${roleId}/${moduleCode}`);
  },

  /**
   * @description: 批量保存字段权限配置
   */
  batchSaveFieldPermissions: (data) => {
    return postRequest('/fieldPermission/batchSave', data);
  },

  /**
   * @description: 删除角色的字段权限配置
   */
  deleteFieldPermissions: (roleId) => {
    return getRequest(`/fieldPermission/delete/${roleId}`);
  },

  /**
   * @description: 清空字段权限缓存
   */
  clearCache: () => {
    return postRequest('/fieldPermission/clearCache');
  },
};
