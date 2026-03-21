<!--
  * 角色 表权限
  *
  * @Author:    xzh
  * @Date:      2025-11-28
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
  *
-->
<template>
  <div>
    <div class="btn-group">
      <a-button class="button-style" type="primary" @click="saveTablePermissions" v-privilege="'system:role:tablePermission:update'">
        保存
      </a-button>
      <a-button class="button-style" @click="refreshData"> 刷新 </a-button>
      <a-button class="button-style" @click="clearCache"> 清空缓存 </a-button>
    </div>

    <!-- 表头 -->
    <a-row class="header">
      <a-col :span="4" class="tab-margin">业务模块</a-col>
      <a-col :span="16" class="tab-data">操作权限</a-col>
      <a-col :span="4" class="tab-margin">快捷操作</a-col>
    </a-row>

    <!-- 数据列表 -->
    <div class="data-container">
      <a-row class="data" align="middle" v-for="item in tablePermissionList" :key="item.moduleCode">
        <a-col :span="4" class="tab-margin">
          <strong>{{ item.moduleName }}</strong>
        </a-col>
        <a-col :span="16" class="tab-data">
          <a-checkbox-group v-model:value="item.selectedPermissions" style="width: 100%">
            <a-row>
              <a-col :span="6">
                <a-checkbox value="canView">
                  <EyeOutlined /> 查看
                </a-checkbox>
              </a-col>
              <a-col :span="6">
                <a-checkbox value="canAdd">
                  <PlusOutlined /> 新增
                </a-checkbox>
              </a-col>
              <a-col :span="6">
                <a-checkbox value="canEdit">
                  <EditOutlined /> 编辑
                </a-checkbox>
              </a-col>
              <a-col :span="6">
                <a-checkbox value="canDelete">
                  <DeleteOutlined /> 删除
                </a-checkbox>
              </a-col>
            </a-row>
            <a-row style="margin-top: 10px">
              <a-col :span="6">
                <a-checkbox value="canExport">
                  <DownloadOutlined /> 导出
                </a-checkbox>
              </a-col>
              <a-col :span="6">
                <a-checkbox value="canImport">
                  <UploadOutlined /> 导入
                </a-checkbox>
              </a-col>
              <a-col :span="6">
                <a-checkbox value="canPrint">
                  <PrinterOutlined /> 打印
                </a-checkbox>
              </a-col>
            </a-row>
          </a-checkbox-group>
        </a-col>
        <a-col :span="4" class="tab-margin">
          <a-space direction="vertical">
            <a-button size="small" @click="setAllPermissions(item, true)">全部授权</a-button>
            <a-button size="small" @click="setAllPermissions(item, false)">全部禁止</a-button>
            <a-button size="small" @click="setReadOnly(item)">仅查看</a-button>
          </a-space>
        </a-col>
      </a-row>

      <!-- 空状态 -->
      <a-empty v-if="tablePermissionList.length === 0" description="暂无数据" style="margin-top: 100px" />
    </div>
  </div>
</template>

