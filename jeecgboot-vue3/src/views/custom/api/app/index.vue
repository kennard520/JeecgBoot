<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleCreate">新增客户</a-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined" />
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button>
            批量操作
            <Icon icon="mdi:chevron-down" />
          </a-button>
        </a-dropdown>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
    </BasicTable>

    <CustomApiAppDrawer @register="registerDrawer" @success="handleDrawerSuccess" />

    <a-modal v-model:open="secretVisible" title="请保存 AppSecret" width="640px" :footer="null">
      <a-alert type="warning" showIcon message="AppSecret 只在新增或重置时显示一次，关闭后无法再次查看。" class="mb-4" />
      <a-descriptions bordered size="small" :column="1">
        <a-descriptions-item label="AppKey">{{ secretPayload.appKey }}</a-descriptions-item>
        <a-descriptions-item label="AppSecret">
          <a-input-group compact>
            <a-input :value="secretPayload.appSecret" readonly style="width: calc(100% - 88px)" />
            <a-button type="primary" @click="copySecret">复制</a-button>
          </a-input-group>
        </a-descriptions-item>
        <a-descriptions-item label="Token接口">POST /custom/api/auth/token</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script lang="ts" name="custom-api-app" setup>
  import { reactive, ref } from 'vue';
  import { Modal } from 'ant-design-vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { Icon } from '/@/components/Icon';
  import { useDrawer } from '/@/components/Drawer';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useMessage } from '/@/hooks/web/useMessage';
  import CustomApiAppDrawer from './components/CustomApiAppDrawer.vue';
  import { batchDelete, clearAccessToken, deleteOne, list, resetSecret, saveOrUpdate } from './CustomApiApp.api';
  import { columns, searchFormSchema } from './CustomApiApp.data';
  import { normalizeApiAppAgentFields } from './apiAppAgentModel';

  defineOptions({ name: 'CustomApiApp' });

  const { createMessage } = useMessage();
  const [registerDrawer, { openDrawer }] = useDrawer();
  const secretVisible = ref(false);
  const secretPayload = reactive({
    appKey: '',
    appSecret: '',
  });

  const { tableContext } = useListPage({
    tableProps: {
      title: 'API客户管理',
      api: list,
      columns,
      canResize: false,
      formConfig: {
        labelWidth: 80,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
      },
      actionColumn: {
        width: 220,
        fixed: 'right',
      },
      defSort: {
        column: 'id',
        order: 'desc',
      },
    },
  });

  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  function handleCreate() {
    openDrawer(true, {
      isUpdate: false,
    });
  }

  function handleEdit(record) {
    openDrawer(true, {
      record,
      isUpdate: true,
    });
  }

  async function handleDrawerSuccess(result) {
    showSecretIfNeeded(result);
    await reload();
  }

  async function handleToggleEnabled(record) {
    const agentFields = normalizeApiAppAgentFields(record);
    await saveOrUpdate(
      {
        id: record.id,
        appKey: record.appKey,
        customerCode: record.customerCode,
        ...agentFields,
        rateLimit: record.rateLimit,
        enabled: `${record.enabled}` === '1' ? 0 : 1,
      },
      true
    );
    createMessage.success(`${record.enabled}` === '1' ? '已停用' : '已启用');
    await reload();
  }

  function handleResetSecret(record) {
    Modal.confirm({
      title: '确认重置密钥',
      content: '重置后旧 AppSecret 和已签发 token 会立即失效，是否继续？',
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const result = await resetSecret(record.id);
        showSecretIfNeeded(result);
        await reload();
      },
    });
  }

  async function handleClearAccessToken(record) {
    await clearAccessToken(record.id);
    createMessage.success('已清空 access token');
    await reload();
  }

  async function handleDelete(record) {
    await deleteOne({ id: record.id }, reload);
  }

  async function batchHandleDelete() {
    await batchDelete({ ids: selectedRowKeys.value.join(',') }, handleSuccess);
  }

  function handleSuccess() {
    selectedRowKeys.value = [];
    reload();
  }

  function showSecretIfNeeded(result) {
    if (!result?.appSecret) {
      return;
    }
    secretPayload.appKey = result.appKey;
    secretPayload.appSecret = result.appSecret;
    secretVisible.value = true;
  }

  async function copySecret() {
    await navigator.clipboard.writeText(secretPayload.appSecret);
    createMessage.success('已复制 AppSecret');
  }

  function getTableAction(record) {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
      {
        label: `${record.enabled}` === '1' ? '停用' : '启用',
        popConfirm: {
          title: `${record.enabled}` === '1' ? '确认停用该客户？' : '确认启用该客户？',
          confirm: handleToggleEnabled.bind(null, record),
        },
      },
    ];
  }

  function getDropDownAction(record) {
    return [
      {
        label: '重置密钥',
        onClick: handleResetSecret.bind(null, record),
      },
      {
        label: '清空Token',
        ifShow: record.hasAccessToken,
        popConfirm: {
          title: '确认清空该客户当前 access token？',
          confirm: handleClearAccessToken.bind(null, record),
        },
      },
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
      },
    ];
  }
</script>
