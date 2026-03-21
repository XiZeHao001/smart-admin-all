<!--
 * AI聊天主页面
 *
 * Gemini风格设计：
 * - 简洁的顶部工具栏
 * - 优雅的消息卡片
 * - 流畅的动画效果
 * - 圆润的输入框
 *
 * @Author 1024创新实验室
 * @Date 2025-11-30
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
-->
<template>
  <div class="h-full flex flex-col bg-gray-50">
    <!-- 顶部工具栏 -->
    <div class="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
      <div class="flex items-center space-x-4">
        <!-- 标题 -->
        <h1 class="text-2xl font-semibold text-gray-800">AI 智能助手</h1>

        <!-- 新建对话按钮 -->
        <a-button type="primary" @click="handleNewConversation" class="rounded-full">
          <template #icon><PlusOutlined /></template>
          新建对话
        </a-button>

        <!-- 历史会话下拉 -->
        <a-dropdown>
          <a-button class="rounded-full">
            <template #icon><HistoryOutlined /></template>
            历史对话
            <DownOutlined />
          </a-button>
          <template #overlay>
            <a-menu @click="handleSelectConversation">
              <a-menu-item v-for="conv in conversationList" :key="conv.conversationId">
                <div class="max-w-xs truncate">{{ conv.title }}</div>
                <div class="text-xs text-gray-400">{{ formatTime(conv.createTime) }}</div>
              </a-menu-item>
              <a-menu-divider v-if="conversationList.length > 0" />
              <a-menu-item key="view-all">
                <a-button type="link" size="small">查看全部</a-button>
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>

      <!-- 模型选择器 -->
      <div class="flex items-center space-x-4">
        <span class="text-sm text-gray-600">模型:</span>
        <a-select
          v-model:value="selectedModel"
          style="width: 250px"
          placeholder="选择AI模型"
          :loading="!availableModels.length"
          @change="handleModelChange"
        >
          <template v-if="modelGroups.length > 0">
            <a-select-opt-group v-for="group in modelGroups" :key="group.provider" :label="group.providerName">
              <a-select-option v-for="model in group.models" :key="model.configKey" :value="model.configKey">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <span>{{ model.modelDisplayName }}</span>
                  <span style="font-size: 12px; color: #999; margin-left: 8px;">
                    {{ formatPrice(model.priceInput, model.priceOutput) }}
                  </span>
                </div>
              </a-select-option>
            </a-select-opt-group>
          </template>
          <a-select-option v-else value="" disabled>
            加载模型列表中...
          </a-select-option>
        </a-select>

        <!-- 设置按钮 -->
        <a-button shape="circle" @click="showSettings = true">
          <template #icon><SettingOutlined /></template>
        </a-button>
      </div>
    </div>

    <!-- 消息列表区域 -->
    <div ref="messageContainer" class="flex-1 overflow-y-auto px-6 py-8">
      <div class="max-w-4xl mx-auto">
        <!-- 空状态 -->
        <div v-if="messages.length === 0" class="text-center py-20">
          <div class="text-6xl mb-4">✨</div>
          <h2 class="text-2xl font-semibold text-gray-700 mb-2">开始与AI对话</h2>
          <p class="text-gray-500">选择一个模型，输入您的问题</p>
        </div>

        <!-- 消息列表 -->
        <div v-for="(msg, index) in messages" :key="index" :class="['gemini-message', msg.role === 'user' ? 'gemini-user-message' : 'gemini-ai-message']">
          <!-- 用户消息 -->
          <div v-if="msg.role === 'user'" class="flex items-start space-x-3">
            <div class="w-8 h-8 rounded-full bg-blue-500 flex items-center justify-center text-white font-semibold flex-shrink-0">
              {{ getUserInitial() }}
            </div>
            <div class="flex-1">
              <div class="text-gray-800 whitespace-pre-wrap">{{ msg.content }}</div>
            </div>
          </div>

          <!-- AI消息 -->
          <div v-else class="flex items-start space-x-3">
            <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center flex-shrink-0">
              <RobotOutlined class="text-white text-lg" />
            </div>
            <div class="flex-1">
              <!-- Markdown渲染的内容 -->
              <div v-html="renderMarkdown(msg.content)" class="prose prose-sm max-w-none markdown-content"></div>

              <!-- 消息操作按钮 -->
              <div class="mt-3 flex items-center space-x-2">
                <a-button size="small" type="text" @click="handleCopy(msg.content)">
                  <template #icon><CopyOutlined /></template>
                  复制
                </a-button>
                <a-button size="small" type="text" @click="handleRegenerate(msg)">
                  <template #icon><ReloadOutlined /></template>
                  重新生成
                </a-button>
                <a-button size="small" type="text">
                  <template #icon><LikeOutlined /></template>
                </a-button>
                <a-button size="small" type="text">
                  <template #icon><DislikeOutlined /></template>
                </a-button>

                <!-- Token和费用信息 -->
                <span v-if="msg.tokensTotal" class="text-xs text-gray-400 ml-auto">
                  {{ msg.tokensTotal }} tokens · ¥{{ msg.costAmount?.toFixed(6) || '0.000000' }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载中动画 -->
        <div v-if="isLoading" class="gemini-message gemini-ai-message">
          <div class="flex items-start space-x-3">
            <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center flex-shrink-0">
              <RobotOutlined class="text-white text-lg" />
            </div>
            <div class="flex-1">
              <div class="flex space-x-2">
                <div class="w-2 h-2 bg-blue-500 rounded-full animate-bounce" style="animation-delay: 0s"></div>
                <div class="w-2 h-2 bg-blue-500 rounded-full animate-bounce" style="animation-delay: 0.2s"></div>
                <div class="w-2 h-2 bg-blue-500 rounded-full animate-bounce" style="animation-delay: 0.4s"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部输入区域 -->
    <div class="bg-white border-t border-gray-200 px-6 py-4">
      <div class="max-w-4xl mx-auto">
        <div class="relative flex items-center space-x-3">
          <!-- 附件按钮 -->
          <a-button shape="circle" size="large" @click="handleAttachment">
            <template #icon><PaperClipOutlined /></template>
          </a-button>

          <!-- 输入框 -->
          <a-textarea
            v-model:value="inputMessage"
            :auto-size="{ minRows: 1, maxRows: 4 }"
            placeholder="输入您的问题..."
            class="flex-1 rounded-2xl resize-none"
            @keydown="handleKeyDown"
          />

          <!-- 语音按钮 -->
          <a-button shape="circle" size="large" @click="handleVoice">
            <template #icon><AudioOutlined /></template>
          </a-button>

          <!-- 发送按钮 -->
          <a-button
            type="primary"
            shape="circle"
            size="large"
            :loading="isLoading"
            :disabled="!inputMessage.trim()"
            @click="handleSend"
          >
            <template #icon><SendOutlined /></template>
          </a-button>
        </div>

        <!-- 提示信息 -->
        <div class="mt-2 text-xs text-gray-400 text-center">
          按 Enter 发送，Shift + Enter 换行
        </div>
      </div>
    </div>

    <!-- 设置抽屉 -->
    <a-drawer
      v-model:open="showSettings"
      title="对话设置"
      placement="right"
      :width="400"
    >
      <a-form layout="vertical">
        <a-form-item label="温度参数">
          <a-slider v-model:value="temperature" :min="0" :max="2" :step="0.1" />
          <div class="text-xs text-gray-500">控制回复的随机性，越大越随机</div>
        </a-form-item>

        <a-form-item label="最大Token数">
          <a-input-number v-model:value="maxTokens" :min="100" :max="8192" style="width: 100%" />
          <div class="text-xs text-gray-500">控制回复的长度</div>
        </a-form-item>

        <a-form-item label="系统提示">
          <a-textarea v-model:value="systemPrompt" :rows="4" placeholder="设定AI的行为和角色..." />
        </a-form-item>
      </a-form>
    </a-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue';
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { localRead } from '/@/utils/local-util';
import LocalStorageKeyConst from '/@/constants/local-storage-key-const.js';

// HMR支持
if (import.meta.hot) {
  import.meta.hot.accept();
}
import { message as AMessage } from 'ant-design-vue';
import {
  PlusOutlined,
  HistoryOutlined,
  DownOutlined,
  SettingOutlined,
  RobotOutlined,
  CopyOutlined,
  ReloadOutlined,
  LikeOutlined,
  DislikeOutlined,
  SendOutlined,
  PaperClipOutlined,
  AudioOutlined,
} from '@ant-design/icons-vue';
import { aiChatApi } from '/@/api/support/ai-chat-api';
import { marked } from 'marked';
import DOMPurify from 'dompurify';
import hljs from 'highlight.js';
import dayjs from 'dayjs';

// ==================== 状态管理 ====================

// 消息列表
const messages = ref([]);

// 当前会话ID
const currentConversationId = ref(null);

// 输入框内容
const inputMessage = ref('');

// 加载状态
const isLoading = ref(false);

// 可用模型列表
const availableModels = ref([]);

// 选中的模型
const selectedModel = ref(null);

// 会话列表
const conversationList = ref([]);

// 设置抽屉
const showSettings = ref(false);

// 对话设置
const temperature = ref(0.7);
const maxTokens = ref(2048);
const systemPrompt = ref('');

// 消息容器引用
const messageContainer = ref(null);

// ==================== 计算属性 ====================

// 按提供商分组的模型列表
const modelGroups = computed(() => {
  if (!availableModels.value || availableModels.value.length === 0) {
    return [];
  }

  const groups = {};

  availableModels.value.forEach(model => {
    if (!model || !model.provider) return;

    if (!groups[model.provider]) {
      groups[model.provider] = {
        provider: model.provider,
        providerName: getProviderDisplayName(model.provider),
        models: [],
      };
    }
    groups[model.provider].models.push(model);
  });

  return Object.values(groups);
});

/**
 * 获取提供商显示名称
 */
function getProviderDisplayName(provider) {
  const providerNames = {
    'DASHSCOPE': '阿里云通义',
    'OPENAI': 'OpenAI',
    'ANTHROPIC': 'Anthropic Claude',
    'GOOGLE': 'Google Gemini',
    'BAIDU': '百度文心',
    'ZHIPU': '智谱AI',
    'XUNFEI': '讯飞星火',
    'MOONSHOT': '月之暗面',
    'DOUBAO': '字节豆包',
    'DEEPSEEK': 'DeepSeek',
    'MINIMAX': 'Minimax',
    'LINGYIWANWU': '零一万物',
    'TENCENT': '腾讯混元',
    'STEPFUN': '阶跃星辰',
    'OLLAMA': '本地Ollama',
  };
  return providerNames[provider] || provider;
}

// ==================== 生命周期 ====================

onMounted(async () => {
  await loadModels();
  await loadConversations();

  // 选择默认模型
  if (availableModels.value && availableModels.value.length > 0) {
    const defaultModel = availableModels.value.find(m => m.defaultFlag);
    if (defaultModel) {
      selectedModel.value = defaultModel.configKey;
    } else {
      // 如果没有默认模型，选择第一个
      selectedModel.value = availableModels.value[0].configKey;
    }
  }
});

// ==================== 方法 ====================

/**
 * 加载可用模型
 */
async function loadModels() {
  try {
    const res = await aiChatApi.getAvailableModels();
    if (res && res.data) {
      availableModels.value = res.data;
      console.log('加载模型成功:', res.data);
    } else {
      console.warn('API返回数据格式异常:', res);
      AMessage.warning('模型列表为空，请检查后端配置');
    }
  } catch (error) {
    console.error('加载模型列表失败:', error);
    AMessage.error('加载模型列表失败: ' + (error.msg || error.message || '网络错误'));
  }
}

/**
 * 加载会话列表
 */
async function loadConversations() {
  try {
    const res = await aiChatApi.getConversationList(10);
    if (res.data) {
      conversationList.value = res.data;
    }
  } catch (error) {
    console.error('加载会话列表失败', error);
  }
}

/**
 * 处理键盘事件
 */
function handleKeyDown(e) {
  // Enter键发送（不含Shift）
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault();
    handleSend();
  }
  // Shift+Enter换行（默认行为，不需要额外处理）
}

