<template>
  <BasicDrawer v-bind="$attrs" @register="registerDrawer" :title="getTitle" width="520px" @ok="handleSubmit" destroyOnClose>
    <BasicForm @register="registerForm" />
  </BasicDrawer>
</template>

<script lang="ts" setup>
  import { computed, ref, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { formSchema } from '../CustomApiApp.data';
  import { saveOrUpdate } from '../CustomApiApp.api';
  import { buildApiAppAgentPayload, normalizeApiAppAgentFields } from '../apiAppAgentModel';

  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(false);

  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    labelWidth: 90,
    schemas: formSchema,
    showActionButtonGroup: false,
  });

  const [registerDrawer, { setDrawerProps, closeDrawer }] = useDrawerInner(async (data) => {
    await resetFields();
    isUpdate.value = !!data?.isUpdate;
    setDrawerProps({ confirmLoading: false });
    if (unref(isUpdate)) {
      const agentFields = normalizeApiAppAgentFields(data.record);
      await setFieldsValue({
        ...data.record,
        ...agentFields,
        enabled: `${data.record.enabled ?? 1}`,
      });
    } else {
      await setFieldsValue({
        allowedAgentCodes: [],
        defaultAgentCode: undefined,
        enabled: '1',
        rateLimit: 60,
      });
    }
  });

  const getTitle = computed(() => (unref(isUpdate) ? '编辑 API 客户' : '新增 API 客户'));

  async function handleSubmit() {
    try {
      const values = buildApiAppAgentPayload(await validate());
      setDrawerProps({ confirmLoading: true });
      const result = await saveOrUpdate(values, isUpdate.value);
      closeDrawer();
      emit('success', result);
    } finally {
      setDrawerProps({ confirmLoading: false });
    }
  }
</script>
