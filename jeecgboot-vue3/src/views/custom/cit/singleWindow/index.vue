<script setup lang="ts" name="CustomCitSingleWindow">
  import { computed, nextTick, ref, watch } from 'vue';
  import { useRoute } from 'vue-router';
  import DeclarationHeadForm from './components/DeclarationHeadForm.vue';
  import DeclarationMasterList from './components/DeclarationMasterList.vue';
  import GoodsSection from './components/GoodsSection.vue';
  import RelatedDataSidePanels from './components/RelatedDataSidePanels.vue';
  import RelatedDataTabs from './components/RelatedDataTabs.vue';
  import { goodsColumns, goodsFormFields, headListColumns, headSections, relatedTabConfigs, singleWindowParaOptionSources } from './cit.data';
  import { saveCitEntity } from './cit.api';
  import { useParaOptions } from './composables/useParaOptions';
  import { createEmptyHead, useSingleWindowDeclaration } from './composables/useSingleWindowDeclaration';
  import { useMessage } from '/@/hooks/web/useMessage';
  import type { CitFieldConfig, CitRecord, DecHead, DecList } from './types';

  const headFormRef = ref<any>();
  const headAddFormRef = ref<any>();
  const moreRelatedTabsRef = ref<any>();
  const headAddVisible = ref(false);
  const headAddSaving = ref(false);
  const markLobUploading = ref(false);
  const headAddModel = ref<DecHead>(createEmptyHead());
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

  function ensureSelectedOptions(record: CitRecord, fields: CitFieldConfig[]) {
    fields.forEach((item) => {
      if (item.optionSource) {
        ensureParaOption(item.optionSource, record[item.field]);
      }
    });
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

  function handleAddNewDecUser() {
    createMessage.warning('请先保存新增报关单，再维护使用人信息');
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
        @option-search="loadParaOptions"
      />
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
