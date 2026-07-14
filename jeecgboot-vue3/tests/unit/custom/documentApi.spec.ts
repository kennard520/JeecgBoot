/** @jest-environment node */

jest.mock('/@/utils/http/axios', () => ({
  defHttp: {
    uploadFile: jest.fn(),
  },
}));

import { defHttp } from '/@/utils/http/axios';
import { uploadZip } from '../../../src/views/custom/task/document/Document.api';

describe('document upload API', () => {
  it('places agentCode and autoStart in the multipart form', async () => {
    const uploadFile = defHttp.uploadFile as jest.Mock;
    uploadFile.mockResolvedValue({
      success: true,
      code: 200,
      result: { id: 9, taskId: 'task-9', status: 'QUEUED' },
    });
    const file = { name: 'case.zip' } as File;

    await uploadZip(file, 'ILLUMNA-CUSTOMS');

    expect(uploadFile).toHaveBeenCalledWith(
      { url: '/custom/task/document/uploadZip' },
      {
        file,
        data: {
          agentCode: 'ILLUMNA-CUSTOMS',
          autoStart: true,
        },
      },
      { isReturnResponse: true }
    );
  });
});
