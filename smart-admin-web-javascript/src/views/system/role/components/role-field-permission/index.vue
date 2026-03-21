<!--
  * 角色 字段权限
  *
  * @Author:    xzh
  * @Date:      2025-11-28
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
  *
-->
<template>
  <div>
    <div class="btn-group">
      <a-button class="button-style" type="primary" @click="saveFieldPermissions" v-privilege="'system:role:fieldPermission:update'">
        保存
      </a-button>
      <a-button class="button-style" @click="refreshData"> 刷新 </a-button>
      <a-button class="button-style" @click="clearCache"> 清空缓存 </a-button>
    </div>

    <!-- 模块选择 -->
    <a-row class="header">
      <a-col :span="6">
        <span style="margin-right: 10px">业务模块：</span>
        <a-select v-model:value="selectedModuleCode" style="width: 200px" @change="onModuleChange">
          <a-select-option v-for="module in moduleList" :key="module.code" :value="module.code">
            {{ module.name }}
          </a-select-option>
        </a-select>
      </a-col>
      <a-col :span="18" style="text-align: right">
        <a-space>
          <a-button size="small" @click="batchSetPermission('VISIBLE')">批量设置：可见</a-button>
          <a-button size="small" @click="batchSetPermission('HIDDEN')">批量设置：隐藏</a-button>
          <a-button size="small" @click="batchSetPermission('MASKED')">批量设置：脱敏</a-button>
          <a-button size="small" @click="batchSetPermission('READONLY')">批量设置：只读</a-button>
        </a-space>
      </a-col>
    </a-row>

    <!-- 表头 -->
    <a-row class="header" style="margin-top: 20px">
      <a-col :span="4" class="tab-margin">字段名称</a-col>
      <a-col :span="8" class="tab-data">权限类型</a-col>
      <a-col :span="6" class="tab-data">脱敏类型</a-col>
      <a-col :span="6" class="tab-margin">说明</a-col>
    </a-row>

    <!-- 数据列表 -->
    <div class="data-container">
      <a-row class="data" align="middle" v-for="(field, index) in fieldList" :key="field.fieldName">
        <a-col :span="4" class="tab-margin">
          {{ field.fieldLabel || field.fieldName }}
        </a-col>
        <a-col :span="8" class="tab-data">
          <a-radio-group v-model:value="field.permissionType">
            <a-radio class="radio-style" value="VISIBLE">可见</a-radio>
            <a-radio class="radio-style" value="HIDDEN">隐藏</a-radio>
            <a-radio class="radio-style" value="MASKED">脱敏</a-radio>
            <a-radio class="radio-style" value="READONLY">只读</a-radio>
            <a-radio class="radio-style" value="EDITABLE">可编辑</a-radio>
          </a-radio-group>
        </a-col>
        <a-col :span="6" class="tab-data">
          <a-select 
            v-model:value="field.maskType" 
            style="width: 100%" 
            :disabled="field.permissionType !== 'MASKED'"
            placeholder="选择脱敏类型">
            <a-select-option value="MOBILE">手机号</a-select-option>
            <a-select-option value="ID_CARD">身份证</a-select-option>
            <a-select-option value="BANK_CARD">银行卡</a-select-option>
            <a-select-option value="EMAIL">邮箱</a-select-option>
            <a-select-option value="NAME">姓名</a-select-option>
            <a-select-option value="ADDRESS">地址</a-select-option>
            <a-select-option value="CUSTOM">自定义</a-select-option>
          </a-select>
        </a-col>
        <a-col :span="6" class="tab-margin tab-desc">
          <p style="margin: 0">{{ getFieldDescription(field) }}</p>
        </a-col>
      </a-row>

      <!-- 空状态 -->
      <a-empty v-if="fieldList.length === 0" description="请先选择业务模块" style="margin-top: 100px" />
    </div>
  </div>
</template>

