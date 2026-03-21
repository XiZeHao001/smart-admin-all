<!--
  * AI配置表单弹窗
  * 
  * @Author:    1024创新实验室
  * @Date:      2025-11-30
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
-->
<template>
  <a-modal
    :open="visible"
    :title="form.id ? '编辑AI配置' : '新建AI配置'"
    @ok="onSubmit"
    @cancel="onClose"
    :width="800"
    :maskClosable="false"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }">
      <a-form-item label="AI提供商" name="provider">
        <a-select v-model:value="form.provider" placeholder="请选择AI提供商" @change="onProviderChange">
          <a-select-option value="DASHSCOPE">阿里云-通义千问</a-select-option>
          <a-select-option value="OPENAI">OpenAI</a-select-option>
          <a-select-option value="ANTHROPIC">Anthropic-Claude</a-select-option>
          <a-select-option value="ZHIPU">智谱AI-GLM</a-select-option>
          <a-select-option value="MOONSHOT">月之暗面-Kimi</a-select-option>
          <a-select-option value="DEEPSEEK">DeepSeek</a-select-option>
          <a-select-option value="BAIDU">百度-文心一言</a-select-option>
          <a-select-option value="XFYUN">讯飞-星火</a-select-option>
          <a-select-option value="TENCENT">腾讯-混元</a-select-option>
          <a-select-option value="MINIMAX">MiniMax</a-select-option>
          <a-select-option value="LINGYI">零一万物</a-select-option>
          <a-select-option value="GOOGLE">Google-Gemini</a-select-option>
          <a-select-option value="OTHER">其他</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="模型名称" name="modelName">
        <a-input v-model:value="form.modelName" placeholder="如: qwen-plus, gpt-4" />
      </a-form-item>

      <a-form-item label="模型显示名称" name="modelDisplayName">
        <a-input v-model:value="form.modelDisplayName" placeholder="如: 通义千问Plus" />
      </a-form-item>

      <a-form-item label="API Key" name="apiKey">
        <a-input-password
          v-model:value="form.apiKey"
          :placeholder="form.id ? '留空则不修改（已配置：' + (form.apiKeyMasked || '****') + '）' : '请输入API Key（将自动加密存储）'"
          :maxLength="500"
        >
          <template #prefix>
            <SafetyOutlined />
          </template>
        </a-input-password>
        <div class="text-xs text-gray-500 mt-1">
          <SafetyOutlined /> API Key将使用AES-256加密后存储
          <span v-if="form.id && form.hasApiKey" class="ml-2 text-green-600">
            ✓ 已配置Key（{{ form.apiKeyMasked }}）
          </span>
        </div>
      </a-form-item>

      <a-form-item label="API Secret" name="apiSecret">
        <a-input-password
          v-model:value="form.apiSecret"
          placeholder="部分平台需要（如讯飞星火）"
          :maxLength="500"
        />
      </a-form-item>

      <a-form-item label="Base URL" name="baseUrl">
        <a-input v-model:value="form.baseUrl" placeholder="自定义API地址（可选）" />
      </a-form-item>

      <a-row>
        <a-col :span="12">
          <a-form-item label="最大Token" name="maxTokens" :label-col="{ span: 10 }">
            <a-input-number v-model:value="form.maxTokens" :min="1" :max="128000" style="width: 100%" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="温度参数" name="temperature" :label-col="{ span: 10 }">
            <a-input-number v-model:value="form.temperature" :min="0" :max="2" :step="0.1" style="width: 100%" />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row>
        <a-col :span="12">
          <a-form-item label="输入价格" name="priceInput" :label-col="{ span: 10 }">
            <a-input-number
              v-model:value="form.priceInput"
              :min="0"
              :step="0.0001"
              :precision="6"
              style="width: 100%"
              placeholder="元/1K tokens"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="输出价格" name="priceOutput" :label-col="{ span: 10 }">
            <a-input-number
              v-model:value="form.priceOutput"
              :min="0"
              :step="0.0001"
              :precision="6"
              style="width: 100%"
              placeholder="元/1K tokens"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-form-item label="模型特性">
        <a-checkbox-group v-model:value="features">
          <a-checkbox value="supportStream">支持流式输出</a-checkbox>
          <a-checkbox value="supportFunctionCall">支持函数调用</a-checkbox>
          <a-checkbox value="supportVision">支持视觉</a-checkbox>
        </a-checkbox-group>
      </a-form-item>

      <a-row>
        <a-col :span="12">
          <a-form-item label="启用状态" name="enabledFlag" :label-col="{ span: 10 }">
            <a-switch v-model:checked="form.enabledFlag" checked-children="启用" un-checked-children="禁用" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="设为默认" name="defaultFlag" :label-col="{ span: 10 }">
            <a-switch v-model:checked="form.defaultFlag" checked-children="是" un-checked-children="否" />
          </a-form-item>
        </a-col>
      </a-row>

      <a-form-item label="排序" name="sortOrder">
        <a-input-number v-model:value="form.sortOrder" :min="0" style="width: 100%" />
      </a-form-item>

      <a-form-item label="备注" name="remark">
        <a-textarea v-model:value="form.remark" :rows="3" placeholder="配置说明或注意事项" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
  import { reactive, ref, nextTick } from 'vue';
  import { message } from 'ant-design-vue';
  import { SafetyOutlined } from '@ant-design/icons-vue';
  import { aiConfigApi } from '/@/api/support/ai-config-api';
  import { smartSentry } from '/@/lib/smart-sentry';

  // ----------------------- 组件 -----------------------
  const emit = defineEmits(['reloadList']);
  defineExpose({ show });

  const formRef = ref();
  const visible = ref(false);

  const formDefault = {
    id: null,
    provider: '',
    modelName: '',
    modelDisplayName: '',
    apiKey: '',
    apiSecret: '',
    baseUrl: '',
    maxTokens: 2048,
    temperature: 0.7,
    enabledFlag: true,
    defaultFlag: false,
    modelType: 'CHAT',
    supportStream: true,
    supportFunctionCall: false,
    supportVision: false,
    priceInput: 0,
    priceOutput: 0,
    sortOrder: 0,
    remark: '',
  };

  let form = reactive({ ...formDefault });
  const features = ref([]);

  const rules = {
    provider: [{ required: true, message: '请选择AI提供商' }],
    modelName: [{ required: true, message: '请输入模型名称' }],
    modelDisplayName: [{ required: true, message: '请输入模型显示名称' }],
    apiKey: [
      { 
        required: false,  // 编辑时可以不填（留空表示不修改）
        validator: (rule, value) => {
          // 新增时必填
          if (!form.id && !value) {
            return Promise.reject('请输入API Key');
          }
          return Promise.resolve();
        }
      }
    ],
    maxTokens: [{ required: true, message: '请输入最大Token数' }],
    temperature: [{ required: true, message: '请输入温度参数' }],
  };

  // ----------------------- 显示弹窗 -----------------------
  async function show(record) {
    visible.value = true;
    features.value = [];

    await nextTick();

    if (record && record.id) {
      // 编辑模式：加载详情
      try {
        const res = await aiConfigApi.getDetail(record.id);
        Object.assign(form, res.data);
        
        // 编辑时清空API Key，避免显示加密串
        // 用户可以选择：留空=不修改，填写=更新为新值
        form.apiKey = '';

        // 设置特性复选框
        if (form.supportStream) features.value.push('supportStream');
        if (form.supportFunctionCall) features.value.push('supportFunctionCall');
        if (form.supportVision) features.value.push('supportVision');
      } catch (e) {
        smartSentry.captureError(e);
      }
    } else {
      // 新增模式
      Object.assign(form, formDefault);
    }

    formRef.value?.clearValidate();
  }

  // ----------------------- 提交 -----------------------
  async function onSubmit() {
    try {
      await formRef.value.validate();

      // 处理特性
      form.supportStream = features.value.includes('supportStream');
      form.supportFunctionCall = features.value.includes('supportFunctionCall');
      form.supportVision = features.value.includes('supportVision');

      if (form.id) {
        await aiConfigApi.update(form);
        message.success('更新成功');
      } else {
        await aiConfigApi.add(form);
        message.success('新建成功');
      }

      emit('reloadList');
      onClose();
    } catch (e) {
      if (e.errorFields) {
        // 表单验证错误
        return;
      }
      smartSentry.captureError(e);
    }
  }

  // ----------------------- 关闭 -----------------------
  function onClose() {
    Object.assign(form, formDefault);
    features.value = [];
    formRef.value?.clearValidate();
    visible.value = false;
  }

  // ----------------------- 提供商切换 -----------------------
  function onProviderChange(value) {
    // 根据提供商设置默认的BaseURL
    const baseUrlMap = {
      DASHSCOPE: 'https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation',
      OPENAI: 'https://api.openai.com/v1/chat/completions',
      ANTHROPIC: 'https://api.anthropic.com/v1/messages',
      ZHIPU: 'https://open.bigmodel.cn/api/paas/v4/chat/completions',
      MOONSHOT: 'https://api.moonshot.cn/v1/chat/completions',
      DEEPSEEK: 'https://api.deepseek.com/v1/chat/completions',
      GOOGLE: 'https://generativelanguage.googleapis.com/v1/models',
    };

    if (baseUrlMap[value]) {
      form.baseUrl = baseUrlMap[value];
    }
  }
</script>

<style lang="less" scoped>
  .text-xs {
    font-size: 12px;
  }

  .text-gray-500 {
    color: #6b7280;
  }

  .mt-1 {
    margin-top: 4px;
  }
</style>



