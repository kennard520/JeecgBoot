<script setup lang="ts" name="CustomCitSingleWindow">
  import { computed, ref, watch } from 'vue';
  import { useRoute } from 'vue-router';
  import DeclarationHeadForm from './components/DeclarationHeadForm.vue';
  import DeclarationMasterList from './components/DeclarationMasterList.vue';
  import GoodsSection from './components/GoodsSection.vue';
  import RelatedDataSidePanels from './components/RelatedDataSidePanels.vue';
  import RelatedDataTabs from './components/RelatedDataTabs.vue';
  import { goodsColumns, goodsFormFields, headListColumns, headSections, relatedTabConfigs, singleWindowParaOptionSources } from './cit.data';
  import { useParaOptions } from './composables/useParaOptions';
  import { useSingleWindowDeclaration } from './composables/useSingleWindowDeclaration';
  import type { CitFieldConfig, CitRecord, DecHead } from './types';

  const headFormRef = ref<any>();
  const route = useRoute();
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
    resetHead,
    saveHead,
    selectGoods,
    resetGoods,
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

  function handleResetHead() {
    resetHead();
    headFormRef.value?.clearValidate();
  }

  async function handleSaveHead() {
    await headFormRef.value?.validate();
    await saveHead();
  }

  async function handleSelectHead(record: DecHead) {
    await selectHead(record);
  }
</script>

<template>
  <div class="single-window-page">
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
          :optionMap="optionMap"
          :optionLoadingMap="optionLoadingMap"
          @save="handleSaveHead"
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
          @reset="resetGoods"
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
      @add="handleResetHead"
    />
  </div>
</template>

<style scoped lang="less">
  .single-window-page {
    min-height: calc(100vh - 84px);
    padding: 8px;
    background: #f0f2f5;

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
