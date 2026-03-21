/*
 * AI配置管理API
 *
 * @Author:    1024创新实验室
 * @Date:      2025-11-30
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import { getRequest, postRequest } from '/@/lib/axios';

export const aiConfigApi = {
  /**
   * 分页查询AI配置
   */
  queryPage: (params) => {
    return postRequest('/support/ai/config/query', params);
  },

  /**
   * 查询所有启用的配置
   */
  listEnabled: () => {
    return getRequest('/support/ai/config/listEnabled');
  },

  /**
   * 查询配置详情
   */
  getDetail: (id) => {
    return getRequest(`/support/ai/config/detail/${id}`);
  },

  /**
   * 新增配置
   */
  add: (data) => {
    return postRequest('/support/ai/config/add', data);
  },

  /**
   * 更新配置
   */
  update: (data) => {
    return postRequest('/support/ai/config/update', data);
  },

  /**
   * 删除配置
   */
  delete: (id) => {
    return getRequest(`/support/ai/config/delete/${id}`);
  },

  /**
   * 批量删除
   */
  batchDelete: (idList) => {
    return postRequest('/support/ai/config/batchDelete', idList);
  },

  /**
   * 设置默认模型
   */
  setDefault: (id) => {
    return getRequest(`/support/ai/config/setDefault/${id}`);
  },

  /**
   * 启用/禁用配置
   */
  updateStatus: (id, enabled) => {
    return postRequest('/support/ai/config/updateStatus', null, {
      id,
      enabled,
    });
  },

  /**
   * 测试连接
   */
  testConnection: (id) => {
    return getRequest(`/support/ai/config/testConnection/${id}`);
  },
};





