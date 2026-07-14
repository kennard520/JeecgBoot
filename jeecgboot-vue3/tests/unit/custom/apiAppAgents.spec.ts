/** @jest-environment node */

import { buildApiAppAgentPayload, normalizeApiAppAgentFields } from '../../../src/views/custom/api/app/apiAppAgentModel';

describe('API app agent routing', () => {
  it('migrates the compatible companyCode into allowed and default agent controls', () => {
    expect(normalizeApiAppAgentFields({ companyCode: 'CUSTOMS' })).toEqual({
      allowedAgentCodes: ['CUSTOMS'],
      defaultAgentCode: 'CUSTOMS',
    });
  });

  it('preserves explicit allowed agents and the default when editing', () => {
    expect(
      normalizeApiAppAgentFields({
        allowedAgentCodes: 'CUSTOMS,ILLUMNA-CUSTOMS',
        defaultAgentCode: 'ILLUMNA-CUSTOMS',
        companyCode: 'CUSTOMS',
      })
    ).toEqual({
      allowedAgentCodes: ['CUSTOMS', 'ILLUMNA-CUSTOMS'],
      defaultAgentCode: 'ILLUMNA-CUSTOMS',
    });
  });

  it('requires a default from the allowed set and keeps companyCode compatible', () => {
    expect(
      buildApiAppAgentPayload({
        appKey: 'demo',
        allowedAgentCodes: ['CUSTOMS', 'ILLUMNA-CUSTOMS'],
        defaultAgentCode: 'ILLUMNA-CUSTOMS',
      })
    ).toMatchObject({
      allowedAgentCodes: ['CUSTOMS', 'ILLUMNA-CUSTOMS'],
      defaultAgentCode: 'ILLUMNA-CUSTOMS',
      companyCode: 'ILLUMNA-CUSTOMS',
    });
    expect(() =>
      buildApiAppAgentPayload({
        allowedAgentCodes: ['CUSTOMS'],
        defaultAgentCode: 'ILLUMNA-CUSTOMS',
      })
    ).toThrow('默认智能体必须包含在可用智能体中');
  });
});
