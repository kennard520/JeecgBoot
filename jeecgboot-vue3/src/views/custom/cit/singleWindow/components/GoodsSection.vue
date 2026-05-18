<script setup lang="ts">
  import { computed, nextTick, ref } from 'vue';
  import { SwapOutlined } from '@ant-design/icons-vue';
  import { Modal } from 'ant-design-vue';
  import { deleteCitEntity, queryCitRecords, saveCitEntity } from '../cit.api';
  import { useMessage } from '/@/hooks/web/useMessage';
  import type { CitFieldConfig, CitRecord, CitSelectOption, CitTableColumn, DecList, ParaOptionLoadingMap, ParaOptionMap, ParaOptionSourceKey } from '../types';

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
    refresh: [];
    optionSearch: [source: ParaOptionSourceKey, keyword?: string];
  }>();

  interface PreferentialForm extends CitRecord {
    certificateNo?: string;
    agreementCode?: string;
    rcepOrigPlaceCode?: string;
    ecoGNo?: string;
    certNoType?: 'C' | 'D';
    ecoRelationId?: string | number;
  }

  const { createMessage } = useMessage();
  const model = defineModel<DecList>({ required: true });
  const formRef = ref<any>();
  const addFormRef = ref<any>();
  const preferentialFormRef = ref<any>();
  const addVisible = ref(false);
  const preferentialVisible = ref(false);
  const preferentialLoading = ref(false);
  const preferentialSaving = ref(false);
  const addModel = ref<DecList>({});
  const preferentialForm = ref<PreferentialForm>({});
  const preferentialOriginField: CitFieldConfig = {
    field: 'rcepOrigPlaceCode',
    label: '优惠贸易协定项下原产地',
    type: 'select',
    optionSource: 'country',
    maxLength: 3,
  };
  const preferentialAgreementField: CitFieldConfig = {
    field: 'agreementCode',
    label: '优惠贸易协定代码',
    type: 'select',
    optionSource: 'agreement',
    maxLength: 2,
  };
  const preferentialRules = {
    agreementCode: [
      { required: true, message: '请输入优惠贸易协定代码', trigger: 'change' },
      {
        validator: (_rule: unknown, value: unknown) => {
          return normalizeText(value).length <= 2 ? Promise.resolve() : Promise.reject('优惠贸易协定代码最多2个字符');
        },
        trigger: 'change',
      },
    ],
    certificateNo: [{ required: true, message: '请输入原产地证明编号', trigger: 'blur' }],
    rcepOrigPlaceCode: [{ required: true, message: '请选择优惠贸易协定项下原产地', trigger: 'change' }],
    ecoGNo: [{ required: true, message: '请输入原产地证明商品项号', trigger: 'blur' }],
  };

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
      const itemRules: any[] = [];
      if (item.required) {
        itemRules.push({ required: true, message: `请输入${item.label}`, trigger: item.type === 'select' ? 'change' : 'blur' });
      }
      if (item.maxLength && item.type === 'select') {
        itemRules.push({
          validator: (_rule: unknown, value: unknown) => {
            const text = value === undefined || value === null ? '' : String(value);
            return text.length <= item.maxLength! ? Promise.resolve() : Promise.reject(`${item.label}最多${item.maxLength}个字符`);
          },
          trigger: 'change',
        });
      }
      if (itemRules.length) result[item.field] = itemRules;
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
    if (item.multiple) {
      return splitMultipleValue(text, item)
        .map((optionValue) => selectOptions(item).find((optionItem) => String(optionItem.value) === optionValue)?.label || optionValue)
        .join('、');
    }
    const option = selectOptions(item).find((optionItem) => String(optionItem.value) === text);
    return option?.label || text;
  }

  function splitMultipleValue(value: unknown, item: CitFieldConfig) {
    if (Array.isArray(value)) {
      return value.map((itemValue) => String(itemValue)).filter(Boolean);
    }
    const text = value === undefined || value === null ? '' : String(value).trim();
    if (!text) return [];
    if (item.valueChunkSize) {
      return text.match(new RegExp(`.{1,${item.valueChunkSize}}`, 'g')) || [];
    }
    return text.split(',').map((itemValue) => itemValue.trim()).filter(Boolean);
  }

  function encodeMultipleValue(value: unknown, item: CitFieldConfig) {
    const values = splitMultipleValue(value, item);
    return item.valueChunkSize ? values.join('') : values.join(',');
  }

  function selectFieldValue(target: DecList, item: CitFieldConfig) {
    const value = target[item.field];
    return item.multiple ? splitMultipleValue(value, item) : value;
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

  function handleSelectChange(target: DecList, item: CitFieldConfig, value: unknown, option?: CitSelectOption | CitSelectOption[]) {
    target[item.field] = item.multiple ? encodeMultipleValue(value, item) : value;
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

  function normalizeText(value: unknown) {
    return value === undefined || value === null ? '' : String(value).trim();
  }

  function stripAgreementPrefix(value: unknown) {
    const text = normalizeText(value).toUpperCase();
    return text.startsWith('Y') ? text.slice(1) : text;
  }

  function buildEcoCertNo() {
    const certificateNo = normalizeText(preferentialForm.value.certificateNo);
    return preferentialForm.value.certNoType ? `${preferentialForm.value.certNoType}${certificateNo}` : certificateNo;
  }

  function parseEcoCertNo(value: unknown) {
    const text = normalizeText(value);
    if (text.startsWith('C') || text.startsWith('D')) {
      return {
        certNoType: text.slice(0, 1) as 'C' | 'D',
        certificateNo: text.slice(1),
      };
    }
    return {
      certNoType: undefined,
      certificateNo: text,
    };
  }

  function ensureSavedGoods() {
    if (!model.value.id || !model.value.decHeadId) {
      createMessage.warning('请先暂存并选择商品明细，再维护协定享惠信息');
      return false;
    }
    return true;
  }

  async function loadPreferentialForm() {
    preferentialLoading.value = true;
    try {
      const relations = await queryCitRecords<CitRecord>('ecoRelation', { decListId: model.value.id });
      const relation = relations.find((item) => normalizeText(item.certType).toUpperCase().startsWith('Y'));
      const certInfo = parseEcoCertNo(relation?.ecoCertNo);
      preferentialForm.value = {
        ecoRelationId: relation?.id,
        certificateNo: certInfo.certificateNo,
        agreementCode: stripAgreementPrefix(relation?.certType),
        rcepOrigPlaceCode: model.value.rcepOrigPlaceCode,
        ecoGNo: relation?.ecoGNo || (model.value.gNo ? String(model.value.gNo) : ''),
        certNoType: certInfo.certNoType,
      };
      nextTick(() => preferentialFormRef.value?.clearValidate());
    } finally {
      preferentialLoading.value = false;
    }
  }

  async function openPreferentialModal() {
    if (!ensureSavedGoods()) return;
    preferentialVisible.value = true;
    await loadPreferentialForm();
  }

  function syncOriginCountryToPreferential() {
    const originCountry = normalizeText(model.value.originCountry);
    if (!originCountry) {
      createMessage.warning('请先维护当前商品的原产国');
      return;
    }
    preferentialForm.value.rcepOrigPlaceCode = originCountry;
    nextTick(() => preferentialFormRef.value?.clearValidate?.(['rcepOrigPlaceCode']));
  }

  async function handlePreferentialConfirm() {
    if (!ensureSavedGoods()) return;
    await preferentialFormRef.value?.validate();
    const agreementCode = stripAgreementPrefix(preferentialForm.value.agreementCode);
    preferentialSaving.value = true;
    try {
      const rcepOrigPlaceCode = normalizeText(preferentialForm.value.rcepOrigPlaceCode);
      const decListPayload = {
        ...model.value,
        rcepOrigPlaceCode,
      };
      await saveCitEntity('decList', decListPayload, true);
      await saveCitEntity(
        'ecoRelation',
        {
          id: preferentialForm.value.ecoRelationId,
          decHeadId: model.value.decHeadId,
          decListId: model.value.id,
          certType: `Y${agreementCode}`,
          decGNo: model.value.gNo ? String(model.value.gNo) : undefined,
          ecoCertNo: buildEcoCertNo(),
          ecoGNo: normalizeText(preferentialForm.value.ecoGNo),
          extendFiled: '优惠贸易协定享惠',
        },
        Boolean(preferentialForm.value.ecoRelationId)
      );
      model.value.rcepOrigPlaceCode = rcepOrigPlaceCode;
      createMessage.success('协定享惠信息已保存');
      preferentialVisible.value = false;
      emit('refresh');
    } finally {
      preferentialSaving.value = false;
    }
  }

  function handlePreferentialCancel() {
    if (!ensureSavedGoods()) return;
    Modal.confirm({
      title: '取消享惠',
      content: '将清空优惠贸易协定项下原产地，并删除当前商品的原产地证明关联关系。是否继续？',
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        preferentialSaving.value = true;
        try {
          await saveCitEntity('decList', { ...model.value, rcepOrigPlaceCode: '' }, true);
          if (preferentialForm.value.ecoRelationId) {
            await deleteCitEntity('ecoRelation', preferentialForm.value.ecoRelationId);
          }
          model.value.rcepOrigPlaceCode = undefined;
          preferentialForm.value = {};
          createMessage.success('已取消协定享惠');
          preferentialVisible.value = false;
          emit('refresh');
        } finally {
          preferentialSaving.value = false;
        }
      },
    });
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
      :scroll="{ x: 2100, y: 142 }"
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
        <template v-for="item in fields" :key="item.field">
          <a-col :xs="24" :md="12" :xl="item.span || 12">
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
                :value="selectFieldValue(model, item)"
                :options="selectOptions(item)"
                :loading="selectLoading(item)"
                :placeholder="placeholder(item)"
                :disabled="!hasHead"
                :mode="item.multiple ? 'multiple' : undefined"
                :filterOption="filterSelectOption"
                optionFilterProp="label"
                showSearch
                allowClear
                @focus="handleSelectFocus(item)"
                @search="handleSelectSearch(item, $event)"
                @change="(value, option) => handleSelectChange(model, item, value, option)"
              />
              <a-date-picker
                v-else-if="item.type === 'date'"
                v-model:value="model[item.field]"
                valueFormat="YYYY-MM-DD"
                size="small"
                :placeholder="placeholder(item)"
                :disabled="!hasHead"
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
          <a-col v-if="item.field === 'originCountry'" :key="`${item.field}-preferential`" :xs="24" :md="12" :xl="12">
            <a-form-item class="goods-section__preferential-action" label="协定享惠">
              <a-button
                size="small"
                type="primary"
                ghost
                :disabled="!hasHead || !model.id"
                :loading="preferentialLoading"
                @click="openPreferentialModal"
              >
                协定享惠
              </a-button>
              <span v-if="model.rcepOrigPlaceCode" class="goods-section__preferential-state">已维护</span>
            </a-form-item>
          </a-col>
        </template>
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
                :value="selectFieldValue(addModel, item)"
                size="small"
                :options="selectOptions(item)"
                :loading="selectLoading(item)"
                :placeholder="placeholder(item)"
                :mode="item.multiple ? 'multiple' : undefined"
                :filterOption="filterSelectOption"
                optionFilterProp="label"
                showSearch
                allowClear
                @focus="handleSelectFocus(item)"
                @search="handleSelectSearch(item, $event)"
                @change="(value, option) => handleSelectChange(addModel, item, value, option)"
              />
              <a-date-picker
                v-else-if="item.type === 'date'"
                v-model:value="addModel[item.field]"
                valueFormat="YYYY-MM-DD"
                size="small"
                :placeholder="placeholder(item)"
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

    <a-modal
      v-model:open="preferentialVisible"
      title="优惠贸易协定享惠"
      wrapClassName="preferential-benefit-modal"
      width="760px"
      :confirmLoading="preferentialSaving"
      :maskClosable="false"
      @ok="handlePreferentialConfirm"
    >
      <div class="preferential-benefit">
        <a-alert
          type="info"
          showIcon
          message="如当前商品需要享惠，“优惠贸易协定项下原产地”与“原产地证明商品项号”必填！如无需享惠，请同时清空上述两个字段。"
        />
        <a-spin :spinning="preferentialLoading">
          <div class="preferential-benefit__body">
            <a-form
              ref="preferentialFormRef"
              class="preferential-benefit__form"
              :model="preferentialForm"
              :rules="preferentialRules"
              :label-col="{ style: { width: '190px' } }"
            >
              <a-form-item label="原产地证明编号" name="certificateNo">
                <a-input v-model:value="preferentialForm.certificateNo" size="small" maxlength="63" allowClear />
              </a-form-item>
              <a-form-item label="优惠贸易协定代码" name="agreementCode">
                <a-select
                  v-model:value="preferentialForm.agreementCode"
                  size="small"
                  :options="selectOptions(preferentialAgreementField)"
                  :loading="selectLoading(preferentialAgreementField)"
                  :filterOption="filterSelectOption"
                  optionFilterProp="label"
                  showSearch
                  allowClear
                  @focus="handleSelectFocus(preferentialAgreementField)"
                  @search="handleSelectSearch(preferentialAgreementField, $event)"
                />
              </a-form-item>
              <a-form-item label="优惠贸易协定项下原产地" name="rcepOrigPlaceCode">
                <a-select
                  v-model:value="preferentialForm.rcepOrigPlaceCode"
                  size="small"
                  :options="selectOptions(preferentialOriginField)"
                  :loading="selectLoading(preferentialOriginField)"
                  :filterOption="filterSelectOption"
                  optionFilterProp="label"
                  showSearch
                  allowClear
                  @focus="handleSelectFocus(preferentialOriginField)"
                  @search="handleSelectSearch(preferentialOriginField, $event)"
                />
              </a-form-item>
              <a-form-item label="原产地证明商品项号" name="ecoGNo">
                <a-input v-model:value="preferentialForm.ecoGNo" size="small" maxlength="9" allowClear />
              </a-form-item>
              <a-form-item label="原产地证明类型" name="certNoType">
                <a-radio-group v-model:value="preferentialForm.certNoType">
                  <a-radio value="C">原产地证书(C)</a-radio>
                  <a-radio value="D">原产地声明(D)</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-form>
            <a-tooltip title="同步当前商品原产国">
              <a-button class="preferential-benefit__sync" shape="circle" type="primary" @click="syncOriginCountryToPreferential">
                <SwapOutlined />
              </a-button>
            </a-tooltip>
          </div>
        </a-spin>
      </div>
      <template #footer>
        <a-button key="confirm" type="primary" :loading="preferentialSaving" @click="handlePreferentialConfirm">确定享惠</a-button>
        <a-button key="cancel-benefit" :loading="preferentialSaving" @click="handlePreferentialCancel">取消享惠</a-button>
      </template>
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
      :deep(.ant-picker),
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
      :deep(.ant-picker),
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
    :deep(.ant-picker),
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

    &__preferential-action {
      :deep(.ant-form-item-control-input-content) {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }

    &__preferential-state {
      color: #1677ff;
      font-size: 12px;
    }
  }

  .preferential-benefit {
    padding: 18px 20px 6px;

    &__body {
      display: grid;
      grid-template-columns: minmax(0, 1fr) 36px;
      column-gap: 14px;
      align-items: center;
      margin-top: 18px;
    }

    &__form {
      border: 1px solid #d9d9d9;

      :deep(.ant-form-item) {
        margin-bottom: 0;
        border-bottom: 1px solid #d9d9d9;

        &:last-child {
          border-bottom: 0;
        }
      }

      :deep(.ant-form-item-label) {
        padding-right: 8px;
        background: #fff;
        border-right: 1px solid #d9d9d9;
      }

      :deep(.ant-form-item-label > label) {
        height: 34px;
        color: #111;
        font-size: 14px;
      }

      :deep(.ant-form-item-control) {
        background: #f5f5f5;
      }

      :deep(.ant-form-item-control-input) {
        min-height: 34px;
      }

      :deep(.ant-form-item-control-input-content) {
        padding: 3px 8px;
      }

      :deep(.ant-input),
      :deep(.ant-select-selector) {
        background: #fff;
      }
    }

    &__sync {
      width: 30px;
      min-width: 30px;
      height: 30px;
      padding: 0;
      font-size: 16px;
      background: #58afe8;
      border-color: #58afe8;
    }
  }

  :global(.preferential-benefit-modal .ant-modal-header) {
    padding: 16px 24px;
    background: #0b8fe8;
  }

  :global(.preferential-benefit-modal .ant-modal-title),
  :global(.preferential-benefit-modal .ant-modal-close-x) {
    color: #fff;
  }

  :global(.preferential-benefit-modal .ant-modal-footer) {
    padding: 20px 24px 28px;
    text-align: center;
  }

  :global(.preferential-benefit-modal .ant-modal-footer .ant-btn) {
    min-width: 120px;
  }
</style>
