<!--
  * AI配置管理页面
  * 
  * @Author:    1024创新实验室
  * @Date:      2025-11-30
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
-->
<template>
  <a-card size="small" :bordered="false" :hoverable="true">
    <!-- 查询表单 -->
    <a-form layout="inline" :model="queryForm" class="smart-query-form">
      <a-form-item label="搜索" class="smart-query-form-item">
        <a-input
          style="width: 300px"
          v-model:value="queryForm.searchWord"
          placeholder="模型名称/显示名称/提供商"
        />
      </a-form-item>

      <a-form-item label="提供商" class="smart-query-form-item">
        <a-select
          style="width: 150px"
          v-model:value="queryForm.provider"
          placeholder="全部"
          allowClear
        >
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="DASHSCOPE">阿里云</a-select-option>
          <a-select-option value="OPENAI">OpenAI</a-select-option>
          <a-select-option value="ANTHROPIC">Anthropic</a-select-option>
          <a-select-option value="ZHIPU">智谱AI</a-select-option>
          <a-select-option value="MOONSHOT">月之暗面</a-select-option>
          <a-select-option value="DEEPSEEK">DeepSeek</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item label="状态" class="smart-query-form-item">
        <a-select
          style="width: 100px"
          v-model:value="queryForm.enabledFlag"
          placeholder="全部"
          allowClear
        >
          <a-select-option :value="null">全部</a-select-option>
          <a-select-option :value="true">启用</a-select-option>
          <a-select-option :value="false">禁用</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item class="smart-query-form-item">
        <a-button type="primary" @click="queryData">
          <template #icon>
            <SearchOutlined />
          </template>
          查询
        </a-button>
        <a-button @click="resetQuery" class="smart-margin-left10">
          <template #icon>
            <ReloadOutlined />
          </template>
          重置
        </a-button>
      </a-form-item>
    </a-form>

    <!-- 操作按钮 -->
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <a-button @click="showForm()" type="primary">
          <template #icon>
            <PlusOutlined />
          </template>
          新建配置
        </a-button>
        <a-button @click="batchDelete" danger :disabled="selectedRowKeyList.length === 0">
          <template #icon>
            <DeleteOutlined />
          </template>
          批量删除
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.SUPPORT.AI_CONFIG" :refresh="queryData" />
      </div>
    </a-row>

    <!-- 数据表格 -->
    <a-table
      size="small"
      :dataSource="tableData"
      :columns="columns"
      rowKey="id"
      bordered
      :loading="tableLoading"
      :pagination="false"
      :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
    >
      <template #bodyCell="{ column, record, index }">
        <template v-if="column.dataIndex === 'provider'">
          <a-tag :color="getProviderColor(record.provider)">
            {{ record.providerDisplayName || record.provider }}
          </a-tag>
        </template>

        <template v-if="column.dataIndex === 'modelName'">
          <div>
            <div class="font-semibold">{{ record.modelDisplayName }}</div>
            <div class="text-xs text-gray-400">{{ record.modelName }}</div>
          </div>
        </template>

        <template v-if="column.dataIndex === 'apiKey'">
          <div class="flex items-center space-x-2">
            <span v-if="record.hasApiKey" class="text-green-600">{{ record.apiKeyMasked }}</span>
            <span v-else class="text-red-500">未配置</span>
            <a-tooltip title="API Key已加密存储" v-if="record.hasApiKey">
              <SafetyOutlined class="text-green-600" />
            </a-tooltip>
          </div>
        </template>

        <template v-if="column.dataIndex === 'price'">
          <div class="text-xs">
            <div>输入: ¥{{ record.priceInput }}/1K</div>
            <div>输出: ¥{{ record.priceOutput }}/1K</div>
          </div>
        </template>

        <template v-if="column.dataIndex === 'features'">
          <div class="flex flex-wrap gap-1">
            <a-tag v-if="record.supportStream" color="blue" class="text-xs">流式</a-tag>
            <a-tag v-if="record.supportFunctionCall" color="green" class="text-xs">函数</a-tag>
            <a-tag v-if="record.supportVision" color="purple" class="text-xs">视觉</a-tag>
          </div>
        </template>

        <template v-if="column.dataIndex === 'enabledFlag'">
          <a-switch
            :checked="record.enabledFlag"
            @change="(checked) => handleStatusChange(record.id, checked)"
            checked-children="启用"
            un-checked-children="禁用"
          />
        </template>

        <template v-if="column.dataIndex === 'defaultFlag'">
          <a-tag v-if="record.defaultFlag" color="gold">
            <template #icon><StarFilled /></template>
            默认
          </a-tag>
          <a-button v-else size="small" type="link" @click="setDefault(record.id)">
            设为默认
          </a-button>
        </template>

        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button @click="testConnection(record.id)" type="link" size="small">
              <template #icon><ApiOutlined /></template>
              测试
            </a-button>
            <a-button @click="showForm(record)" type="link" size="small">
              <template #icon><EditOutlined /></template>
              编辑
            </a-button>
            <a-button @click="deleteConfig(record.id)" danger type="link" size="small">
              <template #icon><DeleteOutlined /></template>
              删除
            </a-button>
          </div>
        </template>
      </template>
    </a-table>

    <!-- 分页 -->
    <div class="smart-query-table-page">
      <a-pagination
        showSizeChanger
        showQuickJumper
        show-less-items
        :pageSizeOptions="PAGE_SIZE_OPTIONS"
        :defaultPageSize="queryForm.pageSize"
        v-model:current="queryForm.pageNum"
        v-model:pageSize="queryForm.pageSize"
        :total="total"
        @change="queryData"
        @showSizeChange="queryData"
        :show-total="(total) => `共${total}条`"
      />
    </div>

    <!-- 表单弹窗 -->
    <AIConfigFormModal ref="formModalRef" @reloadList="queryData" />
  </a-card>
