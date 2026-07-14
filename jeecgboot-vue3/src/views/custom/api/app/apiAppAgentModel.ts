function normalizeCodes(value: unknown): string[] {
  const values = Array.isArray(value) ? value : `${value || ''}`.split(',');
  return [
    ...new Set(
      values
        .map((item: any) => (typeof item === 'object' ? item.agentCode || item.code || item.value : item))
        .map((item) => `${item || ''}`.trim())
        .filter(Boolean)
    ),
  ];
}

export function normalizeApiAppAgentFields(record: any) {
  const allowedAgentCodes = normalizeCodes(record?.allowedAgentCodes ?? record?.agentCodes ?? record?.agents);
  const compatibleCode = `${record?.companyCode || ''}`.trim();
  if (allowedAgentCodes.length === 0 && compatibleCode) {
    allowedAgentCodes.push(compatibleCode);
  }
  const requestedDefault = `${record?.defaultAgentCode || compatibleCode || ''}`.trim();
  const defaultAgentCode = allowedAgentCodes.includes(requestedDefault) ? requestedDefault : allowedAgentCodes[0];
  return { allowedAgentCodes, defaultAgentCode };
}

export function buildApiAppAgentPayload<T extends Record<string, any>>(
  values: T
): T & {
  allowedAgentCodes: string[];
  defaultAgentCode: string;
  companyCode: string;
} {
  const allowedAgentCodes = normalizeCodes(values.allowedAgentCodes ?? values.agentCodes);
  if (allowedAgentCodes.length === 0) {
    throw new Error('请至少选择一个可用智能体');
  }
  const defaultAgentCode = `${values.defaultAgentCode || ''}`.trim();
  if (!defaultAgentCode) {
    throw new Error('请选择默认智能体');
  }
  if (!allowedAgentCodes.includes(defaultAgentCode)) {
    throw new Error('默认智能体必须包含在可用智能体中');
  }
  return {
    ...values,
    allowedAgentCodes,
    defaultAgentCode,
    companyCode: defaultAgentCode,
  };
}
