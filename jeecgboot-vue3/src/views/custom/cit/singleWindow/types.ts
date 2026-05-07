export type CitEntityKey =
  | 'citAttributes'
  | 'decHead'
  | 'decList'
  | 'decContainer'
  | 'decLicenseDocus'
  | 'decRequestCert'
  | 'decGoodsLimit'
  | 'decGoodsLimitVin'
  | 'decOtherPack'
  | 'decCopLimit'
  | 'decUser'
  | 'decMarkLob'
  | 'decFreeTxt'
  | 'decSign'
  | 'trnHead'
  | 'trnList'
  | 'trnContainer'
  | 'trnContaGoodsList'
  | 'decSupplement'
  | 'edocRelation'
  | 'ecoRelation'
  | 'sddTax'
  | 'decRisk'
  | 'decCopPromise'
  | 'decRoyaltyFee'
  | 'decTpAccess'
  | 'decControlModel';

export type CitFieldType = 'input' | 'textarea' | 'select' | 'date' | 'number';

export interface CitSelectOption {
  label: string;
  value: string | number;
  sourceRecord?: Record<string, any>;
}

export type ParaOptionSourceKey =
  | 'cert'
  | 'complex'
  | 'country'
  | 'currency'
  | 'customs'
  | 'district'
  | 'entryPort'
  | 'invtType'
  | 'levyMode'
  | 'levyType'
  | 'port'
  | 'tradeMode'
  | 'transac'
  | 'transport'
  | 'unit'
  | 'wrap';

export type ParaOptionMap = Partial<Record<ParaOptionSourceKey, CitSelectOption[]>>;
export type ParaOptionLoadingMap = Partial<Record<ParaOptionSourceKey, boolean>>;

export interface CitFieldConfig {
  field: string;
  label: string;
  type?: CitFieldType;
  required?: boolean;
  readonly?: boolean;
  placeholder?: string;
  options?: CitSelectOption[];
  optionSource?: ParaOptionSourceKey;
  maxLength?: number;
  span?: number;
}

export interface CitSectionConfig {
  title: string;
  fields: CitFieldConfig[];
}

export interface CitTableColumn {
  title: string;
  dataIndex: string;
  width?: number;
  fixed?: 'left' | 'right';
  align?: 'left' | 'center' | 'right';
  ellipsis?: boolean;
}

export interface CitPage<T = CitRecord> {
  records?: T[];
  total?: number;
  current?: number;
  size?: number;
}

export interface CitRecord {
  id?: string | number;
  [key: string]: any;
}

export interface DecHead extends CitRecord {
  seqNo?: string;
  preEntryId?: string;
  entryId?: string;
  ieFlag?: string;
  customMaster?: string;
  iePort?: string;
  tradeName?: string;
  ownerName?: string;
  agentName?: string;
  copName?: string;
}

export interface DecList extends CitRecord {
  decHeadId?: string | number;
  gNo?: number;
  codeTs?: string;
  gName?: string;
  gModel?: string;
  gQty?: string | number;
  gUnit?: string;
  declPrice?: string | number;
  declTotal?: string | number;
  tradeCurr?: string;
}

export interface RelatedTabConfig {
  key: CitEntityKey;
  title: string;
  parentField: 'decHeadId' | 'decListId' | 'decGoodsLimitId' | 'trnHeadId' | 'trnListId' | 'trnContainerId';
  parentSource: 'head' | 'goods';
  columns: CitTableColumn[];
  formFields: CitFieldConfig[];
}