</template>

<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import {
    SearchOutlined,
    ReloadOutlined,
    PlusOutlined,
    DeleteOutlined,
    EditOutlined,
    ApiOutlined,
    SafetyOutlined,
    StarFilled,
  } from '@ant-design/icons-vue';
  import { aiConfigApi } from '/@/api/support/ai-config-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import AIConfigFormModal from './ai-config-form-modal.vue';

  // ----------------------- 查询表单 -----------------------
  const queryFormState = {
    searchWord: '',
    provider: '',
    modelType: '',
    enabledFlag: null,
    pageNum: 1,
    pageSize: 10,
  };
  const queryForm = reactive({ ...queryFormState });
  const tableLoading = ref(false);
  const tableData = ref([]);
  const total = ref(0);
  const selectedRowKeyList = ref([]);

  // ----------------------- 表格列 -----------------------
  const columns = ref([
    {
      title: '提供商',
      dataIndex: 'provider',
      width: 120,
    },
    {
      title: '模型信息',
      dataIndex: 'modelName',
      width: 200,
    },
    {
      title: 'API Key',
      dataIndex: 'apiKey',
      width: 180,
    },
    {
      title: '价格',
      dataIndex: 'price',
      width: 120,
    },
    {
      title: '最大Token',
      dataIndex: 'maxTokens',
      width: 100,
    },
    {
      title: '特性',
      dataIndex: 'features',
      width: 150,
    },
    {
      title: '状态',
      dataIndex: 'enabledFlag',
      width: 100,
    },
    {
      title: '默认',
      dataIndex: 'defaultFlag',
      width: 100,
    },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 200,
    },
  ]);

  // ----------------------- 查询数据 -----------------------
  async function queryData() {
    try {
      tableLoading.value = true;
      const res = await aiConfigApi.queryPage(queryForm);
      tableData.value = res.data.list;
      total.value = res.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  function resetQuery() {
    Object.assign(queryForm, queryFormState);
    queryData();
  }

  function onSelectChange(selectedRowKeys) {
    selectedRowKeyList.value = selectedRowKeys;
  }

  // ----------------------- 表单操作 -----------------------
  const formModalRef = ref();

  function showForm(record) {
    formModalRef.value.show(record);
  }

  // ----------------------- 删除 -----------------------
  function deleteConfig(id) {
    Modal.confirm({
      title: '提示',
      content: '确定要删除该配置吗？删除后将无法恢复。',
      okText: '删除',
      okType: 'danger',
      onOk: async () => {
        try {
          await aiConfigApi.delete(id);
          message.success('删除成功');
          queryData();
        } catch (e) {
          smartSentry.captureError(e);
        }
      },
      cancelText: '取消',
    });
  }

  function batchDelete() {
    if (selectedRowKeyList.value.length === 0) {
      message.warning('请选择要删除的配置');
      return;
    }

    Modal.confirm({
      title: '提示',
      content: `确定要删除选中的 ${selectedRowKeyList.value.length} 个配置吗？`,
      okText: '删除',
      okType: 'danger',
      onOk: async () => {
        try {
          await aiConfigApi.batchDelete(selectedRowKeyList.value);
          message.success('删除成功');
          selectedRowKeyList.value = [];
          queryData();
        } catch (e) {
          smartSentry.captureError(e);
        }
      },
      cancelText: '取消',
    });
  }

  // ----------------------- 状态切换 -----------------------
  async function handleStatusChange(id, enabled) {
    try {
      await aiConfigApi.updateStatus(id, enabled);
      message.success(enabled ? '已启用' : '已禁用');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
      queryData(); // 切换失败，重新加载数据恢复状态
    }
  }

  // ----------------------- 设置默认 -----------------------
  async function setDefault(id) {
    try {
      await aiConfigApi.setDefault(id);
      message.success('已设置为默认模型');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  // ----------------------- 测试连接 -----------------------
  async function testConnection(id) {
    try {
      message.loading({ content: '正在测试连接...', key: 'testConnection' });
      await aiConfigApi.testConnection(id);
      message.success({ content: '连接测试成功', key: 'testConnection' });
    } catch (e) {
      message.error({ content: '连接测试失败', key: 'testConnection' });
      smartSentry.captureError(e);
    }
  }

  // ----------------------- 工具方法 -----------------------
  function getProviderColor(provider) {
    const colorMap = {
      DASHSCOPE: 'orange',
      OPENAI: 'green',
      ANTHROPIC: 'purple',
      ZHIPU: 'blue',
      MOONSHOT: 'cyan',
      DEEPSEEK: 'red',
    };
    return colorMap[provider] || 'default';
  }

  // ----------------------- 生命周期 -----------------------
  onMounted(() => {
    queryData();
  });
</script>

<style lang="less" scoped>
  .font-semibold {
    font-weight: 600;
  }

  .text-xs {
    font-size: 12px;
  }

  .text-gray-400 {
    color: #9ca3af;
  }

  .text-green-600 {
    color: #16a34a;
  }

  .text-red-500 {
    color: #ef4444;
  }

  .flex {
    display: flex;
  }

  .items-center {
    align-items: center;
  }

  .flex-wrap {
    flex-wrap: wrap;
  }

  .space-x-2 > * + * {
    margin-left: 8px;
  }

  .gap-1 {
    gap: 4px;
  }
</style>





