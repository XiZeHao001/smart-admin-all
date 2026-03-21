/*
 * AI聊天 API
 * 
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */
import { getRequest, postRequest, request } from '/@/lib/axios';

export const aiChatApi = {
  /**
   * 发送聊天消息（同步）
   * @param {Object} data - 聊天表单
   * @param {string} data.conversationId - 会话ID（可选）
   * @param {string} data.message - 用户消息
   * @param {string} data.modelConfigKey - 模型配置Key（可选）
   * @param {boolean} data.stream - 是否流式输出
   * @param {string} data.systemPrompt - 系统提示（可选）
   * @param {number} data.temperature - 温度参数（可选）
   * @param {number} data.maxTokens - 最大Token数（可选）
   * @returns {Promise} AI响应
   */
  sendMessage: (data) => {
    return postRequest('/support/ai/chat/send', data);
  },

  /**
   * 查询用户的会话列表
   * @param {number} limit - 数量限制（默认20）
   * @returns {Promise} 会话列表
   */
  getConversationList: (limit = 20) => {
    return getRequest('/support/ai/chat/conversations', { limit });
  },

  /**
   * 查询会话详情（包含消息列表）
   * @param {string} conversationId - 会话ID
   * @returns {Promise} 会话详情
   */
  getConversationDetail: (conversationId) => {
    return getRequest(`/support/ai/chat/conversations/${conversationId}`);
  },

  /**
   * 删除会话
   * @param {string} conversationId - 会话ID
   * @returns {Promise} 无返回
   */
  deleteConversation: (conversationId) => {
    return request({
      url: `/support/ai/chat/conversations/${conversationId}`,
      method: 'delete',
    });
  },

  /**
   * 查询所有可用的AI模型
   * @returns {Promise} 模型列表
   */
  getAvailableModels: () => {
    return getRequest('/support/ai/chat/models');
  },
};

