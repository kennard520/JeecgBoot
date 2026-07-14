/** @jest-environment node */

jest.mock('ant-design-vue', () => ({ Modal: { confirm: jest.fn() } }));
jest.mock('/@/utils/http/axios', () => ({
  defHttp: {
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
  },
}));

import { defHttp } from '/@/utils/http/axios';
import { list, saveOrUpdate } from '../../../src/views/custom/api/app/CustomApiApp.api';

describe('API app agent grant contract', () => {
  const get = defHttp.get as jest.Mock;
  const post = defHttp.post as jest.Mock;
  const put = defHttp.put as jest.Mock;

  beforeEach(() => {
    get.mockReset();
    post.mockReset();
    put.mockReset();
  });

  it('enriches each API app with /admin/app-grants response fields', async () => {
    get.mockImplementation(({ url }) => {
      if (url === '/custom/api/app/list') {
        return Promise.resolve({ records: [{ id: 7, appKey: 'demo', companyCode: 'CUSTOMS' }], total: 1 });
      }
      if (url === '/custom/ai/admin/app-grants/7') {
        return Promise.resolve([
          { appId: 7, agentCode: 'CUSTOMS', isDefault: 0, enabled: 1 },
          { appId: 7, agentCode: 'ILLUMNA-CUSTOMS', isDefault: 1, enabled: 1 },
        ]);
      }
      throw new Error(`unexpected url: ${url}`);
    });

    await expect(list({ pageNo: 1 })).resolves.toEqual({
      records: [
        {
          id: 7,
          appKey: 'demo',
          companyCode: 'CUSTOMS',
          allowedAgentCodes: ['CUSTOMS', 'ILLUMNA-CUSTOMS'],
          defaultAgentCode: 'ILLUMNA-CUSTOMS',
        },
      ],
      total: 1,
    });
  });

  it('saves compatible app fields first and then replaces the app agent grants', async () => {
    post.mockResolvedValue({ id: 7, appKey: 'demo', companyCode: 'ILLUMNA-CUSTOMS' });
    put.mockResolvedValue([]);

    await saveOrUpdate(
      {
        appKey: 'demo',
        customerCode: 'CIT',
        enabled: 1,
        rateLimit: 60,
        allowedAgentCodes: ['CUSTOMS', 'ILLUMNA-CUSTOMS'],
        defaultAgentCode: 'ILLUMNA-CUSTOMS',
      },
      false
    );

    expect(post).toHaveBeenCalledWith({
      url: '/custom/api/app/add',
      data: {
        appKey: 'demo',
        customerCode: 'CIT',
        enabled: 1,
        rateLimit: 60,
        companyCode: 'ILLUMNA-CUSTOMS',
      },
    });
    expect(put).toHaveBeenCalledWith({
      url: '/custom/ai/admin/app-grants',
      data: {
        appId: 7,
        agentCodes: ['CUSTOMS', 'ILLUMNA-CUSTOMS'],
        defaultAgentCode: 'ILLUMNA-CUSTOMS',
      },
    });
  });
});
