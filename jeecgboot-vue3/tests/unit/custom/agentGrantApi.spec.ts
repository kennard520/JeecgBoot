/** @jest-environment node */

jest.mock('ant-design-vue', () => ({ Modal: { confirm: jest.fn() } }));
jest.mock('/@/utils/http/axios', () => ({
  defHttp: {
    get: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  },
}));

import { defHttp } from '/@/utils/http/axios';
import { Modal } from 'ant-design-vue';
import {
  deleteAgentGrant,
  listAgentGrants,
  listEnabledAgents,
  listEnabledCustomers,
  saveAgentGrant,
} from '../../../src/views/custom/ai/grant/AgentGrant.api';

describe('CustomAgentController frontend contract', () => {
  const get = defHttp.get as jest.Mock;
  const put = defHttp.put as jest.Mock;
  const deleteRequest = defHttp.delete as jest.Mock;
  const confirm = Modal.confirm as jest.Mock;

  beforeEach(() => {
    get.mockReset();
    put.mockReset();
    deleteRequest.mockReset();
    confirm.mockReset();
  });

  it('uses the actual admin agent and customer endpoints and filters disabled records', async () => {
    get.mockImplementation(({ url }) => {
      if (url === '/custom/ai/admin/agents') {
        return Promise.resolve([
          { agentCode: 'CUSTOMS', agentName: '通用', enabled: 1 },
          { agentCode: 'OFFLINE', agentName: '停用', enabled: 0 },
        ]);
      }
      if (url === '/custom/ai/admin/customers') {
        return Promise.resolve([
          { customerCode: 'CIT', customerName: 'CIT', enabled: 1 },
          { customerCode: 'OFF', customerName: '停用客户', enabled: 0 },
        ]);
      }
      throw new Error(`unexpected url: ${url}`);
    });

    await expect(listEnabledAgents()).resolves.toEqual([{ agentCode: 'CUSTOMS', agentName: '通用', enabled: 1 }]);
    await expect(listEnabledCustomers()).resolves.toEqual([{ customerCode: 'CIT', customerName: 'CIT', enabled: 1 }]);
  });

  it('uses the aggregated admin user-grants endpoint and preserves its response fields', async () => {
    const grants = [
      {
        userId: 'u-1',
        username: 'alice',
        realname: 'Alice',
        customerCode: 'CIT',
        customerName: 'CIT Club',
        agentCodes: ['CUSTOMS', 'ILLUMNA-CUSTOMS'],
        agentNames: ['通用', '因美纳'],
        defaultAgentCode: 'ILLUMNA-CUSTOMS',
        defaultAgentName: '因美纳',
        updatedAt: '2026-07-14T10:00:00',
      },
    ];
    get.mockResolvedValue(grants);

    await expect(listAgentGrants({ username: 'alice' })).resolves.toBe(grants);
    expect(get).toHaveBeenCalledWith({
      url: '/custom/ai/admin/user-grants',
      params: { username: 'alice' },
    });
  });

  it('saves the customer relation before replacing user grants with controller request names', async () => {
    put.mockResolvedValue([]);
    const payload = {
      customerCode: 'CIT',
      userId: 'u-1',
      username: 'alice',
      agentCodes: ['CUSTOMS', 'ILLUMNA-CUSTOMS'],
      defaultAgentCode: 'CUSTOMS',
    };

    await saveAgentGrant(payload);

    expect(put).toHaveBeenNthCalledWith(1, {
      url: '/custom/ai/admin/customer-users',
      data: { customerCode: 'CIT', userId: 'u-1', username: 'alice' },
    });
    expect(put).toHaveBeenNthCalledWith(2, {
      url: '/custom/ai/admin/user-grants',
      data: payload,
    });
  });

  it('deletes directly through the aggregated endpoint without opening a second confirmation', async () => {
    deleteRequest.mockResolvedValue(undefined);
    const record = { userId: 'u-1', customerCode: 'CIT' };

    await deleteAgentGrant(record);

    expect(deleteRequest).toHaveBeenCalledWith(
      {
        url: '/custom/ai/admin/user-grants',
        params: record,
      },
      { joinParamsToUrl: true }
    );
    expect(confirm).not.toHaveBeenCalled();
  });
});
