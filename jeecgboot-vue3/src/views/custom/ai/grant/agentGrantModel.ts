export interface AgentGrantDraft {
  id?: string | number;
  customerCode: string;
  userId: string;
  username?: string;
  realname?: string;
  agentCodes: string[];
  defaultAgentCode: string;
}

function normalizeCodes(value: unknown): string[] {
  const values = Array.isArray(value) ? value : `${value || ''}`.split(',');
  return [...new Set(values.map((item) => `${item || ''}`.trim()).filter(Boolean))];
}

export function normalizeAgentGrantRecord(record: any): AgentGrantDraft {
  const normalized: AgentGrantDraft = {
    customerCode: `${record?.customerCode || ''}`.trim(),
    userId: `${record?.userId || ''}`.trim(),
    username: `${record?.username || ''}`.trim() || undefined,
    agentCodes: normalizeCodes(record?.agentCodes ?? record?.allowedAgentCodes),
    defaultAgentCode: `${record?.defaultAgentCode || record?.agentCode || ''}`.trim(),
  };
  if (record?.id !== undefined && record?.id !== null) {
    normalized.id = record.id;
  }
  if (record?.realname) {
    normalized.realname = `${record.realname}`.trim();
  }
  return normalized;
}

export function validateAgentGrantDraft(draft: AgentGrantDraft): string | undefined {
  if (!`${draft.customerCode || ''}`.trim()) {
    return '请选择客户';
  }
  if (!`${draft.userId || ''}`.trim()) {
    return '请选择用户';
  }
  const agentCodes = normalizeCodes(draft.agentCodes);
  if (agentCodes.length === 0) {
    return '请至少选择一个智能体';
  }
  const defaultAgentCode = `${draft.defaultAgentCode || ''}`.trim();
  if (!defaultAgentCode) {
    return '请选择默认智能体';
  }
  if (!agentCodes.includes(defaultAgentCode)) {
    return '默认智能体必须包含在可用智能体中';
  }
  return undefined;
}

export function buildAgentGrantPayload(draft: AgentGrantDraft): AgentGrantDraft {
  const normalized = normalizeAgentGrantRecord(draft);
  const error = validateAgentGrantDraft(normalized);
  if (error) {
    throw new Error(error);
  }
  return normalized;
}
