import { defHttp } from '/@/utils/http/axios';

enum Api {
  userGrants = '/custom/ai/admin/user-grants',
  customerUserSave = '/custom/ai/admin/customer-users',
  agentList = '/custom/ai/admin/agents',
  customerList = '/custom/ai/admin/customers',
}

function recordsOf(result: any): any[] {
  if (Array.isArray(result)) {
    return result;
  }
  return result?.records || result?.list || [];
}

export const listAgentGrants = (params) => defHttp.get({ url: Api.userGrants, params });

export const listEnabledAgents = async (params: Recordable = {}) => {
  const result = await defHttp.get({ url: Api.agentList, params });
  return recordsOf(result)
    .filter((item) => `${item.enabled ?? 1}` === '1')
    .map((item) => ({
      ...item,
      agentCode: item.agentCode || item.code,
      agentName: item.agentName || item.name || item.agentCode || item.code,
    }));
};

export const listEnabledCustomers = async () => {
  const result = await defHttp.get({ url: Api.customerList });
  return recordsOf(result).filter((item) => `${item.enabled ?? 1}` === '1');
};

export const saveAgentGrant = async (data) => {
  await defHttp.put({
    url: Api.customerUserSave,
    data: {
      customerCode: data.customerCode,
      userId: data.userId,
      username: data.username,
    },
  });
  return defHttp.put({ url: Api.userGrants, data });
};

export const deleteAgentGrant = (record) =>
  defHttp.delete(
    {
      url: Api.userGrants,
      params: { userId: record.userId, customerCode: record.customerCode },
    },
    { joinParamsToUrl: true }
  );
