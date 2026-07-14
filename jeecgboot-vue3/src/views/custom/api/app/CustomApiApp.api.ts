import { Modal } from 'ant-design-vue';
import { defHttp } from '/@/utils/http/axios';
import { buildApiAppAgentPayload, normalizeApiAppAgentFields } from './apiAppAgentModel';

enum Api {
  list = '/custom/api/app/list',
  save = '/custom/api/app/add',
  edit = '/custom/api/app/edit',
  deleteOne = '/custom/api/app/delete',
  deleteBatch = '/custom/api/app/deleteBatch',
  resetSecret = '/custom/api/app/resetSecret',
  clearAccessToken = '/custom/api/app/clearAccessToken',
  checkAppKey = '/custom/api/app/checkAppKey',
  enabledAgents = '/custom/ai/admin/agents',
  appGrants = '/custom/ai/admin/app-grants',
}

function isEnabled(value: unknown) {
  return value === undefined || value === null || value === true || value === 1 || value === '1';
}

async function getAppAgentFields(record: any) {
  const compatible = normalizeApiAppAgentFields(record);
  if (!record?.id) {
    return compatible;
  }
  const grants = await defHttp.get({ url: `${Api.appGrants}/${record.id}` });
  const enabledGrants = (Array.isArray(grants) ? grants : []).filter((item) => isEnabled(item.enabled));
  if (enabledGrants.length === 0) {
    return compatible;
  }
  const allowedAgentCodes = enabledGrants.map((item) => item.agentCode).filter(Boolean);
  const defaultGrant = enabledGrants.find((item) => `${item.isDefault ?? 0}` === '1');
  return {
    allowedAgentCodes,
    defaultAgentCode: defaultGrant?.agentCode || compatible.defaultAgentCode || allowedAgentCodes[0],
  };
}

export const list = async (params) => {
  const page = await defHttp.get({ url: Api.list, params });
  const records = page?.records || [];
  const enrichedRecords = await Promise.all(
    records.map(async (record) => ({
      ...record,
      ...(await getAppAgentFields(record)),
    }))
  );
  return { ...page, records: enrichedRecords };
};

export const saveOrUpdate = async (params, isUpdate) => {
  const url = isUpdate ? Api.edit : Api.save;
  const normalized = buildApiAppAgentPayload(params);
  const { allowedAgentCodes, defaultAgentCode, agentCodes: _agentCodes, ...base } = normalized;
  const data = {
    ...base,
    agentCodes: allowedAgentCodes,
    defaultAgentCode,
  };
  const result = isUpdate ? await defHttp.put({ url, data }) : await defHttp.post({ url, data });
  const appId = result?.id || params.id;
  if (!appId) {
    throw new Error('API 应用保存成功但未返回 appId');
  }
  return { ...result, allowedAgentCodes, defaultAgentCode };
};

export const listEnabledAgents = async () => {
  const result = await defHttp.get({ url: Api.enabledAgents });
  const records = Array.isArray(result) ? result : result?.records || result?.list || [];
  return records
    .filter((item) => isEnabled(item.enabled))
    .map((item) => ({
      ...item,
      agentCode: item.agentCode || item.code,
      agentName: item.agentName || item.name || item.agentCode || item.code,
    }));
};

export const resetSecret = (id: string | number) => {
  return defHttp.post({ url: Api.resetSecret, params: { id } }, { joinParamsToUrl: true });
};

export const clearAccessToken = (id: string | number) => {
  return defHttp.post({ url: Api.clearAccessToken, params: { id } }, { joinParamsToUrl: true });
};

export const checkAppKey = (params) => defHttp.get({ url: Api.checkAppKey, params });

export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

export const batchDelete = (params, handleSuccess) => {
  Modal.confirm({
    title: '确认删除',
    content: '是否删除选中的 API 客户配置？',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};
