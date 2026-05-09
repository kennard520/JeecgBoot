<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-upload :showUploadList="false" accept=".zip" :beforeUpload="handleBeforeUpload">
          <a-button type="primary" :loading="uploading" preIcon="ant-design:upload-outlined">上传ZIP</a-button>
        </a-upload>
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
  import { computed, ref } from 'vue';
  import type { UploadProps } from 'ant-design-vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { Icon } from '/@/components/Icon';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useGo } from '/@/hooks/web/usePage';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns, DOCUMENT_STATUS, searchFormSchema } from './Document.data';
  import { batchDelete, deleteOne, list, startParse, uploadZip } from './Document.api';

  const go = useGo();
  const { createMessage } = useMessage();
  const uploading = ref(false);
  const queryParam = {};

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

  const handleBeforeUpload: UploadProps['beforeUpload'] = async (file) => {
    if (!file.name.toLowerCase().endsWith('.zip')) {
      createMessage.warning('只能上传.zip压缩包');
      return false;
    }
    uploading.value = true;
    try {
      await uploadZip(file as File);
      createMessage.success('上传成功');
      await reload();
    } finally {
      uploading.value = false;
    }
    return false;
  };

  async function handleStartParse(record) {
    await startParse(record.id);
    createMessage.success('已创建解析任务');
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
    if (!record.decHeadId) {
      createMessage.warning('该文档尚未关联报关单');
      return;
    }
    go(record.singleWindowPath || `/custom/cit/single-window?decHeadId=${record.decHeadId}`);
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
        ifShow: isNotStarted(record),
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
