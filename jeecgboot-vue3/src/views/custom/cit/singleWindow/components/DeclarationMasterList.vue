<script setup lang="ts">
  import { computed, reactive } from 'vue';
  import type { CitTableColumn, DecHead } from '../types';

  const props = defineProps<{
    rows: DecHead[];
    columns: CitTableColumn[];
    loading: boolean;
    selectedRowKeys: Array<string | number>;
    mode?: 'sidebar' | 'workspace';
  }>();

  const emit = defineEmits<{
    search: [params: Record<string, any>];
    select: [record: DecHead];
    add: [];
  }>();

  const query = reactive({
    seqNo: '',
    entryId: '',
    tradeName: '',
  });

  const rowSelection = computed(() => ({
    type: 'radio',
    columnWidth: 34,
    selectedRowKeys: props.selectedRowKeys,
    onChange: (_keys: Array<string | number>, rows: DecHead[]) => {
      if (rows[0]) emit('select', rows[0]);
    },
  }));
  const isWorkspace = computed(() => props.mode === 'workspace');
  const tableScroll = computed(() => ({
    x: isWorkspace.value ? 900 : 620,
    y: isWorkspace.value ? 286 : 470,
  }));
  const pagination = computed(() => ({
    pageSize: isWorkspace.value ? 12 : 12,
    size: 'small' as const,
    showSizeChanger: false,
    showTotal: (total: number) => `共 ${total} 条`,
  }));

  function reset() {
    query.seqNo = '';
    query.entryId = '';
    query.tradeName = '';
    emit('search', {});
  }

  function search() {
    emit('search', { ...query });
  }

  function getRowClassName(record: DecHead) {
    return props.selectedRowKeys.includes(record.id!) ? 'is-active-row' : '';
  }

  function customRow(record: DecHead) {
    return {
      onClick: () => emit('select', record),
    };
  }
</script>

<template>
  <aside class="declaration-master-list" :class="{ 'is-workspace': isWorkspace }">
    <div class="declaration-master-list__title">
      <div class="declaration-master-list__heading">
        <span>报关单列表</span>
        <strong>{{ rows.length }}</strong>
      </div>
      <a-button preIcon="ant-design:plus-outlined" type="primary" @click="emit('add')">新增</a-button>
    </div>
    <a-form class="declaration-master-list__query" :model="query" :label-col="{ style: { width: '120px' } }" @keyup.enter="search">
      <a-row :gutter="[8, 6]">
        <a-col :xs="24" :md="8" :xl="8">
          <a-form-item label="统一编号">
            <a-input v-model:value="query.seqNo" placeholder="统一编号" allowClear  size="small"/>
          </a-form-item>
        </a-col>
        <a-col :xs="24" :md="8" :xl="8">
          <a-form-item label="海关编号">
            <a-input v-model:value="query.entryId" placeholder="海关编号" allowClear size="small"/>
          </a-form-item>
        </a-col>
        <a-col :xs="24" :md="8" :xl="8">
          <a-form-item label="收发货人">
            <a-input v-model:value="query.tradeName" placeholder="境内收发货人名称" allowClear size="small"/>
          </a-form-item>
        </a-col>
        <a-col :xs="24">
          <div class="declaration-master-list__actions">
            <a-button preIcon="ant-design:search-outlined" type="primary" @click="search">查询</a-button>
            <a-button preIcon="ant-design:reload-outlined" @click="reset">重置</a-button>
          </div>
        </a-col>
      </a-row>
    </a-form>
    <a-table
      class="declaration-master-list__table"
      size="small"
      rowKey="id"
      :columns="columns"
      :dataSource="rows"
      :loading="loading"
      :pagination="pagination"
      :rowSelection="rowSelection"
      :rowClassName="getRowClassName"
      :customRow="customRow"
      :scroll="tableScroll"
    >
      <template #bodyCell="{ column, text }">
        <template v-if="column.dataIndex === 'ieFlag'">
          <a-tag :color="text === 'E' ? 'orange' : 'blue'">{{ text === 'E' ? '出口' : '进口' }}</a-tag>
        </template>
      </template>
    </a-table>
  </aside>
</template>

<style scoped lang="less">
  .declaration-master-list {
    height: 100%;
    min-width: 320px;
    overflow: hidden;
    border: 1px solid #e5e7eb;
    border-radius: 4px;
    background: #fff;

    &__title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      min-height: 36px;
      padding: 0 12px;
      color: #1f2937;
      font-size: 13px;
      font-weight: 600;
      background: #fafafa;
      border-bottom: 1px solid #f0f0f0;
    }

    &__heading {
      display: flex;
      align-items: center;
      min-width: 0;
      gap: 8px;

      span {
        white-space: nowrap;
      }

      strong {
        min-width: 22px;
        height: 18px;
        padding: 0 6px;
        color: #1677ff;
        font-size: 12px;
        font-weight: 500;
        line-height: 18px;
        text-align: center;
        background: #e6f4ff;
        border: 1px solid #bae0ff;
        border-radius: 9px;
      }
    }

    &__query {
      padding: 8px 12px;
      border-bottom: 1px solid #f0f0f0;
      background: #fff;
    }

    &__actions {
      display: flex;
      align-items: center;
      justify-content: flex-end;
      gap: 6px;
      height: 28px;
    }

    :deep(.ant-form-item) {
      margin-bottom: 0;
    }

    :deep(.ant-form-item-label) {
      padding-right: 8px;
      line-height: 28px;
      text-align: right;
    }

    :deep(.ant-form-item-label > label) {
      height: 28px;
      color: #606266;
      font-size: 12px;
      white-space: nowrap;
    }

    :deep(.ant-form-item-control) {
      min-width: 0;
    }

    :deep(.ant-input),
    :deep(.ant-table) {
      font-size: 13px;
    }

    :deep(.ant-btn) {
      height: 24px;
      padding: 0 8px;
      font-size: 12px;
    }

    :deep(.ant-input) {
      width: 100%;
      min-height: 28px;
      border-radius: 4px;
    }

    :deep(.ant-input) {
      height: 26px;
      line-height: 26px;
    }

    :deep(.ant-table-wrapper) {
      padding: 0;
    }

    :deep(.ant-table-thead > tr > th) {
      height: 30px;
      padding: 5px 8px;
      color: #1f2937;
      background: #fafafa;
    }

    :deep(.ant-table-tbody > tr > td) {
      height: 30px;
      padding: 5px 8px;
      color: #303133;
      border-color: #f0f0f0;
      cursor: pointer;
    }

    :deep(.ant-table-tbody > tr:hover > td) {
      background: #f5faff;
    }

    :deep(.ant-table-tbody > tr.is-active-row > td) {
      background: #e6f4ff !important;
    }

    :deep(.ant-table-tbody > tr.is-active-row > td:first-child) {
      box-shadow: inset 3px 0 0 #1677ff;
    }

    :deep(.ant-table-pagination.ant-pagination) {
      margin: 8px 12px;
    }

    :deep(.ant-tag) {
      margin-inline-end: 0;
      line-height: 18px;
    }

    &.is-workspace {
      margin-top: 8px;

      .declaration-master-list__query {
        :deep(.ant-row) {
          align-items: flex-end;
        }
      }
    }

    &.is-workspace &__query {
      .declaration-master-list__actions {
        justify-content: flex-end;
      }
    }
  }
</style>
