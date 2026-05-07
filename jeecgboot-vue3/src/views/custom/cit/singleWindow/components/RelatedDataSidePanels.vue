<script setup lang="ts">
  import { computed, reactive, ref, shallowRef, watch } from 'vue';
  import { Modal } from 'ant-design-vue';
  import { cloneDeep } from 'lodash-es';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { deleteCitEntity, queryCitRecords, saveCitEntity } from '../cit.api';
  import type { CitFieldConfig, CitRecord, ParaOptionLoadingMap, ParaOptionMap, ParaOptionSourceKey, RelatedTabConfig } from '../types';

  const props = defineProps<{
    headId?: string | number;
    goodsId?: string | number;
    configs: RelatedTabConfig[];
    optionMap?: ParaOptionMap;
    optionLoadingMap?: ParaOptionLoadingMap;
  }>();

  const emit = defineEmits<{
    optionSearch: [source: ParaOptionSourceKey, keyword?: string];
  }>();

  const { createMessage } = useMessage();
  const loadingMap = reactive<Record<string, boolean>>({});
  const savingMap = reactive<Record<string, boolean>>({});
  const rowsMap = ref<Record<string, CitRecord[]>>({});
  const modelMap = ref<Record<string, CitRecord>>({});

  function parentIdOf(config: RelatedTabConfig) {
    return config.parentSource === 'head' ? props.headId : props.goodsId;
  }

  function withParentFields(config: RelatedTabConfig, record: CitRecord = {}) {
    const parentId = parentIdOf(config);
    const result = { ...record };
    if (parentId) result[config.parentField] = parentId;
    if (props.headId && config.formFields.some((item) => item.field === 'decHeadId')) result.decHeadId = props.headId;
    if (props.goodsId && config.formFields.some((item) => item.field === 'decListId')) result.decListId = props.goodsId;
    return result;
  }

  function rowsOf(config: RelatedTabConfig) {
    return rowsMap.value[config.key] || [];
  }

  function modelOf(config: RelatedTabConfig) {
    if (!modelMap.value[config.key]) {
      modelMap.value = { ...modelMap.value, [config.key]: withParentFields(config, {}) };
    }
    return modelMap.value[config.key];
  }

  function columnsOf(config: RelatedTabConfig) {
    return config.columns;
  }

  function rowSelectionOf(config: RelatedTabConfig) {
    return computed(() => ({
      type: 'radio',
      columnWidth: 34,
      selectedRowKeys: modelOf(config).id ? [modelOf(config).id] : [],
      onChange: (_keys: Array<string | number>, rows: CitRecord[]) => {
        if (rows[0]) selectRecord(config, rows[0]);
      },
    })).value;
  }

  async function loadConfig(config: RelatedTabConfig) {
    const parentId = parentIdOf(config);
    if (!parentId) {
      rowsMap.value = { ...rowsMap.value, [config.key]: [] };
      modelMap.value = { ...modelMap.value, [config.key]: withParentFields(config, {}) };
      return;
    }
    loadingMap[config.key] = true;
    try {
      const rows = await queryCitRecords(config.key, { [config.parentField]: parentId });
      rowsMap.value = { ...rowsMap.value, [config.key]: rows };
      const selected = modelMap.value[config.key]?.id;
      const nextModel = selected ? rows.find((item) => item.id === selected) : rows[0];
      modelMap.value = { ...modelMap.value, [config.key]: withParentFields(config, nextModel ? cloneDeep(nextModel) : {}) };
    } finally {
      loadingMap[config.key] = false;
    }
  }

  function assertParentReady(config: RelatedTabConfig) {
    if (!props.headId) {
      createMessage.warning('请先暂存表头');
      return false;
    }
    if (config.parentSource === 'goods' && !props.goodsId) {
      createMessage.warning('请先选择商品明细');
      return false;
    }
    return true;
  }

  function newRecord(config: RelatedTabConfig) {
    if (!assertParentReady(config)) return;
    modelMap.value = { ...modelMap.value, [config.key]: withParentFields(config, {}) };
  }

  function selectRecord(config: RelatedTabConfig, record: CitRecord) {
    modelMap.value = { ...modelMap.value, [config.key]: withParentFields(config, cloneDeep(record)) };
  }

  async function saveRecord(config: RelatedTabConfig) {
    if (!assertParentReady(config)) return;
    savingMap[config.key] = true;
    try {
      const payload = withParentFields(config, modelOf(config));
      await saveCitEntity(config.key, payload, Boolean(payload.id));
      await loadConfig(config);
    } finally {
      savingMap[config.key] = false;
    }
  }

  function deleteRecord(config: RelatedTabConfig) {
    const record = modelOf(config);
    if (!record.id) {
      createMessage.warning(`请选择${config.title}记录`);
      return;
    }
    Modal.confirm({
      title: '确认删除',
      content: `是否删除当前${config.title}记录？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        await deleteCitEntity(config.key, record.id!);
        await loadConfig(config);
      },
    });
  }

  function placeholder(item: CitFieldConfig) {
    if (item.placeholder) return item.placeholder;
    return item.type === 'select' || item.type === 'date' ? `请选择${item.label}` : `请输入${item.label}`;
  }

  function fieldDisabled(item: CitFieldConfig) {
    return item.readonly || item.field === 'decHeadId' || item.field === 'decListId';
  }

  function selectOptions(item: CitFieldConfig) {
    if (item.options) return item.options;
    return item.optionSource ? props.optionMap?.[item.optionSource] || [] : [];
  }

  function selectLoading(item: CitFieldConfig) {
    return Boolean(item.optionSource && props.optionLoadingMap?.[item.optionSource]);
  }

  function handleSelectFocus(item: CitFieldConfig) {
    if (item.optionSource) emit('optionSearch', item.optionSource);
  }

  function handleSelectSearch(item: CitFieldConfig, keyword: string) {
    if (item.optionSource) emit('optionSearch', item.optionSource, keyword);
  }

  function visibleFields(config: RelatedTabConfig) {
    return config.formFields.filter((item) => item.field !== 'decHeadId' && item.field !== 'decListId');
  }

  function customRow(config: RelatedTabConfig) {
    return (record: CitRecord) => ({
      onClick: () => selectRecord(config, record),
    });
  }

  function tableHeight(config: RelatedTabConfig) {
    return config.key === 'decContainer' ? 166 : 92;
  }

  function tablePageSize(config: RelatedTabConfig) {
    return config.key === 'decContainer' ? 5 : 3;
  }

  watch(
    () => [props.headId, props.goodsId, props.configs.map((item) => item.key).join(',')],
    () => props.configs.forEach((config) => loadConfig(config)),
    { immediate: true }
  );
</script>

<template>
  <section class="related-side-panels">
    <article v-for="config in configs" :key="config.key" class="related-side-panels__panel">
      <div class="related-side-panels__toolbar">
        <span>{{ config.title }}</span>
        <div>
          <a-button preIcon="ant-design:reload-outlined" @click="loadConfig(config)">刷新</a-button>
          <a-button preIcon="ant-design:plus-outlined" @click="newRecord(config)">新增</a-button>
          <a-button preIcon="ant-design:save-outlined" type="primary" :loading="savingMap[config.key]" @click="saveRecord(config)">保存</a-button>
          <a-button preIcon="ant-design:delete-outlined" danger @click="deleteRecord(config)">删除</a-button>
        </div>
      </div>
      <a-table
        size="small"
        rowKey="id"
        :columns="columnsOf(config)"
        :dataSource="rowsOf(config)"
        :loading="loadingMap[config.key]"
        :pagination="{ pageSize: tablePageSize(config), size: 'small', showSizeChanger: false }"
        :rowSelection="rowSelectionOf(config)"
        :customRow="customRow(config)"
        :scroll="{ x: 420, y: tableHeight(config) }"
      />
      <a-form class="related-side-panels__form" :model="modelOf(config)" :label-col="{ style: { width: '96px' } }">
        <a-form-item v-for="item in visibleFields(config)" :key="item.field" :label="item.label" :class="{ 'is-required-field': item.required }">
          <a-select
            v-if="item.type === 'select'"
            v-model:value="modelOf(config)[item.field]"
            :options="selectOptions(item)"
            :loading="selectLoading(item)"
            :disabled="fieldDisabled(item)"
            :placeholder="placeholder(item)"
            :filterOption="!item.optionSource"
            optionFilterProp="label"
            size="small"
            showSearch
            allowClear
            @focus="handleSelectFocus(item)"
            @search="handleSelectSearch(item, $event)"
          />
          <a-input-number
            v-else-if="item.type === 'number'"
            v-model:value="modelOf(config)[item.field]"
            :disabled="fieldDisabled(item)"
            :placeholder="placeholder(item)"
            :min="0"
            size="small"
          />
          <a-input
            v-else
            v-model:value="modelOf(config)[item.field]"
            :disabled="fieldDisabled(item)"
            :placeholder="placeholder(item)"
            :maxlength="item.maxLength"
            size="small"
            allowClear
          />
        </a-form-item>
      </a-form>
    </article>
  </section>
</template>

<style scoped lang="less">
  .related-side-panels {
    display: flex;
    flex-direction: column;
    gap: 8px;

    &__panel {
      overflow: hidden;
      border: 1px solid #e5e7eb;
      border-radius: 4px;
      background: #fff;
    }

    &__toolbar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      min-height: 32px;
      padding: 0 8px 0 10px;
      color: #1f2937;
      font-size: 13px;
      font-weight: 600;
      background: #fafafa;
      border-bottom: 1px solid #f0f0f0;

      > div {
        display: flex;
        gap: 4px;
      }
    }

    &__form {
      padding: 6px 8px 8px;
      border-top: 1px solid #f0f0f0;
    }

    :deep(.ant-btn) {
      height: 24px;
      padding: 0 7px;
      font-size: 12px;
    }

    :deep(.ant-table-thead > tr > th) {
      height: 29px;
      padding: 5px 6px;
      color: #1f2937;
      font-size: 12px;
      background: #fafafa;
    }

    :deep(.ant-table-tbody > tr > td) {
      height: 29px;
      padding: 5px 6px;
      font-size: 12px;
      cursor: pointer;
    }

    :deep(.ant-table-pagination.ant-pagination) {
      margin: 6px 8px;
    }

    :deep(.ant-form-item) {
      margin-bottom: 5px;
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

    :deep(.ant-input),
    :deep(.ant-input-number),
    :deep(.ant-select-selector) {
      width: 100%;
      min-height: 28px;
      font-size: 13px;
      border-radius: 4px;
    }

    :deep(.ant-input),
    :deep(.ant-input-number-input) {
      height: 26px;
      line-height: 26px;
    }

    :deep(.ant-input-number),
    :deep(.ant-select-single .ant-select-selector) {
      height: 28px;
    }

    :deep(.ant-select-single .ant-select-selector .ant-select-selection-item),
    :deep(.ant-select-single .ant-select-selector .ant-select-selection-placeholder) {
      line-height: 26px;
    }

    :deep(.is-required-field .ant-input:not([disabled])),
    :deep(.is-required-field .ant-input-number:not(.ant-input-number-disabled)),
    :deep(.is-required-field .ant-select-selector) {
      background-color: #fffbe6;
    }
  }
</style>