<script setup>
  import { 
    EyeOutlined, 
    PlusOutlined, 
    EditOutlined, 
    DeleteOutlined, 
    DownloadOutlined, 
    UploadOutlined, 
    PrinterOutlined 
  } from '@ant-design/icons-vue';
  import { message } from 'ant-design-vue';
  import { inject, onMounted, ref, watch } from 'vue';
  import { tablePermissionApi } from '/@/api/system/table-permission-api';
  import { smartSentry } from '/@/lib/smart-sentry';
  import { SmartLoading } from '/@/components/framework/smart-loading';

  // ----------------------- 数据 ---------------------------------
  let selectRoleId = inject('selectRoleId');
  let tablePermissionList = ref([]);
  let moduleList = ref([]);

  // ----------------------- 生命周期 ---------------------------------
  watch(
    () => selectRoleId.value,
    () => loadTablePermissions()
  );

  onMounted(async () => {
    await loadModules();
    await loadTablePermissions();
  });

  // ----------------------- 加载数据 ---------------------------------

  // 加载模块列表
  async function loadModules() {
    try {
      let result = await tablePermissionApi.getModules();
      moduleList.value = result.data || [];
    } catch (e) {
      smartSentry.captureError(e);
    }
  }

  // 加载表权限
  async function loadTablePermissions() {
    if (!selectRoleId.value) {
      return;
    }

    try {
      SmartLoading.show();
      
      // 如果还没有加载模块列表，先加载
      if (moduleList.value.length === 0) {
        await loadModules();
      }

      // 遍历模块，查询每个模块的权限
      let permissionPromises = moduleList.value.map(module => 
        tablePermissionApi.queryTablePermission(selectRoleId.value, module.code)
          .then(res => res.data)
          .catch(() => null) // 如果查询失败，返回 null
      );
      
      let permissions = await Promise.all(permissionPromises);
      
      // 构建显示列表
      tablePermissionList.value = moduleList.value.map((module, index) => {
        let permission = permissions[index];
        let selectedPermissions = [];

        if (permission) {
          if (permission.canView) selectedPermissions.push('canView');
          if (permission.canAdd) selectedPermissions.push('canAdd');
          if (permission.canEdit) selectedPermissions.push('canEdit');
          if (permission.canDelete) selectedPermissions.push('canDelete');
          if (permission.canExport) selectedPermissions.push('canExport');
          if (permission.canImport) selectedPermissions.push('canImport');
          if (permission.canPrint) selectedPermissions.push('canPrint');
        }

        return {
          moduleCode: module.code,
          moduleName: module.name,
          selectedPermissions: selectedPermissions,
        };
      });
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // ----------------------- 快捷操作 ---------------------------------

  function setAllPermissions(item, grantAll) {
    if (grantAll) {
      item.selectedPermissions = ['canView', 'canAdd', 'canEdit', 'canDelete', 'canExport', 'canImport', 'canPrint'];
    } else {
      item.selectedPermissions = [];
    }
  }

  function setReadOnly(item) {
    item.selectedPermissions = ['canView'];
  }

  // ----------------------- 保存 ---------------------------------

  async function saveTablePermissions() {
    if (!selectRoleId.value) {
      message.warning('请先选择角色');
      return;
    }

    try {
      SmartLoading.show();
      
      // 批量保存，逐个调用保存接口
      let savePromises = tablePermissionList.value.map(item => {
        return tablePermissionApi.saveTablePermission({
          roleId: selectRoleId.value,
          moduleCode: item.moduleCode,
          canView: item.selectedPermissions.includes('canView'),
          canAdd: item.selectedPermissions.includes('canAdd'),
          canEdit: item.selectedPermissions.includes('canEdit'),
          canDelete: item.selectedPermissions.includes('canDelete'),
          canExport: item.selectedPermissions.includes('canExport'),
          canImport: item.selectedPermissions.includes('canImport'),
          canPrint: item.selectedPermissions.includes('canPrint'),
          remark: `角色ID ${selectRoleId.value} 的 ${item.moduleName} 权限配置`
        });
      });

      await Promise.all(savePromises);
      message.success('保存成功');
      await loadTablePermissions();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // ----------------------- 其他操作 ---------------------------------

  async function refreshData() {
    await loadTablePermissions();
    message.success('刷新成功');
  }

  async function clearCache() {
    try {
      await tablePermissionApi.clearCache();
      message.success('缓存已清空');
    } catch (e) {
      smartSentry.captureError(e);
    }
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
    padding: 15px 0;
  }

  .tab-data {
    padding: 15px;
  }

  .tab-margin {
    text-align: center;
    padding: 15px;
  }

  .data-container {
    height: 650px;
    overflow-y: auto;
  }

  .data {
    border-bottom: 1px solid #f2f2f2;
    padding: 15px 0;

    &:hover {
      background-color: #fafafa;
    }
  }
</style>
