<template>
  <div>
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:user-add-outlined" @click="handleCreate">新增授权</a-button>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableActions(record)" />
      </template>
    </BasicTable>
    <UserAgentGrantModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" name="custom-ai-agent-grant" setup>
  import { BasicTable, TableAction, type BasicColumn, type FormSchema } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import UserAgentGrantModal from './components/UserAgentGrantModal.vue';
  import { deleteAgentGrant, listAgentGrants } from './AgentGrant.api';

  defineOptions({ name: 'CustomAiAgentGrant' });

  const columns: BasicColumn[] = [
    { title: '客户编码', dataIndex: 'customerCode', width: 130 },
    { title: '客户名称', dataIndex: 'customerName', width: 160, ellipsis: true },
    { title: '用户账号', dataIndex: 'username', width: 140 },
    { title: '用户姓名', dataIndex: 'realname', width: 120 },
    {
      title: '可用智能体',
      dataIndex: 'agentCodes',
      width: 240,
      ellipsis: true,
      customRender: ({ text, record }) => formatAgents(record.agentNames || text),
    },
    {
      title: '默认智能体',
      dataIndex: 'defaultAgentName',
      width: 150,
      customRender: ({ text, record }) => text || record.defaultAgentCode || '-',
    },
    { title: '更新时间', dataIndex: 'updatedAt', width: 170 },
  ];

  const searchFormSchema: FormSchema[] = [
    { label: '客户编码', field: 'customerCode', component: 'Input' },
    { label: '用户账号', field: 'username', component: 'Input' },
    { label: '智能体', field: 'agentCode', component: 'Input' },
  ];

  const [registerModal, { openModal }] = useModal();
  const { tableContext } = useListPage({
    tableProps: {
      title: '客户与智能体授权',
      api: listAgentGrants,
      columns,
      canResize: false,
      pagination: false,
      formConfig: {
        labelWidth: 80,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: false,
      },
      actionColumn: { width: 150, fixed: 'right' },
      rowKey: 'userId',
      defSort: { column: 'updatedAt', order: 'desc' },
    },
  });

  const [registerTable, { reload }] = tableContext;

  function formatAgents(value: unknown) {
    if (Array.isArray(value)) {
      return value.join('、') || '-';
    }
    return `${value || '-'}`.replaceAll(',', '、');
  }

  function handleCreate() {
    openModal(true, { isUpdate: false });
  }

  function handleEdit(record) {
    openModal(true, { isUpdate: true, record });
  }

  function getTableActions(record) {
    return [
      { label: '编辑', onClick: handleEdit.bind(null, record) },
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除该用户的智能体授权？',
          confirm: async () => {
            await deleteAgentGrant(record);
            await reload();
          },
        },
        ifShow: Array.isArray(record.agentCodes) ? record.agentCodes.length > 0 : !!record.agentCodes,
      },
    ];
  }
</script>
