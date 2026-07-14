export interface AgentOption {
  agentCode: string;
  agentName: string;
  isDefault?: boolean;
}

export type AgentControlMode = 'disabled' | 'readonly' | 'select';

export interface AgentSelectionState {
  mode: AgentControlMode;
  options: AgentOption[];
  selectedAgentCode?: string;
  uploadDisabled: boolean;
}

const ACTIVE_DOCUMENT_STATUSES = new Set(['QUEUED', 'PARSING']);
const RETRYABLE_DOCUMENT_STATUSES = new Set(['NOT_STARTED', 'FAILED', 'TIMEOUT']);

function isEnabled(value: unknown): boolean {
  return value === undefined || value === null || value === true || value === 1 || value === '1';
}

function isDefault(value: unknown): boolean {
  return value === true || value === 1 || value === '1';
}

export function normalizeAgentOptions(payload: any): AgentOption[] {
  const source = Array.isArray(payload) ? payload : payload?.agents || payload?.records || payload?.result?.agents || payload?.result?.records || [];
  const seen = new Set<string>();

  return source.reduce((options: AgentOption[], item: any) => {
    if (!item || !isEnabled(item.enabled)) {
      return options;
    }
    const agentCode = `${item.agentCode || item.code || item.companyCode || ''}`.trim();
    if (!agentCode || seen.has(agentCode)) {
      return options;
    }
    seen.add(agentCode);
    options.push({
      agentCode,
      agentName: `${item.agentName || item.name || item.companyName || agentCode}`.trim(),
      isDefault: isDefault(item.defaultAgent ?? item.isDefault ?? item.default),
    });
    return options;
  }, []);
}

export function createAgentSelectionState(options: AgentOption[], preferredAgentCode?: string): AgentSelectionState {
  if (options.length === 0) {
    return {
      mode: 'disabled',
      options,
      selectedAgentCode: undefined,
      uploadDisabled: true,
    };
  }

  const preferred = `${preferredAgentCode || ''}`.trim();
  const selected = options.find((item) => item.agentCode === preferred) || options.find((item) => item.isDefault) || options[0];

  return {
    mode: options.length === 1 ? 'readonly' : 'select',
    options,
    selectedAgentCode: selected.agentCode,
    uploadDisabled: false,
  };
}

export function buildDocumentUploadData(agentCode: string) {
  const normalizedAgentCode = `${agentCode || ''}`.trim();
  if (!normalizedAgentCode) {
    throw new Error('请选择智能体');
  }
  return {
    agentCode: normalizedAgentCode,
    autoStart: true,
  };
}

export function isActiveDocumentStatus(status?: string): boolean {
  return ACTIVE_DOCUMENT_STATUSES.has(`${status || ''}`.toUpperCase());
}

export function canRetryDocument(status?: string): boolean {
  return RETRYABLE_DOCUMENT_STATUSES.has(`${status || ''}`.toUpperCase());
}

export function getDocumentNavigationPath(record: any): string | undefined {
  if (`${record?.status || ''}`.toUpperCase() !== 'COMPLETED' || !record?.decHeadId) {
    return undefined;
  }
  return record.singleWindowPath || `/custom/cit/single-window?decHeadId=${record.decHeadId}`;
}
