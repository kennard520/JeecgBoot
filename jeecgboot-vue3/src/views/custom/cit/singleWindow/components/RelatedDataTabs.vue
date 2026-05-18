<script setup lang="ts">
  import { computed, ref, shallowRef, watch } from 'vue';
  import { Modal } from 'ant-design-vue';
  import { cloneDeep } from 'lodash-es';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { deleteCitEntity, queryCitById, queryCitRecords, saveCitEntity } from '../cit.api';
  import type { CitFieldConfig, CitRecord, CitSelectOption, ParaOptionLoadingMap, ParaOptionMap, ParaOptionSourceKey, RelatedTabConfig } from '../types';

  const props = defineProps<{
    headId?: string | number;
    goodsId?: string | number;
    configs: RelatedTabConfig[];
    optionMap?: ParaOptionMap;
    optionLoadingMap?: ParaOptionLoadingMap;
    expanded?: boolean;
    expandable?: boolean;
    title?: string;
  }>();

  const emit = defineEmits<{
    optionSearch: [source: ParaOptionSourceKey, keyword?: string];
    'update:expanded': [value: boolean];
  }>();

  const { createMessage } = useMessage();
  const activeKey = shallowRef(props.configs[0]?.key);
  const loading = shallowRef(false);
  const rowsMap = ref<Record<string, CitRecord[]>>({});
  const editorVisible = shallowRef(false);
  const editorSaving = shallowRef(false);
  const editorModel = ref<CitRecord>({});
  const formRef = ref<any>();
  const previewVisible = shallowRef(false);
  const previewLoading = shallowRef(false);
  const previewRecord = ref<CitRecord>({});
  const previewImageSrc = shallowRef('');

  const currentConfig = computed(() => props.configs.find((item) => item.key === activeKey.value) || props.configs[0]);
  const currentRows = computed(() => rowsMap.value[currentConfig.value?.key || ''] || []);
  const currentNeedsGoods = computed(() => currentConfig.value?.parentSource === 'goods');
  const editorTitle = computed(() => `${editorModel.value.id ? '编辑' : '新增'}${currentConfig.value?.title || ''}`);
  const tableColumns = computed(() => [
    ...(currentConfig.value?.columns || []),
    { title: '操作', dataIndex: 'action', width: 110, fixed: 'right', align: 'center' },
  ]);
  const paginationConfig = computed(() => ({ pageSize: props.expanded ? 10 : 6, size: 'small' as const }));
  const tableScroll = computed(() => ({ x: 820, y: props.expanded ? 360 : 196 }));
  const editorRules = computed(() => {
    const result: Record<string, any[]> = {};
    currentConfig.value?.formFields.forEach((item) => {
      if (item.required) {
        result[item.field] = [{ required: true, message: `请输入${item.label}`, trigger: item.type === 'select' ? 'change' : 'blur' }];
      }
    });
    return result;
  });

  function getParentId(config?: RelatedTabConfig) {
    if (!config) return undefined;
    return config.parentSource === 'head' ? props.headId : props.goodsId;
  }

  function withParentFields(record: CitRecord = {}, config = currentConfig.value) {
    const parentId = getParentId(config);
    const result = { ...record };
    if (config && parentId) result[config.parentField] = parentId;
    if (props.headId && config?.formFields.some((item) => item.field === 'decHeadId')) result.decHeadId = props.headId;
    if (props.goodsId && config?.formFields.some((item) => item.field === 'decListId')) result.decListId = props.goodsId;
    return result;
  }

  async function loadByKey(key?: RelatedTabConfig['key']) {
    const config = props.configs.find((item) => item.key === key);
    if (!config) return;
    const parentId = getParentId(config);
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

  async function loadCurrent() {
    await loadByKey(currentConfig.value?.key);
  }

  async function activate(key: RelatedTabConfig['key']) {
    if (!props.configs.some((item) => item.key === key)) return;
    activeKey.value = key;
    await loadByKey(key);
  }

  function assertParentReady(config = currentConfig.value) {
    if (!props.headId) {
      createMessage.warning('请先暂存表头');
      return false;
    }
    if (config?.parentSource === 'goods' && !props.goodsId) {
      createMessage.warning('请先选择商品明细');
      return false;
    }
    return true;
  }

  function openAdd() {
    if (!assertParentReady(currentConfig.value)) return;
    editorModel.value = withParentFields({});
    editorVisible.value = true;
  }

  function openAddByKey(key: RelatedTabConfig['key']) {
    const config = props.configs.find((item) => item.key === key);
    if (!config) return;
    if (!assertParentReady(config)) return;
    activeKey.value = key;
    editorModel.value = withParentFields({}, config);
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

  function imageMimeFromName(fileName?: string) {
    const ext = String(fileName || '').split('.').pop()?.toLowerCase();
    const mimeMap: Record<string, string> = {
      jpg: 'image/jpeg',
      jpeg: 'image/jpeg',
      png: 'image/png',
      gif: 'image/gif',
      bmp: 'image/bmp',
      webp: 'image/webp',
    };
    return (ext && mimeMap[ext]) || 'image/*';
  }

  function arrayBufferToBase64(bytes: number[]) {
    let binary = '';
    bytes.forEach((byte) => {
      binary += String.fromCharCode(byte & 0xff);
    });
    return btoa(binary);
  }

  function normalizeAttachment(value: unknown) {
    if (!value) return '';
    if (typeof value === 'string') return value;
    if (Array.isArray(value)) return arrayBufferToBase64(value);
    return '';
  }

  async function openPreview(record: CitRecord) {
    previewVisible.value = true;
    previewLoading.value = true;
    previewImageSrc.value = '';
    previewRecord.value = record;
    try {
      const detail = record.id ? await queryCitById('decMarkLob', record.id) : record;
      const attachment = normalizeAttachment(detail.attachment || record.attachment);
      if (!attachment) {
        previewVisible.value = false;
        createMessage.warning('当前附件没有可预览内容');
        return;
      }
      previewRecord.value = detail;
      previewImageSrc.value = attachment.startsWith('data:') ? attachment : `data:${imageMimeFromName(detail.attachName)};base64,${attachment}`;
    } finally {
      previewLoading.value = false;
    }
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

  function filterSelectOption(input: string, option: any) {
    const keyword = input.trim().toLowerCase();
    if (!keyword) return true;
    const label = String(option?.label ?? '').toLowerCase();
    const value = String(option?.value ?? '').toLowerCase();
    return label.includes(keyword) || value.includes(keyword);
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

  function recordValue(record: CitRecord | undefined, ...keys: string[]) {
    if (!record) return undefined;
    const normalizedMap = new Map(Object.keys(record).map((key) => [key.replace(/_/g, '').toLowerCase(), record[key]]));
    for (const key of keys) {
      const value = normalizedMap.get(key.replace(/_/g, '').toLowerCase());
      if (value !== undefined && value !== null && value !== '') return value;
    }
    return undefined;
  }

  function applyCredentialDefaults(option?: CitSelectOption | CitSelectOption[]) {
    const selected = Array.isArray(option) ? option[0] : option;
    const sourceRecord = selected?.sourceRecord;
    editorModel.value.applOri = sourceRecord ? String(recordValue(sourceRecord, 'count') ?? '') : '';
    editorModel.value.applCopyQuan = sourceRecord ? String(recordValue(sourceRecord, 'subCount', 'sub_count') ?? '') : '';
  }

  function handleSelectChange(item: CitFieldConfig, option?: CitSelectOption | CitSelectOption[]) {
    if (item.field === 'appCertCode') {
      applyCredentialDefaults(option);
    }
  }

  function toggleExpanded() {
    emit('update:expanded', !props.expanded);
  }

  watch(
    () => props.configs,
    (configs) => {
      if (!configs.some((item) => item.key === activeKey.value)) {
        activeKey.value = configs[0]?.key;
      }
    },
    { immediate: true }
  );
  watch([activeKey, () => props.headId, () => props.goodsId], () => loadCurrent(), { immediate: true });

  defineExpose({ loadCurrent, loadByKey, activate, openAddByKey });
</script>

<template>
  <section class="related-data-tabs" :class="{ 'is-expanded': expanded }">
    <div class="related-data-tabs__header">
      <span>{{ title || '集装箱及随附资料' }}</span>
      <div class="related-data-tabs__actions">
        <a-button preIcon="ant-design:reload-outlined" @click="loadCurrent">刷新</a-button>
        <a-button v-if="expandable" :preIcon="expanded ? 'ant-design:fullscreen-exit-outlined' : 'ant-design:fullscreen-outlined'" @click="toggleExpanded">
          {{ expanded ? '收起' : '展开' }}
        </a-button>
        <a-button v-if="!currentConfig?.hideAdd" preIcon="ant-design:plus-outlined" type="primary" @click="openAdd">新增</a-button>
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
      :pagination="paginationConfig"
      :scroll="tableScroll"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a-space :size="4">
            <a-button v-if="currentConfig?.key === 'decMarkLob'" type="link" size="small" @click="openPreview(record)">预览</a-button>
            <a-button v-else type="link" size="small" @click="openEdit(record)">编辑</a-button>
            <a-button type="link" size="small" danger @click="handleDelete(record)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal v-model:open="editorVisible" :title="editorTitle" :confirmLoading="editorSaving" width="760px" @ok="handleSave">
      <a-form ref="formRef" class="related-data-tabs__form" :model="editorModel" :rules="editorRules" :label-col="{ style: { width: '112px' } }">
        <a-row :gutter="[8, 2]">
          <a-col v-for="item in (currentConfig?.formFields || []).filter((field) => !field.hidden)" :key="item.field" :xs="24" :md="item.span === 24 ? 24 : 12">
            <a-form-item :label="item.label" :name="item.field" :class="{ 'is-required-field': item.required }">
              <a-select
              size="small"
                v-if="item.type === 'select'"
                v-model:value="editorModel[item.field]"
                :options="selectOptions(item)"
                :loading="selectLoading(item)"
                :disabled="fieldDisabled(item)"
                :placeholder="placeholder(item)"
                :filterOption="filterSelectOption"
                optionFilterProp="label"
                showSearch
                allowClear
                @focus="handleSelectFocus(item)"
                @search="handleSelectSearch(item, $event)"
                @change="(_value, option) => handleSelectChange(item, option)"
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

    <a-modal v-model:open="previewVisible" :title="previewRecord.attachName || '附件预览'" width="760px" :footer="null">
      <div class="related-data-tabs__preview">
        <a-spin v-if="previewLoading" />
        <img v-else-if="previewImageSrc" class="related-data-tabs__preview-image" :src="previewImageSrc" alt="附件预览" />
      </div>
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

    &__preview {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 240px;
      background: #f8fafc;
      border: 1px solid #e5e7eb;
      border-radius: 4px;
    }

    &__preview-image {
      display: block;
      max-width: 100%;
      max-height: 70vh;
      object-fit: contain;
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
