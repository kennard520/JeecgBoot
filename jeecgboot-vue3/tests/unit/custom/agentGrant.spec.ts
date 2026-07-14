/** @jest-environment node */

import { buildAgentGrantPayload, normalizeAgentGrantRecord, validateAgentGrantDraft } from '../../../src/views/custom/ai/grant/agentGrantModel';

describe('user agent grants', () => {
  const validDraft = {
    customerCode: 'CIT',
    userId: 'u-1',
    username: 'alice',
    agentCodes: ['CUSTOMS', 'ILLUMNA-CUSTOMS'],
    defaultAgentCode: 'CUSTOMS',
  };

  it('requires customer, user, at least one agent, and one default agent', () => {
    expect(validateAgentGrantDraft({ ...validDraft, customerCode: '' })).toBe('请选择客户');
    expect(validateAgentGrantDraft({ ...validDraft, userId: '' })).toBe('请选择用户');
    expect(validateAgentGrantDraft({ ...validDraft, agentCodes: [] })).toBe('请至少选择一个智能体');
    expect(validateAgentGrantDraft({ ...validDraft, defaultAgentCode: '' })).toBe('请选择默认智能体');
    expect(validateAgentGrantDraft(validDraft)).toBeUndefined();
  });

  it('rejects a default agent that is not included in the grant', () => {
    expect(validateAgentGrantDraft({ ...validDraft, defaultAgentCode: 'OTHER' })).toBe('默认智能体必须包含在可用智能体中');
  });

  it('normalizes persisted comma separated grants for editing', () => {
    expect(
      normalizeAgentGrantRecord({
        customerCode: 'CIT',
        userId: 'u-1',
        username: 'alice',
        agentCodes: 'CUSTOMS,ILLUMNA-CUSTOMS,CUSTOMS',
        defaultAgentCode: 'CUSTOMS',
      })
    ).toEqual(validDraft);
  });

  it('builds a stable replacement payload with unique agent codes', () => {
    expect(buildAgentGrantPayload({ ...validDraft, agentCodes: [' CUSTOMS ', 'ILLUMNA-CUSTOMS', 'CUSTOMS'] })).toEqual(validDraft);
  });
});