/**
 * 发送消息（支持SSE流式响应）
 */
async function handleSend() {
  if (!inputMessage.value.trim()) {
    return;
  }

  if (!selectedModel.value) {
    AMessage.warning('请先选择一个AI模型');
    return;
  }

  // 添加用户消息
  const userMessage = {
    role: 'user',
    content: inputMessage.value,
    createTime: new Date(),
  };
  messages.value.push(userMessage);

  // 清空输入框
  const messageToSend = inputMessage.value;
  inputMessage.value = '';

  // 滚动到底部
  await nextTick();
  scrollToBottom();

  // 显示加载状态
  isLoading.value = true;

  // 创建AI消息占位符（使用reactive确保响应式）
  const aiMessage = reactive({
    role: 'assistant',
    content: '',
    model: selectedModel.value,
    createTime: new Date(),
  });
  messages.value.push(aiMessage);

  try {
    // 使用fetchEventSource处理SSE流式响应
    const baseURL = import.meta.env.VITE_APP_API_BASE_URL || 'http://127.0.0.1:10010';
    const token = localRead(LocalStorageKeyConst.USER_TOKEN);

    if (!token) {
      AMessage.error('请先登录');
      isLoading.value = false;
      messages.value = messages.value.filter(m => m !== aiMessage);
      return;
    }

    await fetchEventSource(`${baseURL}/support/ai/chat/stream`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token,
      },
      body: JSON.stringify({
        conversationId: currentConversationId.value,
        message: messageToSend,
        modelConfigKey: selectedModel.value,
        temperature: temperature.value,
        maxTokens: maxTokens.value,
        systemPrompt: systemPrompt.value || undefined,
      }),

      async onopen(response) {
        if (response.ok) {
          isLoading.value = false;
          return; // 一切正常
        }
        throw new Error(`HTTP错误: ${response.status}`);
      },

      onmessage(event) {
        try {
          console.log('收到SSE消息:', event.data);
          const data = JSON.parse(event.data);
          console.log('解析后的数据:', data);

          // 处理内容增量（后端发送的是 {content:"xxx"}）
          if (data.content) {
            console.log('追加内容:', data.content);
            aiMessage.content += data.content;
            console.log('当前AI消息内容:', aiMessage.content);
            // 滚动到底部
            nextTick(() => scrollToBottom());
          }

          // 处理完成事件（后端发送的是 {done:true, conversationId:"xxx"}）
          if (data.done) {
            console.log('对话完成, conversationId:', data.conversationId);
            if (data.conversationId) {
              currentConversationId.value = data.conversationId;
            }
            // 刷新会话列表
            loadConversations();
          }

          // 处理错误
          if (data.error) {
            console.error('AI错误:', data.error);
            AMessage.error('AI回复失败: ' + data.error);
            messages.value = messages.value.filter(m => m !== aiMessage);
          }
        } catch (e) {
          console.error('解析SSE消息失败:', e, event.data);
        }
      },

      onerror(error) {
        console.error('SSE错误:', error);
        isLoading.value = false;
        AMessage.error('连接中断，请重试');
        throw error; // 停止重试
      },

      onclose() {
        isLoading.value = false;
      },
    });

  } catch (error) {
    console.error('发送失败:', error);
    isLoading.value = false;
    // 移除失败的AI消息
    messages.value = messages.value.filter(m => m !== aiMessage);
    AMessage.error('发送失败：' + (error.message || '网络错误'));
  }
}

