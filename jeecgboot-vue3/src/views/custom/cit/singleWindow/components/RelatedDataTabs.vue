<script setup lang="ts">
  import { computed, ref, shallowRef, watch } from 'vue';
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
  const activeKey = shallowRef(props.configs[0]?.key);
  const loading = shallowRef(false);
  const rowsMap = ref<Record<string, CitRecord[]>>({});
  const editorVisible = shallowRef(false);
  const editorSaving = shallowRef(false);
  const editorModel = ref<CitRecord>({});
  const formRef = ref<any>();

  const currentConfig = computed(() => props.configs.find((item) => item.key === activeKey.value) || props.configs[0]);
  const currentRows = computed(() => rowsMap.value[currentConfig.value?.key || ''] || []);
  const currentParentId = computed(() => {
    const config = currentConfig.value;
    if (!config) return undefined;
    return config.parentSource === 'head' ? props.headId : props.goodsId;
  });
  const currentNeedsGoods = computed(() => currentConfig.value?.parentSource === 'goods');
  const editorTitle = computed(() => `${editorModel.value.id ? '编辑' : '新增'}${currentConfig.value?.title || ''}`);
  const tableColumns = computed(() => [
    ...(currentConfig.value?.columns || []),
    { title: '操作', dataIndex: 'action', width: 110, fixed: 'right', align: 'center' },
  ]);
  const editorRules = computed(() => {
    const result: Record<string, any[]> = {};
    currentConfig.value?.formFields.forEach((item) => {
      if (item.required) {
        result[item.field] = [{ required: true, message: `请输入${item.label}`, trigger: item.type === 'select' ? 'change' : 'blur' }];
      }
    });
    return result;
  });

  function withParentFields(record: CitRecord = {}) {
    const config = currentConfig.value;
    const parentId = currentParentId.value;
    const result = { ...record };
    if (config && parentId) result[config.parentField] = parentId;
    if (props.headId && currentConfig.value?.formFields.some((item) => item.field === 'decHeadId')) result.decHeadId = props.headId;
    if (props.goodsId && currentConfig.value?.formFields.some((item) => item.field === 'decListId')) result.decListId = props.goodsId;
    return result;
  }

  async function loadCurrent() {
    const config = currentConfig.value;
    if (!config) return;
    const parentId = currentParentId.value;
    if (!parentId) {
      rowsMap.value = { ...rowsMap.value, [config.key]: [] };
      return;
    }
    loading.value = true;
    try {
      const rows = await queryCitRecords(config.key, { [config.parentField]: parentId });
      rowsMap.value = { ...rowsMap.value, [config.key]: rows };
    } finally {
      loading.value = false;
    }
  }

  function assertParentReady() {
    if (!props.headId) {
      createMessage.warning('请先暂存表头');
      return false;
    }
    if (currentNeedsGoods.value && !props.goodsId) {
      createMessage.warning('请先选择商品明细');
      return false;
    }
    return true;
  }

  function openAdd() {
    if (!assertParentReady()) return;
    editorModel.value = withParentFields({});
    editorVisible.value = true;
  }

  function openEdit(record: CitRecord) {
    editorModel.value = withParentFields(cloneDeep(record));
    editorVisible.value = true;
  }

  async function handleSave() {
    const config = currentConfig.value;
    if (!config) return;
    await formRef.value?.validate();
    editorSaving.value = true;
    try {
      const payload = withParentFields(editorModel.value);
      await saveCitEntity(config.key, payload, Boolean(payload.id));
      editorVisible.value = false;
      await loadCurrent();
    } finally {
      editorSaving.value = false;
    }
  }

  function handleDelete(record: CitRecord) {
    const config = currentConfig.value;
    if (!config || !record.id) return;
    Modal.confirm({
      title: '确认删除',
      content: `是否删除当前${config.title}记录？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        await deleteCitEntity(config.key, record.id!);
        await loadCurrent();
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
    if (item.optionSource) {
      emit('optionSearch', item.optionSource);
    }
  }

  function handleSelectSearch(item: CitFieldConfig, keyword: string) {
    if (item.optionSource) {
      emit('optionSearch', item.optionSource, keyword);
    }
  }

  watch([activeKey, () => props.headId, () => props.goodsId], () => loadCurrent(), { immediate: true });
</script>

<template>
  <section class="related-data-tabs">
    <div class="related-data-tabs__header">
      <span>集装箱及随附资料</span>
      <div class="related-data-tabs__actions">
        <a-button preIcon="ant-design:reload-outlined" @click="loadCurrent">刷新</a-button>
        <a-button preIcon="ant-design:plus-outlined" type="primary" @click="openAdd">新增</a-button>
      </div>
    </div>
    <a-tabs v-model:activeKey="activeKey" size="small">
      <a-tab-pane v-for="item in configs" :key="item.key" :tab="item.title" />
    </a-tabs>
    <div v-if="currentNeedsGoods && !goodsId" class="related-data-tabs__tip">选择一条商品明细后，可维护当前页签数据。</div>
    <a-table
      size="small"
      rowKey="id"
      :columns="tableColumns"
      :dataSource="currentRows"
      :loading="loading"
      :pagination="{ pageSize: 6, size: 'small' }"
      :scroll="{ x: 820, y: 196 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a-space :size="4">
            <a-button type="link" size="small" @click="openEdit(record)">编辑</a-button>
            <a-button type="link" size="small" danger @click="handleDelete(record)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal v-model:open="editorVisible" :title="editorTitle" :confirmLoading="editorSaving" width="760px" @ok="handleSave">
      <a-form ref="formRef" class="related-data-tabs__form" :model="editorModel" :rules="editorRules" :label-col="{ style: { width: '112px' } }">
        <a-row :gutter="[8, 2]">
          <a-col v-for="item in currentConfig?.formFields || []" :key="item.field" :xs="24" :md="item.span === 24 ? 24 : 12">
            <a-form-item :label="item.label" :name="item.field" :class="{ 'is-required-field': item.required }">
              <a-select
              size="small"
                v-if="item.type === 'select'"
                v-model:value="editorModel[item.field]"
                :options="selectOptions(item)"
                :loading="selectLoading(item)"
                :disabled="fieldDisabled(item)"
                :placeholder="placeholder(item)"
                :filterOption="!item.optionSource"
                optionFilterProp="label"
                showSearch
                allowClear
                @focus="handleSelectFocus(item)"
                @search="handleSelectSearch(item, $event)"
              />
              <a-date-picker
              size="small"
                v-else-if="item.type === 'date'"
                v-model:value="editorModel[item.field]"
                valueFormat="YYYY-MM-DD"
                :disabled="fieldDisabled(item)"
                :placeholder="placeholder(item)"
              />
              <a-input-number
              size="small"
                v-else-if="item.type === 'number'"
                v-model:value="editorModel[item.field]"
                :disabled="fieldDisabled(item)"
                :placeholder="placeholder(item)"
                :min="0"
              />
              <a-textarea
              size="small"
                v-else-if="item.type === 'textarea'"
                v-model:value="editorModel[item.field]"
                :disabled="fieldDisabled(item)"
                :placeholder="placeholder(item)"
                :maxlength="item.maxLength"
                :autoSize="{ minRows: 1, maxRows: 3 }"
              />
              <a-input
              size="small"
                v-else
                v-model:value="editorModel[item.field]"
                :disabled="fieldDisabled(item)"
                :placeholder="placeholder(item)"
                :maxlength="item.maxLength"
                allowClear
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </section>
</template>

<style scoped lang="less">
  .related-data-tabs {
    overflow: hidden;
    border: 1px solid #e5e7eb;
    border-radius: 4px;
    background: #fff;

    &__header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      min-height: 32px;
      padding: 0 12px;
      color: #1f2937;
      font-size: 13px;
      font-weight: 600;
      background: #fafafa;
      border-bottom: 1px solid #f0f0f0;
    }

    &__actions {
      display: flex;
      gap: 4px;
    }

    &__tip {
      margin: 4px 8px;
      padding: 4px 8px;
      color: #8c6d1f;
      font-size: 12px;
      background: #fffbe6;
      border: 1px solid #ffe58f;
    }

    &__form {
      padding-top: 8px;
    }

    :deep(.ant-tabs-nav) {
      margin: 0;
      padding: 0 10px;
      background: #fff;
      border-bottom: 1px solid #f0f0f0;
    }

    :deep(.ant-tabs-tab) {
      padding: 7px 0;
      font-size: 13px;
    }

    :deep(.ant-tabs-tab-active .ant-tabs-tab-btn) {
      color: #1677ff;
    }

    :deep(.ant-table-thead > tr > th) {
      height: 30px;
      padding: 5px 8px;
      color: #1f2937;
      font-size: 13px;
      background: #fafafa;
    }

    :deep(.ant-table-tbody > tr > td) {
      height: 30px;
      padding: 5px 8px;
      font-size: 13px;
    }

    :deep(.ant-btn) {
      height: 24px;
      padding: 0 8px;
      font-size: 12px;
    }

    :deep(.ant-form-item) {
      margin-bottom: 5px;
    }

    :deep(.ant-input),
    :deep(.ant-input-number),
    :deep(.ant-picker),
    :deep(.ant-select-selector) {
      width: 100%;
      min-height: 28px;
      font-size: 13px;
      border-radius: 4px;
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
    :deep(.ant-input-number-input),
    :deep(.ant-picker-input > input) {
      height: 26px;
      line-height: 26px;
    }

    :deep(.ant-input-number),
    :deep(.ant-picker),
    :deep(.ant-select-single .ant-select-selector) {
      height: 28px;
    }

    :deep(.ant-select-single .ant-select-selector .ant-select-selection-search-input) {
      height: 26px;
    }

    :deep(.ant-select-single .ant-select-selector .ant-select-selection-item),
    :deep(.ant-select-single .ant-select-selector .ant-select-selection-placeholder) {
      line-height: 26px;
    }

    :deep(.is-required-field .ant-input:not([disabled])),
    :deep(.is-required-field .ant-input-number:not(.ant-input-number-disabled)),
    :deep(.is-required-field .ant-picker:not(.ant-picker-disabled)),
    :deep(.is-required-field .ant-select-selector) {
      background-color: #fffbe6;
    }
  }
</style>
