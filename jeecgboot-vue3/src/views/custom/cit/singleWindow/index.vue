<script setup lang="ts" name="CustomCitSingleWindow">
  import { computed, nextTick, ref, watch } from 'vue';
  import { useRoute } from 'vue-router';
  import DeclarationHeadForm from './components/DeclarationHeadForm.vue';
  import DeclarationMasterList from './components/DeclarationMasterList.vue';
  import GoodsSection from './components/GoodsSection.vue';
  import RelatedDataSidePanels from './components/RelatedDataSidePanels.vue';
  import RelatedDataTabs from './components/RelatedDataTabs.vue';
  import { goodsColumns, goodsFormFields, headListColumns, headSections, relatedTabConfigs, singleWindowParaOptionSources } from './cit.data';
  import { deleteCitEntity, queryCitRecords, saveCitEntity } from './cit.api';
  import { useParaOptions } from './composables/useParaOptions';
  import { createEmptyHead, useSingleWindowDeclaration } from './composables/useSingleWindowDeclaration';
  import { useMessage } from '/@/hooks/web/useMessage';
  import type { CitFieldConfig, CitRecord, DecHead, DecList } from './types';

  interface VisaCertificateRow extends CitRecord {
    code: string;
    name: string;
    count?: string;
    subCount?: string;
    visaId?: string | number;
  }

  interface VisaElementForm extends CitRecord {
    domesticConsigneeEname?: string;
    overseasConsignorCname?: string;
    overseasConsignorAddr?: string;
    cmplDschrgDt?: string;
    declGoodsEname?: string;
  }

  defineOptions({ name: 'CustomCitSingleWindow' });

  const headFormRef = ref<any>();
  const headAddFormRef = ref<any>();
  const moreRelatedTabsRef = ref<any>();
  const visaFormRef = ref<any>();
  const headAddVisible = ref(false);
  const visaVisible = ref(false);
  const headAddSaving = ref(false);
  const visaLoading = ref(false);
  const visaSaving = ref(false);
  const markLobUploading = ref(false);
  const headAddModel = ref<DecHead>(createEmptyHead());
  const visaForm = ref<VisaElementForm>({});
  const visaCertificateRows = ref<VisaCertificateRow[]>([]);
  const visaSelectedCodes = ref<string[]>([]);
  const visaExistingIds = ref<Array<string | number>>([]);
  const route = useRoute();
  const { createMessage } = useMessage();
  const { optionMap, optionLoadingMap, loadParaOptions, ensureParaOption } = useParaOptions(singleWindowParaOptionSources);

  const {
    headForm,
    goodsForm,
    headRows,
    goodsRows,
    loadingHeads,
    loadingGoods,
    savingHead,
    savingGoods,
    currentHeadId,
    currentGoodsId,
    selectedHeadKey,
    loadHeadRows,
    loadGoodsRows,
    selectHead,
    saveHeadRecord,
    saveHead,
    selectGoods,
    saveGoodsRecord,
    saveGoods,
    deleteGoods,
  } = useSingleWindowDeclaration(route.query.decHeadId as string | undefined);

  const statusItems = computed(() => [
    { label: '申报地海关', value: headForm.value.customMaster },
    { label: '统一编号', value: headForm.value.seqNo },
    { label: '预录入编号', value: headForm.value.preEntryId },
    { label: '海关编号', value: headForm.value.entryId },
    { label: '进出口', value: headForm.value.ieFlag === 'E' ? '出口' : '进口' },
    { label: '收发货人', value: headForm.value.tradeName },
  ]);
  const sideRelatedConfigs = computed(() => relatedTabConfigs.filter((item) => item.key === 'decContainer' || item.key === 'decLicenseDocus'));
  const moreRelatedConfigs = computed(() => relatedTabConfigs.filter((item) => item.key !== 'decContainer' && item.key !== 'decLicenseDocus'));
  const headSelectFields = headSections.flatMap((section) => section.fields).filter((item) => item.optionSource);
  const goodsSelectFields = goodsFormFields.filter((item) => item.optionSource);
  const imageExtensionSet = new Set(['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']);
  const visaCertificateColumns = [
    { title: '序号', dataIndex: 'index', width: 70, align: 'center' },
    { title: '证书代码', dataIndex: 'code', width: 120 },
    { title: '证书名称', dataIndex: 'name', width: 320, ellipsis: true },
    { title: '正本数量', dataIndex: 'count', width: 150 },
    { title: '副本数量', dataIndex: 'subCount', width: 150 },
  ];
  const visaRowSelection = computed(() => ({
    selectedRowKeys: visaSelectedCodes.value,
    columnWidth: 44,
    onChange: (keys: Array<string | number>) => {
      visaSelectedCodes.value = keys.map((item) => String(item));
    },
  }));
  const visaRules = {
    domesticConsigneeEname: [{ max: 400, message: '境内收发货人名称(外文)最多400个字符', trigger: 'blur' }],
    overseasConsignorCname: [{ max: 150, message: '境外收发货人名称(中文)最多150个字符', trigger: 'blur' }],
    overseasConsignorAddr: [{ max: 100, message: '境外发货人地址最多100个字符', trigger: 'blur' }],
    declGoodsEname: [{ max: 100, message: '商品英文名称最多100个字符', trigger: 'blur' }],
  };

  function ensureSelectedOptions(record: CitRecord, fields: CitFieldConfig[]) {
    fields.forEach((item) => {
      if (item.optionSource) {
        ensureParaOption(item.optionSource, record[item.field]);
      }
    });
  }

  function normalizeText(value: unknown) {
    return value === undefined || value === null ? '' : String(value).trim();
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

  function optionText(label: unknown) {
    const text = normalizeText(label);
    return text.includes('|') ? text.split('|').slice(1).join('|') : text;
  }

  function formatDateValue(value: unknown) {
    if (!value) return undefined;
    if (value instanceof Date) {
      const year = value.getFullYear();
      const month = `${value.getMonth() + 1}`.padStart(2, '0');
      const day = `${value.getDate()}`.padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
    return normalizeText(value).slice(0, 10);
  }

  function buildVisaRows(existingRows: CitRecord[]) {
    const existingByCode = new Map<string, CitRecord>();
    existingRows.forEach((item) => {
      const code = normalizeText(item.certCode);
      if (code) {
        existingByCode.set(code, item);
      }
    });
    const rows = (optionMap.value.credential || []).map((option, index) => {
      const sourceRecord = option.sourceRecord;
      const code = normalizeText(recordValue(sourceRecord, 'code')) || normalizeText(option.value);
      const existing = existingByCode.get(code);
      return {
        id: code,
        code,
        name: normalizeText(recordValue(sourceRecord, 'name')) || optionText(option.label),
        count: normalizeText(existing?.certOriginalCount) || normalizeText(recordValue(sourceRecord, 'count')),
        subCount: normalizeText(existing?.certCopyCount) || normalizeText(recordValue(sourceRecord, 'subCount', 'sub_count')),
        visaId: existing?.id,
        index: index + 1,
      };
    });
    existingRows.forEach((item) => {
      const code = normalizeText(item.certCode);
      if (code && !rows.some((row) => row.code === code)) {
        rows.push({
          id: code,
          code,
          name: normalizeText(item.certName) || code,
          count: normalizeText(item.certOriginalCount),
          subCount: normalizeText(item.certCopyCount),
          visaId: item.id,
          index: rows.length + 1,
        });
      }
    });
    visaCertificateRows.value = rows;
    visaSelectedCodes.value = existingRows.map((item) => normalizeText(item.certCode)).filter(Boolean);
  }

  async function openVisaElementsModal() {
    if (!currentHeadId.value) {
      createMessage.warning('请先保存表头，再维护检验检疫签证申报要素');
      return;
    }
    if (!currentGoodsId.value) {
      createMessage.warning('请先暂存并选择商品明细，再维护商品英文名称');
      return;
    }
    visaVisible.value = true;
    visaLoading.value = true;
    try {
      await loadParaOptions('credential');
      const visaRows = await queryCitRecords('decCiqVisa', { decHeadId: currentHeadId.value, decListId: currentGoodsId.value });
      const visaElement = visaRows[0] || {};
      visaExistingIds.value = visaRows.map((item) => item.id).filter(Boolean) as Array<string | number>;
      buildVisaRows(visaRows);
      visaForm.value = {
        domesticConsigneeEname: visaElement.domesticConsigneeEname,
        overseasConsignorCname: visaElement.overseasConsignorCname,
        overseasConsignorAddr: visaElement.overseasConsignorAddr,
        cmplDschrgDt: formatDateValue(visaElement.cmplDschrgDt),
        declGoodsEname: visaElement.declGoodsEname,
      };
      nextTick(() => visaFormRef.value?.clearValidate());
    } finally {
      visaLoading.value = false;
    }
  }

  async function saveVisaRecords() {
    const selectedCodeSet = new Set(visaSelectedCodes.value);
    const selectedRows = visaCertificateRows.value.filter((row) => selectedCodeSet.has(row.code));
    const elementPayload = {
      decHeadId: currentHeadId.value,
      decListId: currentGoodsId.value,
      domesticConsigneeEname: normalizeText(visaForm.value.domesticConsigneeEname),
      overseasConsignorCname: normalizeText(visaForm.value.overseasConsignorCname),
      overseasConsignorAddr: normalizeText(visaForm.value.overseasConsignorAddr),
      cmplDschrgDt: visaForm.value.cmplDschrgDt,
      declGoodsEname: normalizeText(visaForm.value.declGoodsEname),
    };
    await Promise.all(visaExistingIds.value.map((id) => deleteCitEntity('decCiqVisa', id)));
    const payloadRows = selectedRows.length
      ? selectedRows.map((row) => ({
          ...elementPayload,
          certCode: row.code,
          certName: row.name,
          certOriginalCount: normalizeText(row.count),
          certCopyCount: normalizeText(row.subCount),
        }))
      : [elementPayload];
    await Promise.all(payloadRows.map((row) => saveCitEntity('decCiqVisa', row, false)));
  }

  async function handleSaveVisaElements() {
    if (!currentHeadId.value || !currentGoodsId.value) return;
    await visaFormRef.value?.validate();
    visaSaving.value = true;
    try {
      await saveVisaRecords();
      createMessage.success('检验检疫签证申报要素已保存');
      visaVisible.value = false;
    } finally {
      visaSaving.value = false;
    }
  }

  watch(headForm, (value) => ensureSelectedOptions(value, headSelectFields), { immediate: true, deep: true });
  watch(goodsForm, (value) => ensureSelectedOptions(value, goodsSelectFields), { immediate: true, deep: true });
  watch(goodsRows, (rows) => rows.forEach((item) => ensureSelectedOptions(item, goodsSelectFields)), { immediate: true, deep: true });

  function openHeadAddModal() {
    headAddModel.value = createEmptyHead();
    headAddVisible.value = true;
    nextTick(() => headAddFormRef.value?.clearValidate());
  }

  async function handleSaveHead() {
    await headFormRef.value?.validate();
    await saveHead();
  }

  async function handleSaveNewHead() {
    await headAddFormRef.value?.validate();
    headAddSaving.value = true;
    try {
      await saveHeadRecord(headAddModel.value);
      headAddVisible.value = false;
    } finally {
      headAddSaving.value = false;
    }
  }

  async function handleCreateGoods(record: DecList, close: () => void) {
    await saveGoodsRecord(record);
    close();
  }

  function readFileAsBase64(file: File) {
    return new Promise<string>((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        const result = String(reader.result || '');
        resolve(result.includes(',') ? result.split(',')[1] : result);
      };
      reader.onerror = () => reject(reader.error);
      reader.readAsDataURL(file);
    });
  }

  function getFileExtension(fileName: string) {
    const ext = fileName.includes('.') ? fileName.split('.').pop() || '' : '';
    return ext.toLowerCase();
  }

  function inferAttachType(fileName: string) {
    return getFileExtension(fileName);
  }

  function isImageFile(file: File) {
    if (file.type) {
      return file.type.toLowerCase().startsWith('image/');
    }
    return imageExtensionSet.has(getFileExtension(file.name));
  }

  async function handleUploadMarkLob(file: File) {
    if (!currentHeadId.value) {
      createMessage.warning('请先保存表头，再上传标记唛码附件');
      return;
    }
    if (!isImageFile(file)) {
      createMessage.warning('标记唛码附件只能上传图片');
      return;
    }
    if (file.size > 1024 * 1024) {
      createMessage.warning('标记唛码附件不能超过 1MB');
      return;
    }
    markLobUploading.value = true;
    try {
      const attachment = await readFileAsBase64(file);
      await saveCitEntity(
        'decMarkLob',
        {
          decHeadId: currentHeadId.value,
          attachName: file.name.slice(0, 80),
          attachType: inferAttachType(file.name),
          attachment,
        },
        false
      );
      createMessage.success('标记唛码附件已上传');
      if (moreRelatedTabsRef.value?.activate) {
        await moreRelatedTabsRef.value.activate('decMarkLob');
      } else {
        await moreRelatedTabsRef.value?.loadCurrent?.();
      }
    } finally {
      markLobUploading.value = false;
    }
  }

  function handleUploadNewMarkLob(file: File) {
    if (!isImageFile(file)) {
      createMessage.warning('标记唛码附件只能上传图片');
      return;
    }
    createMessage.warning('请先保存新增报关单，再上传标记唛码附件');
  }

  async function handleAddDecUser() {
    if (!currentHeadId.value) {
      createMessage.warning('请先保存表头，再维护使用人信息');
      return;
    }
    await moreRelatedTabsRef.value?.openAddByKey?.('decUser');
  }

  async function handleAddDecCopLimit() {
    if (!currentHeadId.value) {
      createMessage.warning('请先保存表头，再维护企业资质信息');
      return;
    }
    await moreRelatedTabsRef.value?.openAddByKey?.('decCopLimit');
  }

  function handleAddNewDecUser() {
    createMessage.warning('请先保存新增报关单，再维护使用人信息');
  }

  function handleAddNewDecCopLimit() {
    createMessage.warning('请先保存新增报关单，再维护企业资质信息');
  }

  function handleAddNewCiqVisa() {
    createMessage.warning('请先保存新增报关单并选择商品明细，再维护检验检疫签证申报要素');
  }

  async function handleSelectHead(record: DecHead) {
    await selectHead(record);
  }
</script>

<template>
  <div class="single-window-page">
    <div class="single-window-page__toolbar">
      <a-button type="primary" size="small" :loading="savingHead" @click="handleSaveHead">保存表头</a-button>
    </div>

    <div class="single-window-page__status">
      <span v-for="item in statusItems" :key="item.label">
        <em>{{ item.label }}</em>
        <strong>{{ item.value || '-' }}</strong>
      </span>
    </div>

    <div class="single-window-page__declaration">
      <main class="single-window-page__left">
        <DeclarationHeadForm
          ref="headFormRef"
          v-model="headForm"
          :sections="headSections"
          :saving="savingHead"
          :markLobUploading="markLobUploading"
          :optionMap="optionMap"
          :optionLoadingMap="optionLoadingMap"
          @save="handleSaveHead"
          @mark-lob-upload="handleUploadMarkLob"
          @dec-user-add="handleAddDecUser"
          @dec-cop-limit-add="handleAddDecCopLimit"
          @ciq-visa-add="openVisaElementsModal"
          @option-search="loadParaOptions"
        />
        <GoodsSection
          v-model="goodsForm"
          :rows="goodsRows"
          :columns="goodsColumns"
          :fields="goodsFormFields"
          :loading="loadingGoods"
          :saving="savingGoods"
          :hasHead="!!currentHeadId"
          :optionMap="optionMap"
          :optionLoadingMap="optionLoadingMap"
          @save="saveGoods"
          @create="handleCreateGoods"
          @select="selectGoods"
          @delete="deleteGoods"
          @refresh="loadGoodsRows"
          @option-search="loadParaOptions"
        />
      </main>

      <RelatedDataSidePanels
        class="single-window-page__right"
        :headId="currentHeadId"
        :goodsId="currentGoodsId"
        :configs="sideRelatedConfigs"
        :optionMap="optionMap"
        :optionLoadingMap="optionLoadingMap"
        @option-search="loadParaOptions"
      />
    </div>

    <RelatedDataTabs
      ref="moreRelatedTabsRef"
      class="single-window-page__more"
      title="更多"
      :headId="currentHeadId"
      :goodsId="currentGoodsId"
      :configs="moreRelatedConfigs"
      :optionMap="optionMap"
      :optionLoadingMap="optionLoadingMap"
      @option-search="loadParaOptions"
    />

    <DeclarationMasterList
      mode="workspace"
      :rows="headRows"
      :columns="headListColumns"
      :loading="loadingHeads"
      :selectedRowKeys="selectedHeadKey"
      @search="loadHeadRows"
      @select="handleSelectHead"
      @add="openHeadAddModal"
    />

    <a-modal
      v-model:open="headAddVisible"
      title="新增报关单"
      :confirmLoading="headAddSaving || savingHead"
      width="980px"
      :bodyStyle="{ maxHeight: 'calc(100vh - 220px)', overflowY: 'auto', background: '#f0f2f5' }"
      @ok="handleSaveNewHead"
    >
      <DeclarationHeadForm
        ref="headAddFormRef"
        v-model="headAddModel"
        :sections="headSections"
        :saving="headAddSaving"
        :optionMap="optionMap"
        :optionLoadingMap="optionLoadingMap"
        @save="handleSaveNewHead"
        @mark-lob-upload="handleUploadNewMarkLob"
        @dec-user-add="handleAddNewDecUser"
        @dec-cop-limit-add="handleAddNewDecCopLimit"
        @ciq-visa-add="handleAddNewCiqVisa"
        @option-search="loadParaOptions"
      />
    </a-modal>

    <a-modal
      v-model:open="visaVisible"
      title="检验检疫签证申报要素"
      width="980px"
      :confirmLoading="visaSaving"
      :maskClosable="false"
      @ok="handleSaveVisaElements"
    >
      <div class="visa-elements">
        <a-table
          size="small"
          :columns="visaCertificateColumns"
          :dataSource="visaCertificateRows"
          :loading="visaLoading"
          :pagination="false"
          :rowKey="(record) => record.code"
          :rowSelection="visaRowSelection"
          :scroll="{ y: 320 }"
        >
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.dataIndex === 'index'">
              {{ index + 1 }}
            </template>
            <template v-else-if="column.dataIndex === 'count'">
              <a-input v-model:value="record.count" size="small" />
            </template>
            <template v-else-if="column.dataIndex === 'subCount'">
              <a-input v-model:value="record.subCount" size="small" />
            </template>
          </template>
        </a-table>
        <a-form
          ref="visaFormRef"
          class="visa-elements__form"
          :model="visaForm"
          :rules="visaRules"
          :label-col="{ style: { width: '200px' } }"
        >
          <a-form-item label="境内收发货人名称(外文)" name="domesticConsigneeEname">
            <a-input v-model:value="visaForm.domesticConsigneeEname" size="small" maxlength="400" allowClear />
          </a-form-item>
          <a-form-item label="境外收发货人名称(中文)" name="overseasConsignorCname">
            <a-input v-model:value="visaForm.overseasConsignorCname" size="small" maxlength="150" allowClear />
          </a-form-item>
          <a-form-item label="境外发货人地址" name="overseasConsignorAddr">
            <a-input v-model:value="visaForm.overseasConsignorAddr" size="small" maxlength="100" allowClear />
          </a-form-item>
          <a-form-item label="卸毕日期" name="cmplDschrgDt">
            <a-date-picker v-model:value="visaForm.cmplDschrgDt" valueFormat="YYYY-MM-DD" size="small" placeholder="请选择日期" />
          </a-form-item>
          <a-form-item label="商品英文名称" name="declGoodsEname">
            <a-input v-model:value="visaForm.declGoodsEname" size="small" maxlength="100" allowClear />
          </a-form-item>
        </a-form>
      </div>
      <template #footer>
        <a-button key="save" type="primary" :loading="visaSaving" @click="handleSaveVisaElements">保存</a-button>
      </template>
    </a-modal>
  </div>
</template>

<style scoped lang="less">
  .single-window-page {
    min-height: calc(100vh - 84px);
    padding: 8px;
    background: #f0f2f5;

    &__toolbar {
      display: flex;
      gap: 8px;
      justify-content: flex-end;
      margin-bottom: 8px;
    }

    &__status {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      margin-bottom: 8px;
      overflow: hidden;
      border: 1px solid #e5e7eb;
      border-radius: 4px;
      background: #fff;

      span {
        display: flex;
        min-width: 0;
        height: 28px;
        border-right: 1px solid #f0f0f0;

        &:last-child {
          border-right: 0;
        }
      }

      em,
      strong {
        min-width: 0;
        padding: 0 8px;
        overflow: hidden;
        font-size: 12px;
        line-height: 27px;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      em {
        flex: 0 0 88px;
        color: #606266;
        font-style: normal;
        text-align: right;
        background: #fafafa;
        border-right: 1px solid #f0f0f0;
      }

      strong {
        flex: 1;
        color: #111;
        font-weight: 400;
      }
    }

    &__declaration {
      display: grid;
      grid-template-columns: minmax(0, 1fr) 340px;
      gap: 8px;
      align-items: start;
    }

    &__left {
      display: flex;
      min-width: 0;
      flex-direction: column;
      gap: 8px;
    }

    &__right {
      min-width: 0;
    }

    &__more {
      margin-top: 8px;
    }
  }

  .visa-elements {
    :deep(.ant-table-thead > tr > th) {
      height: 28px;
      padding: 4px 8px;
      color: #fff;
      font-size: 13px;
      font-weight: 600;
      background: #5aa7df;
      border-color: #d7e8f5;
    }

    :deep(.ant-table-tbody > tr:nth-child(even) > td) {
      background: #eaf5ff;
    }

    :deep(.ant-table-tbody > tr > td) {
      height: 28px;
      padding: 3px 8px;
      border-color: #e6edf3;
    }

    :deep(.ant-input),
    :deep(.ant-picker) {
      width: 100%;
      height: 26px;
      border-radius: 0;
    }

    &__form {
      margin-top: 8px;
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
        height: 30px;
        color: #111;
        font-size: 13px;
      }

      :deep(.ant-form-item-control) {
        background: #f2f2f2;
      }

      :deep(.ant-form-item-control-input) {
        min-height: 30px;
      }

      :deep(.ant-form-item-control-input-content) {
        padding: 2px 4px;
      }
    }
  }

  @media (max-width: 1180px) {
    .single-window-page {
      &__status {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }

      &__declaration {
        grid-template-columns: 1fr;
      }
    }
  }
</style>