/**
 * 新建对话
 */
function handleNewConversation() {
  currentConversationId.value = null;
  messages.value = [];
  inputMessage.value = '';
}

/**
 * 选择历史会话
 */
async function handleSelectConversation({ key }) {
  if (key === 'view-all') {
    // TODO: 打开会话列表页面
    return;
  }

  try {
    const res = await aiChatApi.getConversationDetail(key);
    console.log('加载会话历史:', res);

    if (res.data && Array.isArray(res.data)) {
      // 后端直接返回消息数组
      messages.value = res.data.map(msg => ({
        role: msg.role,
        content: msg.content,
        model: msg.model,
        createTime: msg.createTime,
      }));

      // 从消息中获取 conversationId（所有消息的 conversationId 应该是一样的）
      if (res.data.length > 0) {
        currentConversationId.value = res.data[0].conversationId || key;
      } else {
        currentConversationId.value = key;
      }

      console.log('加载了', messages.value.length, '条历史消息');

      await nextTick();
      scrollToBottom();
    }
  } catch (error) {
    console.error('加载会话失败:', error);
    AMessage.error('加载会话失败: ' + (error.msg || error.message || '网络错误'));
  }
}

/**
 * 模型切换
 */
function handleModelChange(value) {
  console.log('切换模型:', value);
}

