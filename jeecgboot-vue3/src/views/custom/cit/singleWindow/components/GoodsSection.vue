<script setup lang="ts">
  import { computed, nextTick, ref } from 'vue';
  import type { CitFieldConfig, CitSelectOption, CitTableColumn, DecList, ParaOptionLoadingMap, ParaOptionMap, ParaOptionSourceKey } from '../types';

  const props = defineProps<{
    rows: DecList[];
    columns: CitTableColumn[];
    fields: CitFieldConfig[];
    loading: boolean;
    saving: boolean;
    hasHead: boolean;
    optionMap?: ParaOptionMap;
    optionLoadingMap?: ParaOptionLoadingMap;
  }>();

  const emit = defineEmits<{
    save: [];
    create: [record: DecList, close: () => void];
    select: [record: DecList];
    delete: [record: DecList];
    optionSearch: [source: ParaOptionSourceKey, keyword?: string];
  }>();

  const model = defineModel<DecList>({ required: true });
  const formRef = ref<any>();
  const addFormRef = ref<any>();
  const addVisible = ref(false);
  const addModel = ref<DecList>({});

  const tableColumns = computed(() => [
    ...props.columns,
    {
      title: '操作',
      dataIndex: 'action',
      width: 120,
      fixed: 'right',
      align: 'center',
    },
  ]);
  const fieldMap = computed(() => {
    const map = new Map<string, CitFieldConfig>();
    props.fields.forEach((item) => map.set(item.field, item));
    return map;
  });

  const rules = computed(() => {
    const result: Record<string, any[]> = {};
    props.fields.forEach((item) => {
      if (item.required) {
        result[item.field] = [{ required: true, message: `请输入${item.label}`, trigger: item.type === 'select' ? 'change' : 'blur' }];
      }
    });
    return result;
  });

  function placeholder(item: CitFieldConfig) {
    return item.placeholder || (item.type === 'select' || item.type === 'date' ? `请选择${item.label}` : `请输入${item.label}`);
  }

  function customRow(record: DecList) {
    return {
      onClick: () => emit('select', record),
    };
  }

  async function handleSave() {
    await formRef.value?.validate();
    emit('save');
  }

  function selectOptions(item: CitFieldConfig) {
    if (item.options) return item.options;
    return item.optionSource ? props.optionMap?.[item.optionSource] || [] : [];
  }

  function formatCellText(field: string, value: unknown) {
    const text = value === undefined || value === null ? '' : String(value);
    const item = fieldMap.value.get(field);
    if (!item?.optionSource || !text) return text;
    const option = selectOptions(item).find((optionItem) => String(optionItem.value) === text);
    return option?.label || text;
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

  function handleSelectChange(target: DecList, item: CitFieldConfig, option?: CitSelectOption | CitSelectOption[]) {
    if (item.field !== 'codeTs') return;
    const sourceRecord = Array.isArray(option) ? option[0]?.sourceRecord : option?.sourceRecord;
    if (!sourceRecord) return;
    if (sourceRecord.gName) {
      target.gName = sourceRecord.gName;
    }
    if (sourceRecord.unit1 && !target.firstUnit) {
      target.firstUnit = sourceRecord.unit1;
    }
    if (sourceRecord.unit2 && !target.secondUnit) {
      target.secondUnit = sourceRecord.unit2;
    }
  }

  function nextGoodsNo() {
    const maxNo = props.rows.reduce((max, item) => {
      const current = Number(item.gNo);
      return Number.isFinite(current) ? Math.max(max, current) : max;
    }, 0);
    return maxNo + 1;
  }

  function openAddModal() {
    if (!props.hasHead) return;
    addModel.value = { gNo: nextGoodsNo() };
    addVisible.value = true;
    nextTick(() => addFormRef.value?.clearValidate());
  }

  async function handleCreate() {
    await addFormRef.value?.validate();
    emit('create', { ...addModel.value }, () => {
      addVisible.value = false;
      addModel.value = {};
    });
  }
</script>

<template>
  <section class="goods-section">
    <div class="goods-section__title">
      <span>报关单表体</span>
      <div class="goods-section__actions">
        <a-button preIcon="ant-design:plus-outlined" type="primary" :disabled="!hasHead" @click="openAddModal">新增</a-button>
        <a-button preIcon="ant-design:save-outlined" type="primary" :disabled="!hasHead" :loading="saving" @click="handleSave">暂存</a-button>
      </div>
    </div>
    <a-table
      size="small"
      rowKey="id"
      :columns="tableColumns"
      :dataSource="rows"
      :loading="loading"
      :pagination="false"
      :customRow="customRow"
      :scroll="{ x: 1380, y: 142 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'action'">
          <a-space :size="4">
            <a-button type="link" size="small" @click.stop="emit('select', record)">编辑</a-button>
            <a-button type="link" size="small" danger @click.stop="emit('delete', record)">删除</a-button>
          </a-space>
        </template>
        <template v-else>
          {{ formatCellText(column.dataIndex, record[column.dataIndex]) }}
        </template>
      </template>
    </a-table>
    <a-form ref="formRef" class="goods-section__form" :model="model" :rules="rules" :label-col="{ style: { width: '120px' } }">
      <a-row :gutter="[8, 0]">
        <a-col v-for="item in fields" :key="item.field" :xs="24" :md="12" :xl="item.span || 12">
          <a-form-item :label="item.label" :name="item.field" :class="{ 'is-required-field': item.required }">
            <a-input-number
            size="small"
              v-if="item.type === 'number'"
              v-model:value="model[item.field]"
              :placeholder="placeholder(item)"
              :disabled="!hasHead"
              :min="0"
            />
            <a-textarea
            size="small"
              v-else-if="item.type === 'textarea'"
              v-model:value="model[item.field]"
              :placeholder="placeholder(item)"
              :disabled="!hasHead"
              :maxlength="item.maxLength"
              :autoSize="{ minRows: 1, maxRows: 2 }"
            />
            <a-select
            size="small"
              v-else-if="item.type === 'select'"
              v-model:value="model[item.field]"
              :options="selectOptions(item)"
              :loading="selectLoading(item)"
              :placeholder="placeholder(item)"
              :disabled="!hasHead"
              :filterOption="filterSelectOption"
              optionFilterProp="label"
              showSearch
              allowClear
              @focus="handleSelectFocus(item)"
              @search="handleSelectSearch(item, $event)"
              @change="(_, option) => handleSelectChange(model, item, option)"
            />
            <a-input
            size="small"
              v-else
              v-model:value="model[item.field]"
              :placeholder="placeholder(item)"
              :disabled="!hasHead"
              :maxlength="item.maxLength"
              allowClear
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>

    <a-modal v-model:open="addVisible" title="新增商品明细" :confirmLoading="saving" width="760px" @ok="handleCreate">
      <a-form ref="addFormRef" class="goods-section__form goods-section__modal-form" :model="addModel" :rules="rules" :label-col="{ style: { width: '120px' } }">
        <a-row :gutter="[8, 0]">
          <a-col v-for="item in fields" :key="item.field" :xs="24" :md="item.span === 24 ? 24 : 12">
            <a-form-item :label="item.label" :name="item.field" :class="{ 'is-required-field': item.required }">
              <a-input-number
                v-if="item.type === 'number'"
                v-model:value="addModel[item.field]"
                size="small"
                :placeholder="placeholder(item)"
                :min="0"
              />
              <a-textarea
                v-else-if="item.type === 'textarea'"
                v-model:value="addModel[item.field]"
                size="small"
                :placeholder="placeholder(item)"
                :maxlength="item.maxLength"
                :autoSize="{ minRows: 1, maxRows: 2 }"
              />
              <a-select
                v-else-if="item.type === 'select'"
                v-model:value="addModel[item.field]"
                size="small"
                :options="selectOptions(item)"
                :loading="selectLoading(item)"
                :placeholder="placeholder(item)"
                :filterOption="filterSelectOption"
                optionFilterProp="label"
                showSearch
                allowClear
                @focus="handleSelectFocus(item)"
                @search="handleSelectSearch(item, $event)"
                @change="(_, option) => handleSelectChange(addModel, item, option)"
              />
              <a-input
                v-else
                v-model:value="addModel[item.field]"
                size="small"
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
  .goods-section {
    overflow: hidden;
    border: 1px solid #e5e7eb;
    border-radius: 4px;
    background: #fff;

    &__title {
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

    &__form {
      padding: 10px 12px 0;
      border-top: 1px solid #f0f0f0;
    }

    &__modal-form {
      padding: 8px 0 0;
      border-top: 0;

      :deep(.ant-form-item) {
        margin-bottom: 8px;
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

    :deep(.ant-table-thead > tr > th) {
      height: 30px;
      padding: 5px 8px;
      color: #1f2937;
      font-size: 13px;
      background: #fafafa;
      border-color: #f0f0f0;
    }

    :deep(.ant-table-tbody > tr > td) {
      height: 30px;
      padding: 5px 8px;
      font-size: 13px;
    }

    :deep(.ant-form-item) {
      margin-bottom: 8px;
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
    :deep(.ant-select-selection-item),
    :deep(.ant-select-selection-placeholder) {
      font-size: 13px;
    }

    :deep(.ant-input),
    :deep(.ant-input-number),
    :deep(.ant-select-selector) {
      width: 100%;
      min-height: 28px;
      border-radius: 4px;
    }

    :deep(.ant-form-item-control) {
      min-width: 0;
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

    :deep(.ant-select-single .ant-select-selector .ant-select-selection-search-input) {
      height: 26px;
    }

    :deep(.ant-select-single .ant-select-selector .ant-select-selection-item),
    :deep(.ant-select-single .ant-select-selector .ant-select-selection-placeholder) {
      line-height: 26px;
    }

    :deep(.ant-btn) {
      height: 24px;
      padding: 0 8px;
      font-size: 12px;
    }

    :deep(.is-required-field .ant-input:not([disabled])),
    :deep(.is-required-field .ant-input-number:not(.ant-input-number-disabled)),
    :deep(.is-required-field .ant-select-selector) {
      background-color: #fffbe6;
    }
  }
</style>
