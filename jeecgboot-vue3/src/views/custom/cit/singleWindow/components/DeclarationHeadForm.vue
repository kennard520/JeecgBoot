<script setup lang="ts">
  import { computed, ref } from 'vue';
  import type { CitFieldConfig, CitSectionConfig, DecHead, ParaOptionLoadingMap, ParaOptionMap, ParaOptionSourceKey } from '../types';

  const props = defineProps<{
    sections: CitSectionConfig[];
    disabled?: boolean;
    saving?: boolean;
    showSaveButton?: boolean;
    showMarkLobUpload?: boolean;
    markLobUploading?: boolean;
    optionMap?: ParaOptionMap;
    optionLoadingMap?: ParaOptionLoadingMap;
  }>();

  const emit = defineEmits<{
    save: [];
    markLobUpload: [file: File];
    decUserAdd: [];
    decCopLimitAdd: [];
    ciqVisaAdd: [];
    optionSearch: [source: ParaOptionSourceKey, keyword?: string];
  }>();

  const model = defineModel<DecHead>({ required: true });
  const formRef = ref<any>();
  const flagModalVisible = ref(false);
  type FlagField = 'promiseItmes' | 'businessItems' | 'specDeclFlag';
  const activeFlagField = ref<FlagField>('promiseItmes');
  const flagDraft = ref<string[]>([]);

  const flagOptions = [
    { label: '0 否', value: '0' },
    { label: '1 是', value: '1' },
  ];

  const flagConfigs = {
    promiseItmes: {
      title: '价格说明',
      control: 'select',
      items: ['特殊关系确认', '价格影响确认', '支付特许权使用费确认', '公式定价确认', '暂定价格确认'],
    },
    businessItems: {
      title: '业务事项',
      control: 'checkbox',
      items: ['税单无纸化', '担保验放', '跨境电商海外仓', '组合港', '自报自享'],
    },
    specDeclFlag: {
      title: '特种业务标识',
      control: 'checkbox',
      items: ['国际赛事', '特殊进出军工物资', '国际援助物资', '国际会议', '直通放行', '外交礼遇', '转关'],
    },
  } as const;

  const activeFlagConfig = computed(() => flagConfigs[activeFlagField.value]);
  const flagTip = computed(() => (activeFlagConfig.value.control === 'checkbox' ? '勾选为1，未勾选为0' : '0-否、1-是'));
  const saveButtonText = computed(() => (model.value.id ? '保存表头' : '暂存表头'));

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

  function handleMarkLobBeforeUpload(file: File) {
    emit('markLobUpload', file);
    return false;
  }

  function isFlagButtonField(field: string): field is FlagField {
    return field === 'promiseItmes' || field === 'businessItems' || field === 'specDeclFlag';
  }

  function normalizeFlagValue(value: unknown, length: number) {
    const text = value === undefined || value === null ? '' : String(value);
    return Array.from({ length }, (_, index) => (text[index] === '1' ? '1' : '0')).join('');
  }

  function openFlagModal(item: CitFieldConfig) {
    if (!isFlagButtonField(item.field)) return;
    if (itemDisabled(item)) return;
    activeFlagField.value = item.field;
    flagDraft.value = normalizeFlagValue(model.value[item.field], flagConfigs[item.field].items.length).split('');
    flagModalVisible.value = true;
  }

  function handleFlagCheckboxChange(index: number, event: any) {
    const next = [...flagDraft.value];
    next[index] = event?.target?.checked ? '1' : '0';
    flagDraft.value = next;
  }

  function handleFlagConfirm() {
    const field = activeFlagField.value;
    const flagLength = activeFlagConfig.value.items.length;
    const current = model.value[field] === undefined || model.value[field] === null ? '' : String(model.value[field]);
    const suffix = current.slice(flagLength);
    const maxLength = props.sections.flatMap((section) => section.fields).find((item) => item.field === field)?.maxLength || flagLength;
    model.value[field] = `${flagDraft.value.join('')}${suffix}`.slice(0, maxLength);
    flagModalVisible.value = false;
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
          v-show="props.showSaveButton !== false"
          preIcon="ant-design:save-outlined"
          type="primary"
          size="small"
          :loading="saving"
          :disabled="disabled"
          @click="emit('save')"
        >
          {{ saveButtonText }}
        </a-button>
      </div>
      <a-row :gutter="[8, 0]">
        <template v-for="item in section.fields" :key="item.field">
          <a-col :xs="24" :md="12" :xl="item.span || 12">
            <a-form-item
              :label="item.label"
              :name="item.field"
              :class="{
                'is-required-field': item.required,
                'is-flag-button-field': isFlagButtonField(item.field),
              }"
            >
              <a-select
                v-if="item.type === 'select'"
                v-model:value="model[item.field]"
                :options="selectOptions(item)"
                :loading="selectLoading(item)"
                :placeholder="placeholder(item)"
                :disabled="itemDisabled(item)"
                :filterOption="filterSelectOption"
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
              <template v-else-if="item.type === 'textarea'">
                <a-textarea
                  size="small"
                  v-model:value="model[item.field]"
                  :placeholder="placeholder(item)"
                  :disabled="itemDisabled(item)"
                  :maxlength="item.maxLength"
                  :autoSize="{ minRows: 1, maxRows: 2 }"
                />
              </template>
              <a-input
                v-else-if="!isFlagButtonField(item.field)"
                v-model:value="model[item.field]"
                :placeholder="placeholder(item)"
                :disabled="itemDisabled(item)"
                :maxlength="item.maxLength"
                size="small"
                allowClear
              />
              <a-button
                v-else
                class="declaration-head-form__flag-button"
                size="small"
                type="primary"
                ghost
                :disabled="itemDisabled(item)"
                @click="openFlagModal(item)"
              >
                {{ item.label }}
              </a-button>
            </a-form-item>
          </a-col>
          <a-col v-if="item.field === 'markNo'" :xs="24" :md="12" :xl="6">
            <div class="declaration-head-form__mark-attachment declaration-head-form__mark-attachment--standalone" style="margin-top: 12px">
              <a-upload accept="image/*" :showUploadList="false" :beforeUpload="handleMarkLobBeforeUpload">
                <a-button
                  preIcon="ant-design:upload-outlined"
                  type="primary"
                  size="small"
                  :loading="props.markLobUploading"
                  :disabled="props.disabled || props.markLobUploading"
                >
                  上传附件
                </a-button>
              </a-upload>
              <span>不超过 1MB</span>
            </div>
          </a-col>
          <a-col v-if="item.field === 'correlationReasonFlag'" :xs="24" :md="12" :xl="8">
            <div class="declaration-head-form__inline-action" style="margin-top: 12px">
              <a-button preIcon="ant-design:user-add-outlined" type="primary" size="small" @click="emit('decUserAdd')">使用人信息</a-button>
              <a-button preIcon="ant-design:solution-outlined" type="primary" size="small" @click="emit('decCopLimitAdd')">企业资质</a-button>
              <a-button preIcon="ant-design:file-protect-outlined" type="primary" size="small" @click="emit('ciqVisaAdd')">检验检疫签证申报要素</a-button>
            </div>
          </a-col>
        </template>
      </a-row>
    </section>
  </a-form>

  <a-modal v-model:open="flagModalVisible" :title="activeFlagConfig.title" width="640px" @ok="handleFlagConfirm">
    <div class="declaration-head-form__flag-modal-body">
      <div class="declaration-head-form__flag-tip">{{ flagTip }}</div>
      <div class="declaration-head-form__flag-list">
        <div v-for="(item, index) in activeFlagConfig.items" :key="item" class="declaration-head-form__flag-row">
          <span>{{ index + 1 }}</span>
          <strong>{{ item }}</strong>
          <a-checkbox
            v-if="activeFlagConfig.control === 'checkbox'"
            class="declaration-head-form__flag-checkbox"
            :checked="flagDraft[index] === '1'"
            @change="handleFlagCheckboxChange(index, $event)"
          >
            选中
          </a-checkbox>
          <a-select v-else v-model:value="flagDraft[index]" class="declaration-head-form__flag-select" :options="flagOptions" size="small" />
        </div>
      </div>
    </div>
  </a-modal>
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

    &__flag-button {
      width: 100%;
      height: 28px;
      padding: 0 8px;
      border-radius: 4px;
    }

    &__flag-modal-body {
      padding: 20px;
    }

    &__flag-tip {
      margin-bottom: 8px;
      padding: 6px 8px;
      color: #8c6d1f;
      font-size: 12px;
      background: #fffbe6;
      border: 1px solid #ffe58f;
      border-radius: 4px;
    }

    &__flag-list {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    &__flag-row {
      display: grid;
      grid-template-columns: 28px minmax(0, 1fr) 112px;
      gap: 8px;
      align-items: center;
      min-height: 32px;

      > span {
        width: 22px;
        height: 22px;
        color: #1677ff;
        font-size: 12px;
        line-height: 20px;
        text-align: center;
        background: #e6f4ff;
        border: 1px solid #bae0ff;
        border-radius: 50%;
      }

      > strong {
        min-width: 0;
        overflow: hidden;
        color: #303133;
        font-size: 13px;
        font-weight: 500;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    &__flag-select {
      width: 112px;
    }

    &__flag-checkbox {
      white-space: nowrap;
    }

    &__mark-attachment {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-top: 6px;

      span {
        color: #909399;
        font-size: 12px;
      }
    }

    &__mark-attachment--standalone {
      height: 28px;
      margin-top: 0;
      margin-bottom: 8px;
    }

    &__inline-action {
      display: flex;
      align-items: center;
      gap: 4px;
      min-height: 28px;
      margin-bottom: 8px;
      flex-wrap: wrap;

      :deep(.ant-btn) {
        padding: 0 6px;
      }
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

    :deep(.is-flag-button-field .ant-form-item-label) {
      display: none;
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
    :deep(.is-required-field .ant-select-selector),
    :deep(.is-required-field .declaration-head-form__flag-button:not(.ant-btn-disabled)) {
      background-color: #fffbe6;
    }
  }
</style>
