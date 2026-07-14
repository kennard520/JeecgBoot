/** @jest-environment node */

import {
  buildDocumentUploadData,
  canRetryDocument,
  createAgentSelectionState,
  getDocumentNavigationPath,
  isActiveDocumentStatus,
  normalizeAgentOptions,
} from '../../../src/views/custom/task/document/documentWorkflow';

describe('document agent workflow', () => {
  it('disables upload when the current user has no available agent', () => {
    expect(createAgentSelectionState([], 'CUSTOMS')).toEqual({
      mode: 'disabled',
      options: [],
      selectedAgentCode: undefined,
      uploadDisabled: true,
    });
  });

  it('automatically selects the only available agent', () => {
    const options = [{ agentCode: 'CUSTOMS', agentName: '通用' }];

    expect(createAgentSelectionState(options)).toEqual({
      mode: 'readonly',
      options,
      selectedAgentCode: 'CUSTOMS',
      uploadDisabled: false,
    });
  });

  it('shows a selector for multiple agents and prefers the remembered authorized choice', () => {
    const options = [
      { agentCode: 'CUSTOMS', agentName: '通用', isDefault: true },
      { agentCode: 'ILLUMNA-CUSTOMS', agentName: '因美纳' },
    ];

    expect(createAgentSelectionState(options, 'ILLUMNA-CUSTOMS')).toMatchObject({
      mode: 'select',
      selectedAgentCode: 'ILLUMNA-CUSTOMS',
      uploadDisabled: false,
    });
    expect(createAgentSelectionState(options, 'NOT-A-GRANT').selectedAgentCode).toBe('CUSTOMS');
  });

  it('normalizes the mine endpoint response and ignores disabled entries', () => {
    expect(
      normalizeAgentOptions({
        customerCode: 'CIT',
        agents: [
          { code: 'CUSTOMS', name: '通用', default: 1, enabled: 1 },
          { agentCode: 'ILLUMNA-CUSTOMS', agentName: '因美纳', isDefault: false, enabled: true },
          { agentCode: 'OFFLINE', agentName: '停用', enabled: 0 },
        ],
      })
    ).toEqual([
      { agentCode: 'CUSTOMS', agentName: '通用', isDefault: true },
      { agentCode: 'ILLUMNA-CUSTOMS', agentName: '因美纳', isDefault: false },
    ]);
  });

  it('reads the defaultAgent field serialized by AgentOptionResponse', () => {
    expect(
      normalizeAgentOptions({
        agents: [
          { agentCode: 'CUSTOMS', agentName: '通用', defaultAgent: false },
          { agentCode: 'ILLUMNA-CUSTOMS', agentName: '因美纳', defaultAgent: true },
        ],
      })
    ).toEqual([
      { agentCode: 'CUSTOMS', agentName: '通用', isDefault: false },
      { agentCode: 'ILLUMNA-CUSTOMS', agentName: '因美纳', isDefault: true },
    ]);
  });

  it('always uploads with an authorized agent and autoStart enabled', () => {
    expect(buildDocumentUploadData(' ILLUMNA-CUSTOMS ')).toEqual({
      agentCode: 'ILLUMNA-CUSTOMS',
      autoStart: true,
    });
    expect(() => buildDocumentUploadData('')).toThrow('请选择智能体');
  });

  it('polls only active rows, exposes failed retry, and navigates completed rows', () => {
    expect(isActiveDocumentStatus('QUEUED')).toBe(true);
    expect(isActiveDocumentStatus('PARSING')).toBe(true);
    expect(isActiveDocumentStatus('COMPLETED')).toBe(false);
    expect(canRetryDocument('NOT_STARTED')).toBe(true);
    expect(canRetryDocument('FAILED')).toBe(true);
    expect(canRetryDocument('TIMEOUT')).toBe(true);
    expect(canRetryDocument('PARSING')).toBe(false);
    expect(getDocumentNavigationPath({ status: 'COMPLETED', decHeadId: '12', singleWindowPath: '/custom/result/12' })).toBe('/custom/result/12');
    expect(getDocumentNavigationPath({ status: 'COMPLETED', decHeadId: 13 })).toBe('/custom/cit/single-window?decHeadId=13');
    expect(getDocumentNavigationPath({ status: 'PARSING', decHeadId: 13 })).toBeUndefined();
  });
});