/**
 * 渲染Markdown
 */
function renderMarkdown(content) {
  if (!content) return '';

  // 配置marked
  marked.setOptions({
    highlight: function(code, lang) {
      if (lang && hljs.getLanguage(lang)) {
        return hljs.highlight(code, { language: lang }).value;
      }
      return hljs.highlightAuto(code).value;
    },
    breaks: true,
    gfm: true,
  });

  // 渲染并净化HTML
  const rawHtml = marked.parse(content);
  return DOMPurify.sanitize(rawHtml);
}

/**
 * 复制消息
 */
function handleCopy(content) {
  navigator.clipboard.writeText(content).then(() => {
    AMessage.success('已复制');
  });
}

/**
 * 重新生成
 */
function handleRegenerate(msg) {
  // TODO: 实现重新生成逻辑
  AMessage.info('功能开发中');
}

/**
 * 附件上传
 */
function handleAttachment() {
  AMessage.info('附件上传功能开发中');
}

/**
 * 语音输入
 */
function handleVoice() {
  AMessage.info('语音输入功能开发中');
}

/**
 * 滚动到底部
 */
function scrollToBottom() {
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
  }
}

/**
 * 获取用户首字母
 */
function getUserInitial() {
  // TODO: 从用户信息获取
  return 'U';
}

