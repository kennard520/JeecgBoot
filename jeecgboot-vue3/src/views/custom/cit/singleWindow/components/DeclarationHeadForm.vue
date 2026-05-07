<script setup lang="ts">
  import { computed, ref } from 'vue';
  import type { CitFieldConfig, CitSectionConfig, DecHead, ParaOptionLoadingMap, ParaOptionMap, ParaOptionSourceKey } from '../types';

  const props = defineProps<{
    sections: CitSectionConfig[];
    disabled?: boolean;
    saving?: boolean;
    optionMap?: ParaOptionMap;
    optionLoadingMap?: ParaOptionLoadingMap;
  }>();

  const emit = defineEmits<{
    save: [];
    optionSearch: [source: ParaOptionSourceKey, keyword?: string];
  }>();

  const model = defineModel<DecHead>({ required: true });
  const formRef = ref<any>();

  const rules = computed(() => {
    const result: Record<string, any[]> = {};
    props.sections.forEach((section) => {
      section.fields.forEach((item) => {
        if (item.required) {
          result[item.field] = [{ required: true, message: `请输入${item.label}`, trigger: item.type === 'select' ? 'change' : 'blur' }];
        }
      });
    });
    return result;
  });

  function placeholder(item: CitFieldConfig) {
    if (item.placeholder) return item.placeholder;
    if (item.readonly) return '';
    return item.type === 'select' || item.type === 'date' ? `请选择${item.label}` : `请输入${item.label}`;
  }

  function itemDisabled(item: CitFieldConfig) {
    return props.disabled || item.readonly;
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

  async function validate() {
    await formRef.value?.validate();
  }

  function clearValidate() {
    formRef.value?.clearValidate();
  }

  defineExpose({ validate, clearValidate });
</script>

<template>
  <a-form ref="formRef" class="declaration-head-form" :model="model" :rules="rules" :label-col="{ style: { width: '120px' } }">
    <section v-for="(section, index) in sections" :key="section.title" class="declaration-head-form__section">
      <div class="declaration-head-form__title">
        <span>{{ section.title }}</span>
        <a-button
          v-if="index === 0"
          preIcon="ant-design:save-outlined"
          type="primary"
          size="small"
          :loading="saving"
          :disabled="disabled"
          @click="emit('save')"
        >
          暂存表头
        </a-button>
      </div>
      <a-row :gutter="[8, 0]">
        <a-col v-for="item in section.fields" :key="item.field" :xs="24" :md="12" :xl="item.span || 12">
          <a-form-item :label="item.label" :name="item.field" :class="{ 'is-required-field': item.required }">
            <a-select
              v-if="item.type === 'select'"
              v-model:value="model[item.field]"
              :options="selectOptions(item)"
              :loading="selectLoading(item)"
              :placeholder="placeholder(item)"
              :disabled="itemDisabled(item)"
              :filterOption="!item.optionSource"
              optionFilterProp="label"
              showSearch
              allowClear
              size="small"
              @focus="handleSelectFocus(item)"
              @search="handleSelectSearch(item, $event)"
            />
            <a-date-picker
              size="small"
              v-else-if="item.type === 'date'"
              v-model:value="model[item.field]"
              valueFormat="YYYY-MM-DD"
              :placeholder="placeholder(item)"
              :disabled="itemDisabled(item)"
            />
            <a-input-number
              size="small"
              v-else-if="item.type === 'number'"
              v-model:value="model[item.field]"
              :placeholder="placeholder(item)"
              :disabled="itemDisabled(item)"
              :min="0"
            />
            <a-textarea
              size="small"
              v-else-if="item.type === 'textarea'"
              v-model:value="model[item.field]"
              :placeholder="placeholder(item)"
              :disabled="itemDisabled(item)"
              :maxlength="item.maxLength"
              :autoSize="{ minRows: 1, maxRows: 2 }"
            />
            <a-input
              v-else
              v-model:value="model[item.field]"
              :placeholder="placeholder(item)"
              :disabled="itemDisabled(item)"
              :maxlength="item.maxLength"
              size="small"
              allowClear
            />
          </a-form-item>
        </a-col>
      </a-row>
    </section>
  </a-form>
</template>

<style scoped lang="less">
  .declaration-head-form {
    display: flex;
    flex-direction: column;
    gap: 8px;

    &__section {
      overflow: hidden;
      border: 1px solid #e5e7eb;
      border-radius: 4px;
      background: #fff;
    }

    &__section:first-child {
      border-top: 1px solid #e5e7eb;
    }

    &__title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 32px;
      padding: 0 12px;
      color: #1f2937;
      font-size: 13px;
      font-weight: 600;
      line-height: 32px;
      background: #fafafa;
      border-bottom: 1px solid #f0f0f0;
    }

    &__title :deep(.ant-btn) {
      height: 24px;
      padding: 0 8px;
      font-size: 12px;
      font-weight: 400;
    }

    :deep(.ant-row) {
      padding: 10px 12px 0;
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

    :deep(.ant-form-item-control) {
      min-width: 0;
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

    :deep(.ant-input-number-input),
    :deep(.ant-select-selection-item),
    :deep(.ant-select-selection-placeholder) {
      font-size: 13px;
    }

    :deep(.is-required-field .ant-input:not([disabled])),
    :deep(.is-required-field .ant-input-number:not(.ant-input-number-disabled)),
    :deep(.is-required-field .ant-picker:not(.ant-picker-disabled)),
    :deep(.is-required-field .ant-select-selector) {
      background-color: #fffbe6;
    }
  }
</style>
