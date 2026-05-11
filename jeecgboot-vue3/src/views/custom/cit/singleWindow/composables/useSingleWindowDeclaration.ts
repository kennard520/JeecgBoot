import { computed, onMounted, ref, shallowRef, unref } from 'vue';
import { Modal } from 'ant-design-vue';
import { cloneDeep } from 'lodash-es';
import { useMessage } from '/@/hooks/web/useMessage';
import { deleteCitEntity, queryCitRecords, saveCitEntity } from '../cit.api';
import type { CitRecord, DecHead, DecList } from '../types';

export function createEmptyHead(): DecHead {
  return {
    ieFlag: 'I',
    declTrnRel: '0',
    ediId: '1',
    entryType: '0',
    promiseItmes: '00000',
    businessItems: '00000',
  };
}

export function createEmptyGoods(decHeadId?: string | number, nextNo = 1): DecList {
  return {
    decHeadId,
    gNo: nextNo,
  };
}

export function useSingleWindowDeclaration(initialHeadId?: string | number) {
  const { createMessage } = useMessage();
  const headForm = ref<DecHead>(createEmptyHead());
  const goodsForm = ref<DecList>(createEmptyGoods());
  const headRows = shallowRef<DecHead[]>([]);
  const goodsRows = shallowRef<DecList[]>([]);
  const loadingHeads = shallowRef(false);
  const loadingGoods = shallowRef(false);
  const savingHead = shallowRef(false);
  const savingGoods = shallowRef(false);
  const citVersion = shallowRef('');

  const currentHeadId = computed(() => headForm.value.id);
  const currentGoodsId = computed(() => goodsForm.value.id);
  const selectedHeadKey = computed(() => (currentHeadId.value ? [currentHeadId.value] : []));

  async function loadCitVersion() {
    const rows = await queryCitRecords('citAttributes', { pageSize: 1 });
    citVersion.value = rows[0]?.version || '';
  }

  async function loadHeadRows(params?: CitRecord) {
    loadingHeads.value = true;
    try {
      const rows = await queryCitRecords<DecHead>('decHead', params);
      headRows.value = rows;
      const hasCurrent = Boolean(currentHeadId.value && rows.some((item) => item.id === currentHeadId.value));
      if (rows.length && !hasCurrent) {
        await selectHead(rows[0]);
      }
      return unref(headRows);
    } finally {
      loadingHeads.value = false;
    }
  }

  async function loadGoodsRows() {
    if (!currentHeadId.value) {
      goodsRows.value = [];
      goodsForm.value = createEmptyGoods();
      return;
    }
    loadingGoods.value = true;
    try {
      goodsRows.value = await queryCitRecords<DecList>('decList', { decHeadId: currentHeadId.value });
      goodsForm.value = goodsRows.value[0] ? cloneDeep(goodsRows.value[0]) : createEmptyGoods(currentHeadId.value, 1);
    } finally {
      loadingGoods.value = false;
    }
  }

  async function selectHead(record: DecHead) {
    headForm.value = cloneDeep(record);
    await loadGoodsRows();
  }

  function resetHead() {
    headForm.value = createEmptyHead();
    goodsRows.value = [];
    goodsForm.value = createEmptyGoods();
  }

  function copyHead() {
    const copied = cloneDeep(headForm.value);
    delete copied.id;
    delete copied.seqNo;
    delete copied.entryId;
    delete copied.preEntryId;
    headForm.value = copied;
    goodsRows.value = [];
    goodsForm.value = createEmptyGoods();
    createMessage.info('已复制表头信息，请暂存后再维护商品明细');
  }

  async function saveHeadRecord(record: DecHead) {
    savingHead.value = true;
    const isUpdate = Boolean(record.id);
    try {
      await saveCitEntity('decHead', record, isUpdate);
      createMessage.success(isUpdate ? '表头已更新' : '表头已暂存');
      const rows = await loadHeadRows();
      if (!isUpdate && rows[0]) {
        await selectHead(rows[0]);
      } else if (record.id) {
        const nextRecord = rows.find((item) => item.id === record.id);
        if (nextRecord) {
          await selectHead(nextRecord);
        }
      }
    } finally {
      savingHead.value = false;
    }
  }

  async function saveHead() {
    await saveHeadRecord(headForm.value);
  }

  function deleteHead() {
    if (!currentHeadId.value) {
      createMessage.warning('请先选择需要删除的报关单');
      return;
    }
    Modal.confirm({
      title: '确认删除',
      content: '删除表头不会由当前页面自动级联删除明细，数据库外键策略为 ON DELETE SET NULL。',
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        await deleteCitEntity('decHead', currentHeadId.value!);
        resetHead();
        await loadHeadRows();
      },
    });
  }

  function selectGoods(record: DecList) {
    goodsForm.value = cloneDeep(record);
  }

  function resetGoods() {
    goodsForm.value = createEmptyGoods(currentHeadId.value, goodsRows.value.length + 1);
  }

  async function saveGoodsRecord(record: DecList) {
    if (!currentHeadId.value) {
      createMessage.warning('请先暂存表头，再维护商品明细');
      return;
    }
    savingGoods.value = true;
    const payload = {
      ...record,
      decHeadId: currentHeadId.value,
    };
    try {
      await saveCitEntity('decList', payload, Boolean(payload.id));
      createMessage.success(payload.id ? '商品明细已更新' : '商品明细已新增');
      await loadGoodsRows();
    } finally {
      savingGoods.value = false;
    }
  }

  async function saveGoods() {
    await saveGoodsRecord(goodsForm.value);
  }

  function deleteGoods(record: DecList) {
    if (!record.id) return;
    Modal.confirm({
      title: '确认删除',
      content: `是否删除商品项号 ${record.gNo || record.id}？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        await deleteCitEntity('decList', record.id!);
        await loadGoodsRows();
      },
    });
  }

  function submitDeclaration() {
    createMessage.warning('当前后端仅生成了基础 CRUD 接口，正式申报接口尚未提供');
  }

  function printDeclaration() {
    window.print();
  }

  onMounted(async () => {
    await loadCitVersion();
    const rows = await loadHeadRows(initialHeadId ? { id: initialHeadId } : undefined);
    if (initialHeadId && !rows.length) {
      await loadHeadRows();
    }
  });

  return {
    headForm,
    goodsForm,
    headRows,
    goodsRows,
    loadingHeads,
    loadingGoods,
    savingHead,
    savingGoods,
    citVersion,
    currentHeadId,
    currentGoodsId,
    selectedHeadKey,
    loadHeadRows,
    loadGoodsRows,
    selectHead,
    resetHead,
    copyHead,
    saveHeadRecord,
    saveHead,
    deleteHead,
    selectGoods,
    resetGoods,
    saveGoodsRecord,
    saveGoods,
    deleteGoods,
    submitDeclaration,
    printDeclaration,
  };
}