<script setup>
  import { message } from 'ant-design-vue';
  import { inject, onMounted, ref, watch } from 'vue';
  import { fieldPermissionApi } from '/@/api/system/field-permission-api';
  import { smartSentry } from '/@/lib/smart-sentry';

  // ----------------------- 数据 ---------------------------------
  let selectRoleId = inject('selectRoleId');
  let moduleList = ref([]);
  let selectedModuleCode = ref(undefined);
  let fieldList = ref([]);

  // ----------------------- 生命周期 ---------------------------------
  watch(
    () => selectRoleId.value,
    () => {
      if (selectedModuleCode.value) {
        loadFieldPermissions();
      }
    }
  );

  onMounted(async () => {
    await loadModules();
  });

  // ----------------------- 加载数据 ---------------------------------

  // 加载模块列表
  async function loadModules() {
    try {
      let result = await fieldPermissionApi.getModules();
      moduleList.value = result.data;
      if (moduleList.value.length > 0) {
        selectedModuleCode.value = moduleList.value[0].code;
        await loadFieldPermissions();
      }
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  // 模块切换
  async function onModuleChange() {
    await loadFieldPermissions();
  }

  // 加载字段权限
  async function loadFieldPermissions() {
    if (!selectRoleId.value || !selectedModuleCode.value) {
      return;
    }

    try {
      let result = await fieldPermissionApi.queryFieldPermissions(selectRoleId.value, selectedModuleCode.value);
      
      if (result.data && result.data.fields && result.data.fields.length > 0) {
        fieldList.value = result.data.fields.map(item => ({
          fieldName: item.fieldName,
          fieldLabel: item.fieldLabel || item.fieldName,
          permissionType: item.permissionType || 'VISIBLE',
          maskType: item.maskType
        }));
      } else {
        // 没有配置时，显示默认字段列表（根据模块动态生成）
        fieldList.value = getDefaultFieldsByModule(selectedModuleCode.value);
      }
    } catch (e) {
      smartSentry.captureError(e);
      // 如果查询失败，也显示默认字段
      fieldList.value = getDefaultFieldsByModule(selectedModuleCode.value);
    }
  }

  // 获取默认字段列表（根据模块）
  function getDefaultFieldsByModule(moduleCode) {
    const defaultFields = {
      'employee': [
        { fieldName: 'actualName', fieldLabel: '姓名', permissionType: 'VISIBLE' },
        { fieldName: 'phone', fieldLabel: '手机号', permissionType: 'VISIBLE' },
        { fieldName: 'idCard', fieldLabel: '身份证', permissionType: 'VISIBLE' },
        { fieldName: 'email', fieldLabel: '邮箱', permissionType: 'VISIBLE' },
        { fieldName: 'departmentId', fieldLabel: '部门', permissionType: 'VISIBLE' },
        { fieldName: 'positionId', fieldLabel: '职位', permissionType: 'VISIBLE' },
      ],
      'enterprise': [
        { fieldName: 'enterpriseName', fieldLabel: '企业名称', permissionType: 'VISIBLE' },
        { fieldName: 'contact', fieldLabel: '联系人', permissionType: 'VISIBLE' },
        { fieldName: 'contactPhone', fieldLabel: '联系电话', permissionType: 'VISIBLE' },
        { fieldName: 'bankName', fieldLabel: '开户银行', permissionType: 'VISIBLE' },
        { fieldName: 'bankAccount', fieldLabel: '银行账号', permissionType: 'VISIBLE' },
        { fieldName: 'unifiedSocialCreditCode', fieldLabel: '统一社会信用代码', permissionType: 'VISIBLE' },
      ],
      'invoice': [
        { fieldName: 'invoiceHead', fieldLabel: '发票抬头', permissionType: 'VISIBLE' },
        { fieldName: 'invoiceAmount', fieldLabel: '发票金额', permissionType: 'VISIBLE' },
        { fieldName: 'taxpayerIdNumber', fieldLabel: '纳税人识别号', permissionType: 'VISIBLE' },
      ],
      'bank': [
        { fieldName: 'accountName', fieldLabel: '账户名称', permissionType: 'VISIBLE' },
        { fieldName: 'accountNumber', fieldLabel: '账号', permissionType: 'VISIBLE' },
        { fieldName: 'bankName', fieldLabel: '开户银行', permissionType: 'VISIBLE' },
      ],
      'goods': [
        { fieldName: 'goodsName', fieldLabel: '商品名称', permissionType: 'VISIBLE' },
        { fieldName: 'price', fieldLabel: '价格', permissionType: 'VISIBLE' },
        { fieldName: 'stock', fieldLabel: '库存', permissionType: 'VISIBLE' },
      ],
      'customer': [
        { fieldName: 'customerName', fieldLabel: '客户名称', permissionType: 'VISIBLE' },
        { fieldName: 'contactPerson', fieldLabel: '联系人', permissionType: 'VISIBLE' },
        { fieldName: 'contactPhone', fieldLabel: '联系电话', permissionType: 'VISIBLE' },
      ],
    };
    return defaultFields[moduleCode] || [];
  }

  // ----------------------- 批量操作 ---------------------------------

  // 批量设置权限
  function batchSetPermission(permissionType) {
    fieldList.value.forEach(field => {
      field.permissionType = permissionType;
      if (permissionType !== 'MASKED') {
        field.maskType = undefined;
      }
    });
    message.success('批量设置成功');
  }

  // ----------------------- 保存 ---------------------------------

  async function saveFieldPermissions() {
    if (!selectRoleId.value || !selectedModuleCode.value) {
      message.warning('请先选择角色和模块');
      return;
    }

    try {
      await fieldPermissionApi.batchSaveFieldPermissions({
        roleId: selectRoleId.value,
        moduleCode: selectedModuleCode.value,
        fieldPermissions: fieldList.value.map(field => ({
          fieldName: field.fieldName,
          permissionType: field.permissionType,
          maskType: field.maskType
        }))
      });
      message.success('保存成功');
      await loadFieldPermissions();
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  // ----------------------- 其他操作 ---------------------------------

  async function refreshData() {
    await loadFieldPermissions();
    message.success('刷新成功');
  }

  async function clearCache() {
    try {
      await fieldPermissionApi.clearCache();
      message.success('缓存已清空');
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  function getFieldDescription(field) {
    const descriptions = {
      'VISIBLE': '该字段对当前角色可见',
      'HIDDEN': '该字段对当前角色隐藏',
      'MASKED': `该字段对当前角色脱敏显示（${field.maskType || '未设置'}）`,
      'READONLY': '该字段对当前角色只读',
      'EDITABLE': '该字段对当前角色可编辑'
    };
    return descriptions[field.permissionType] || '';
  }
</script>

<style scoped lang="less">
  .btn-group {
    text-align: right;
    margin-bottom: 20px;
  }

  .button-style {
    margin: 0 5px;
  }

  .header {
    border-bottom: 1px solid #f2f2f2;
    font-weight: 600;
    padding: 10px 0;
  }

  .tab-data {
    padding: 10px;
  }

  .tab-margin {
    text-align: center;
    padding: 10px;
  }

  .data-container {
    height: 600px;
    overflow-y: auto;
  }

  .data {
    border-bottom: 1px solid #f2f2f2;
    padding: 10px 0;

    &:hover {
      background-color: #fafafa;
    }
  }

  .radio-style {
    display: block;
    height: 30px;
    line-height: 30px;
  }

  .tab-desc {
    color: #888;
    font-size: 12px;
  }
</style>
