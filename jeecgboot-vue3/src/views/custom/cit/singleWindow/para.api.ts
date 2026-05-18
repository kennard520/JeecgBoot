import { defHttp } from '/@/utils/http/axios';
import type { CitPage, CitRecord, CitSelectOption, ParaOptionSourceKey } from './types';

const PARA_BASE_URL = '/custom/para';

const paraApiPathMap = {
  paraAgreementRate: `${PARA_BASE_URL}/paraAgreementRate`,
  paraCert: `${PARA_BASE_URL}/paraCert`,
  paraComplex: `${PARA_BASE_URL}/paraComplex`,
  paraCorrelationReason: `${PARA_BASE_URL}/paraCorrelationReason`,
  paraCountry: `${PARA_BASE_URL}/paraCountry`,
  paraCredential: `${PARA_BASE_URL}/paraCredential`,
  paraCurr: `${PARA_BASE_URL}/paraCurr`,
  paraCustomsRel: `${PARA_BASE_URL}/paraCustomsRel`,
  paraDistrictRel: `${PARA_BASE_URL}/paraDistrictRel`,
  paraEntryport: `${PARA_BASE_URL}/paraEntryport`,
  paraGoodsAttr: `${PARA_BASE_URL}/paraGoodsAttr`,
  paraInvttype: `${PARA_BASE_URL}/paraInvttype`,
  paraLevymode: `${PARA_BASE_URL}/paraLevymode`,
  paraLevytype: `${PARA_BASE_URL}/paraLevytype`,
  paraPortLin: `${PARA_BASE_URL}/paraPortLin`,
  paraPurpose: `${PARA_BASE_URL}/paraPurpose`,
  paraTrade: `${PARA_BASE_URL}/paraTrade`,
  paraTransac: `${PARA_BASE_URL}/paraTransac`,
  paraTransf: `${PARA_BASE_URL}/paraTransf`,
  paraUnit: `${PARA_BASE_URL}/paraUnit`,
  paraWrap: `${PARA_BASE_URL}/paraWrap`,
} as const;

type ParaEntityKey = keyof typeof paraApiPathMap;

interface ParaOptionSourceConfig {
  entityKey: ParaEntityKey;
  valueField: string;
  textField: string;
  pageSize?: number;
  autoload?: boolean;
}

const paraOptionSourceMap: Record<ParaOptionSourceKey, ParaOptionSourceConfig> = {
  agreement: { entityKey: 'paraAgreementRate', valueField: 'agreementCode', textField: 'agreementCode', pageSize: 500, autoload: false },
  cert: { entityKey: 'paraCert', valueField: 'certCo', textField: 'certName', pageSize: 500 },
  complex: { entityKey: 'paraComplex', valueField: 'codeTS', textField: 'gName', pageSize: 80, autoload: false },
  correlationReason: { entityKey: 'paraCorrelationReason', valueField: 'code', textField: 'text', pageSize: 200 },
  country: { entityKey: 'paraCountry', valueField: 'countryCo', textField: 'countryNa', pageSize: 3000 },
  credential: { entityKey: 'paraCredential', valueField: 'code', textField: 'name', pageSize: 500 },
  currency: { entityKey: 'paraCurr', valueField: 'currCode', textField: 'currName', pageSize: 500 },
  customs: { entityKey: 'paraCustomsRel', valueField: 'customsCode', textField: 'customsName', pageSize: 1000 },
  district: { entityKey: 'paraDistrictRel', valueField: 'districtCode', textField: 'districtName', pageSize: 5000 },
  entryPort: { entityKey: 'paraEntryport', valueField: 'portCode', textField: 'portCCod', pageSize: 2000 },
  goodsAttr: { entityKey: 'paraGoodsAttr', valueField: 'attrCode', textField: 'attrName', pageSize: 100 },
  invtType: { entityKey: 'paraInvttype', valueField: 'cCode', textField: 'cName', pageSize: 200 },
  levyMode: { entityKey: 'paraLevymode', valueField: 'dutyMode', textField: 'dutySpec', pageSize: 200 },
  levyType: { entityKey: 'paraLevytype', valueField: 'cutMode', textField: 'fullCut', pageSize: 500 },
  port: { entityKey: 'paraPortLin', valueField: 'portCode', textField: 'portCCod', pageSize: 5000 },
  purpose: { entityKey: 'paraPurpose', valueField: 'cCode', textField: 'cName', pageSize: 200 },
  tradeMode: { entityKey: 'paraTrade', valueField: 'tradeMode', textField: 'abbrTrade', pageSize: 1000 },
  transac: { entityKey: 'paraTransac', valueField: 'transMode', textField: 'transSpec', pageSize: 200 },
  transport: { entityKey: 'paraTransf', valueField: 'trafCode', textField: 'trafSpec', pageSize: 200 },
  unit: { entityKey: 'paraUnit', valueField: 'unitCode', textField: 'unitName', pageSize: 500 },
  wrap: { entityKey: 'paraWrap', valueField: 'wrapCode', textField: 'wrapName', pageSize: 300 },
};

function normalizeValue(value: unknown) {
  return value === undefined || value === null ? '' : String(value).trim();
}

function normalizeRecordKey(key: string) {
  return key.replace(/_/g, '').toLowerCase();
}

function getRecordValue(record: CitRecord, field: string) {
  if (Object.prototype.hasOwnProperty.call(record, field)) {
    return record[field];
  }
  const normalizedField = normalizeRecordKey(field);
  const matchedKey = Object.keys(record).find((key) => normalizeRecordKey(key) === normalizedField);
  return matchedKey ? record[matchedKey] : undefined;
}

function buildSearchParams(config: ParaOptionSourceConfig, keyword?: string, searchField?: string) {
  const params: CitRecord = {
    pageNo: 1,
    pageSize: config.pageSize || 500,
  };
  const text = normalizeValue(keyword);
  if (text && searchField) {
    params[searchField] = `*${text}*`;
  }
  return params;
}

function toOption(record: CitRecord, config: ParaOptionSourceConfig): CitSelectOption | undefined {
  const value = normalizeValue(getRecordValue(record, config.valueField));
  if (!value) return undefined;
  const text = normalizeValue(getRecordValue(record, config.textField)) || value;
  return {
    value,
    label: text === value ? value : `${value}|${text}`,
    sourceRecord: record,
  };
}

function uniqueOptions(options: CitSelectOption[]) {
  const map = new Map<string | number, CitSelectOption>();
  options.forEach((item) => {
    if (!map.has(item.value)) {
      map.set(item.value, item);
    }
  });
  return Array.from(map.values());
}

async function queryParaRecords(entityKey: ParaEntityKey, params: CitRecord) {
  const page = await defHttp.get<CitPage<CitRecord>>({ url: `${paraApiPathMap[entityKey]}/list`, params });
  return Array.isArray(page) ? page : page?.records || [];
}

export function isParaSourceAutoload(source: ParaOptionSourceKey) {
  return paraOptionSourceMap[source].autoload !== false;
}

export async function queryParaOptions(source: ParaOptionSourceKey, keyword?: string) {
  const config = paraOptionSourceMap[source];
  const text = normalizeValue(keyword);
  const searchFields = text ? Array.from(new Set([config.valueField, config.textField])) : [undefined];
  const rowsGroup = await Promise.all(searchFields.map((field) => queryParaRecords(config.entityKey, buildSearchParams(config, text, field))));
  const rows = rowsGroup.flat();
  return uniqueOptions(rows.map((item) => toOption(item, config)).filter(Boolean) as CitSelectOption[]);
}
