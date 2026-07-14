<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="modalTitle" width="640px" :minHeight="360" destroyOnClose @ok="handleSubmit">
    <a-spin :spinning="loading">
      <a-form :model="draft" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
        <a-form-item label="客户" required>
          <a-select v-model:value="draft.customerCode" :options="customerOptions" show-search option-filter-prop="label" placeholder="请选择客户" />
        </a-form-item>
        <a-form-item label="用户" required>
          <a-input-search :value="selectedUserLabel" readonly enter-button="选择" placeholder="请选择用户" @search="openUserSelector" />
        </a-form-item>
        <a-form-item label="可用智能体" required>
          <a-select
            v-model:value="draft.agentCodes"
            mode="multiple"
            :options="agentOptions"
            option-filter-prop="label"
            placeholder="请选择智能体"
            @change="handleAgentChange"
          />
        </a-form-item>
        <a-form-item label="默认智能体" required>
          <a-select
            v-model:value="draft.defaultAgentCode"
            :options="selectedAgentOptions"
            option-filter-prop="label"
            placeholder="请选择默认智能体"
          />
        </a-form-item>
      </a-form>
    </a-spin>
    <UserSelectModal
      rowKey="id"
      labelKey="username"
      :maxSelectCount="1"
      :showSelected="false"
      @register="registerUserModal"
      @get-select-result="handleUserSelected"
    />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { computed, reactive, ref } from 'vue';
  import { BasicModal, useModal, useModalInner } from '/@/components/Modal';
  import UserSelectModal from '/@/components/Form/src/jeecg/components/modal/UserSelectModal.vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { listEnabledAgents, listEnabledCustomers, saveAgentGrant } from '../AgentGrant.api';
  import { buildAgentGrantPayload, normalizeAgentGrantRecord, type AgentGrantDraft } from '../agentGrantModel';

  const emit = defineEmits(['register', 'success']);
  const { createMessage } = useMessage();
  const [registerUserModal, { openModal: openUserModal }] = useModal();
  const isUpdate = ref(false);
  const loading = ref(false);
  const customers = ref<any[]>([]);
  const agents = ref<any[]>([]);
  const selectedUserLabel = ref('');
  const draft = reactive<AgentGrantDraft>(emptyDraft());

  const customerOptions = computed(() =>
    customers.value.map((item) => ({
      label: item.customerName ? `${item.customerName}（${item.customerCode}）` : item.customerCode,
      value: item.customerCode,
    }))
  );
  const agentOptions = computed(() =>
    agents.value.map((item) => ({
      label: item.agentName || item.agentCode,
      value: item.agentCode,
    }))
  );
  const selectedAgentOptions = computed(() => agentOptions.value.filter((item) => draft.agentCodes.includes(item.value)));
  const modalTitle = computed(() => (isUpdate.value ? '编辑智能体授权' : '新增智能体授权'));

  const [registerModal, { closeModal, setModalProps }] = useModalInner(async (data) => {
    isUpdate.value = !!data?.isUpdate;
    Object.assign(draft, emptyDraft());
    selectedUserLabel.value = '';
    setModalProps({ confirmLoading: false });
    loading.value = true;
    try {
      const [customerList, agentList] = await Promise.all([listEnabledCustomers(), listEnabledAgents()]);
      customers.value = customerList;
      agents.value = agentList;
      if (data?.record) {
        Object.assign(draft, normalizeAgentGrantRecord(data.record));
        selectedUserLabel.value = data.record.realname || data.record.username || data.record.userId;
      }
    } finally {
      loading.value = false;
    }
  });

  function emptyDraft(): AgentGrantDraft {
    return {
      customerCode: '',
      userId: '',
      username: '',
      agentCodes: [],
      defaultAgentCode: '',
    };
  }

  function openUserSelector() {
    openUserModal(true);
  }

  function handleUserSelected(options, values) {
    const option = options?.[0];
    draft.userId = `${values?.[0] || ''}`;
    draft.username = `${option?.label || ''}`;
    selectedUserLabel.value = draft.username || draft.userId;
  }

  function handleAgentChange(codes: string[]) {
    if (!codes.includes(draft.defaultAgentCode)) {
      draft.defaultAgentCode = codes.length === 1 ? codes[0] : '';
    }
  }

  async function handleSubmit() {
    try {
      const payload = buildAgentGrantPayload(draft);
      setModalProps({ confirmLoading: true });
      await saveAgentGrant(payload);
      closeModal();
      createMessage.success('智能体授权已保存');
      emit('success');
    } catch (error: any) {
      createMessage.warning(error?.message || '保存失败');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
