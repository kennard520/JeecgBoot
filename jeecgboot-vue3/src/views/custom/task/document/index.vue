<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-select
          v-if="agentSelection.mode === 'select'"
          v-model:value="selectedAgentCode"
          :options="agentSelectOptions"
          style="width: 190px"
          aria-label="选择智能体"
          @change="rememberAgent"
        />
        <span v-else-if="agentSelection.mode === 'readonly'" class="agent-context">
          <Icon icon="ant-design:robot-outlined" />
          {{ agentSelection.options[0]?.agentName }}
        </span>
        <a-tooltip :title="uploadDisabled ? '当前账号未配置可用智能体，请联系管理员' : ''">
          <span>
            <a-upload :showUploadList="false" accept=".zip" :beforeUpload="handleBeforeUpload" :disabled="uploadDisabled">
              <a-button type="primary" :loading="uploading || agentsLoading" :disabled="uploadDisabled" preIcon="ant-design:upload-outlined">
                上传ZIP
              </a-button>
            </a-upload>
          </span>
        </a-tooltip>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" :disabled="!canBatchDelete" @click="batchHandleDelete">
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
  </div>
</template>

<script lang="ts" name="custom-task-document" setup>
  import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
  import type { UploadProps } from 'ant-design-vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { Icon } from '/@/components/Icon';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useGo } from '/@/hooks/web/usePage';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns, DOCUMENT_STATUS, searchFormSchema } from './Document.data';
  import { batchDelete, deleteOne, getMyAgents, list, startParse, uploadZip } from './Document.api';
  import { createDocumentPolling, type DocumentPollingController } from './documentPolling';
  import {
    canRetryDocument,
    createAgentSelectionState,
    getDocumentNavigationPath,
    isActiveDocumentStatus,
    normalizeAgentOptions,
    type AgentOption,
  } from './documentWorkflow';

  defineOptions({ name: 'CustomTaskDocument' });

  const go = useGo();
  const { createMessage } = useMessage();
  const uploading = ref(false);
  const agentsLoading = ref(true);
  const agents = ref<AgentOption[]>([]);
  const selectedAgentCode = ref<string>();
  const queryParam = {};
  const RECENT_AGENT_KEY = 'customs:recent-agent-code';
  let polling: DocumentPollingController | undefined;

  const agentSelection = computed(() => createAgentSelectionState(agents.value, selectedAgentCode.value));
  const agentSelectOptions = computed(() => agents.value.map((item) => ({ label: item.agentName, value: item.agentCode })));
  const uploadDisabled = computed(() => agentsLoading.value || agentSelection.value.uploadDisabled);

  const { tableContext } = useListPage({
    tableProps: {
      title: '文档解析任务',
      api: list,
      columns,
      canResize: false,
      formConfig: {
        labelWidth: 100,
        schemas: searchFormSchema,
        autoSubmitOnEnter: true,
        showAdvancedButton: true,
      },
      actionColumn: {
        width: 180,
        fixed: 'right',
      },
      beforeFetch: (params) => Object.assign(params, queryParam),
      afterFetch: (records) => {
        polling?.setActive(records.some((record) => isActiveDocumentStatus(record.status)));
        return records;
      },
    },
  });

  const [registerTable, { reload, getSelectRows }, { rowSelection, selectedRowKeys }] = tableContext;
  const canBatchDelete = computed(() => getSelectRows().every((item) => isNotStarted(item)));

  function isNotStarted(record) {
    return record?.status === DOCUMENT_STATUS.NOT_STARTED;
  }

  function isCompleted(record) {
    return record?.status === DOCUMENT_STATUS.COMPLETED;
  }

  polling = createDocumentPolling({
    reload: async () => {
      await reload();
    },
    isVisible: () => document.visibilityState === 'visible',
  });

  onMounted(() => {
    document.addEventListener('visibilitychange', handleVisibilityChange);
    void loadAgents();
  });

  onBeforeUnmount(() => {
    document.removeEventListener('visibilitychange', handleVisibilityChange);
    polling?.stop();
  });

  async function loadAgents() {
    agentsLoading.value = true;
    try {
      agents.value = normalizeAgentOptions(await getMyAgents());
      const remembered = localStorage.getItem(RECENT_AGENT_KEY) || undefined;
      selectedAgentCode.value = createAgentSelectionState(agents.value, remembered).selectedAgentCode;
      rememberAgent(selectedAgentCode.value);
    } catch (error: any) {
      agents.value = [];
      selectedAgentCode.value = undefined;
      createMessage.error(error?.message || '可用智能体加载失败');
    } finally {
      agentsLoading.value = false;
    }
  }

  function rememberAgent(agentCode?: string) {
    if (agentCode) {
      localStorage.setItem(RECENT_AGENT_KEY, agentCode);
    }
  }

  function handleVisibilityChange() {
    polling?.handleVisibilityChange();
  }

  const handleBeforeUpload: UploadProps['beforeUpload'] = async (file) => {
    if (!file.name.toLowerCase().endsWith('.zip')) {
      createMessage.warning('只能上传.zip压缩包');
      return false;
    }
    if (!selectedAgentCode.value) {
      createMessage.warning('当前账号未配置可用智能体，请联系管理员');
      return false;
    }
    uploading.value = true;
    try {
      await uploadZip(file as File, selectedAgentCode.value);
      createMessage.success('已上传并进入解析队列');
      await reload();
    } catch (error: any) {
      createMessage.error(error?.message || '上传失败');
    } finally {
      uploading.value = false;
    }
    return false;
  };

  async function handleStartParse(record) {
    await startParse(record.id);
    createMessage.success(record.status === DOCUMENT_STATUS.NOT_STARTED ? '已创建解析任务' : '已重新加入解析队列');
    await reload();
  }

  async function handleDelete(record) {
    if (!isNotStarted(record)) {
      createMessage.warning('只有未开始的文档允许删除');
      return;
    }
    await deleteOne({ id: record.id }, reload);
  }

  async function batchHandleDelete() {
    const rows = getSelectRows();
    if (!rows.every((item) => isNotStarted(item))) {
      createMessage.warning('只有未开始的文档允许删除，请取消选择解析中或已完成的数据');
      return;
    }
    await batchDelete({ ids: selectedRowKeys.value.join(',') }, handleSuccess);
  }

  function handleView(record) {
    const path = getDocumentNavigationPath(record);
    if (!path) {
      createMessage.warning('该文档尚未关联报关单');
      return;
    }
    go(path);
  }

  function handleSuccess() {
    selectedRowKeys.value = [];
    reload();
  }

  function getTableAction(record) {
    return [
      {
        label: '开始解析',
        onClick: handleStartParse.bind(null, record),
        ifShow: canRetryDocument(record?.status),
      },
      {
        label: '查看',
        onClick: handleView.bind(null, record),
        ifShow: isCompleted(record),
      },
    ];
  }

  function getDropDownAction(record) {
    return [
      {
        label: '删除',
        ifShow: isNotStarted(record),
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
      },
    ];
  }
</script>

<style lang="less" scoped>
  .agent-context {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    height: 32px;
    padding: 0 8px;
    color: rgba(0, 0, 0, 0.65);
  }
</style>