/**
 * 格式化时间
 */
function formatTime(time) {
  return dayjs(time).format('MM-DD HH:mm');
}

/**
 * 格式化价格
 */
function formatPrice(inputPrice, outputPrice) {
  if (inputPrice === undefined || inputPrice === null || outputPrice === undefined || outputPrice === null) {
    return '免费';
  }

  const input = Number(inputPrice);
  const output = Number(outputPrice);

  if (isNaN(input) || isNaN(output)) {
    return '免费';
  }

  return `¥${input.toFixed(4)}/${output.toFixed(4)}`;
}
</script>

<style scoped>
/* Markdown内容样式 - 使用纯CSS替代Tailwind的@apply，避免HMR失效 */
.markdown-content :deep(pre) {
  background-color: #1f2937;
  color: #f3f4f6;
  border-radius: 0.5rem;
  padding: 1rem;
  overflow-x: auto;
}

.markdown-content :deep(code) {
  background-color: #f3f4f6;
  color: #dc2626;
  padding: 0.125rem 0.25rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
}

.markdown-content :deep(pre code) {
  background-color: transparent;
  color: #f3f4f6;
  padding: 0;
}

.markdown-content :deep(p) {
  margin-bottom: 0.75rem;
}

.markdown-content :deep(ul),
.markdown-content :deep(ol) {
  margin-left: 1rem;
  margin-bottom: 0.75rem;
}

.markdown-content :deep(li) {
  margin-bottom: 0.25rem;
}

.markdown-content :deep(h1),
.markdown-content :deep(h2),
.markdown-content :deep(h3) {
  font-weight: 600;
  margin-bottom: 0.75rem;
  margin-top: 1rem;
}

.markdown-content :deep(blockquote) {
  border-left: 4px solid #3b82f6;
  padding-left: 1rem;
  color: #4b5563;
  font-style: italic;
}

.markdown-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1rem;
}

.markdown-content :deep(th),
.markdown-content :deep(td) {
  border: 1px solid #d1d5db;
  padding: 0.5rem 1rem;
}

.markdown-content :deep(th) {
  background-color: #f3f4f6;
  font-weight: 600;
}
</style>

