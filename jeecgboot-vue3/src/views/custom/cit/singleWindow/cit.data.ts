import type { CitFieldConfig, CitSectionConfig, CitTableColumn, ParaOptionSourceKey, RelatedTabConfig } from './types';

const yesNoOptions = [
  { label: '0|否', value: '0' },
  { label: '1|是', value: '1' },
];

export const headRequiredFields = [
  'agentCode',
  'agentName',
  'copCode',
  'copName',
  'customMaster',
  'declTrnRel',
  'distinatePort',
  'ediId',
  'entryType',
  'grossWet',
  'ieFlag',
  'iePort',
  'inputerName',
  'netWt',
  'ownerName',
  'packNo',
  'tradeCountry',
  'tradeMode',
  'tradeCode',
  'trafMode',
  'tradeName',
  'transMode',
  'type',
  'typistNo',
  'wrapType',
  'tradeCoScc',
  'agentCodeScc',
  'ownerCodeScc',
  'promiseItmes',
  'tradeAreaCode',
  'despPortCode',
  'entyPortCode',
  'goodsPlace',
  'declareName',
];

const headSelectOptions: Record<string, Array<{ label: string; value: string }>> = {
  ieFlag: [
    { label: 'I|进口', value: 'I' },
    { label: 'E|出口', value: 'E' },
  ],
  declTrnRel: [
    { label: '0|一般报关单', value: '0' },
    { label: '1|转关提前报关单', value: '1' },
  ],
  ediId: [
    { label: '1|普通报关', value: '1' },
    { label: '3|北方转关提前', value: '3' },
    { label: '5|南方转关提前', value: '5' },
    { label: '6|南方 H2000 直转', value: '6' },
  ],
  entryType: [
    { label: '0|普通报关单', value: '0' },
    { label: 'L|带清单报关单', value: 'L' },
    { label: 'W|无纸报关', value: 'W' },
    { label: 'D|清单无纸', value: 'D' },
    { label: 'M|无纸化通关', value: 'M' },
  ],
};

function field(config: CitFieldConfig): CitFieldConfig {
  return {
    type: 'input',
    span: 12,
    required: headRequiredFields.includes(config.field),
    options: headSelectOptions[config.field],
    ...config,
  };
}

export const headSections: CitSectionConfig[] = [
  {
    title: '单证基础信息',
    fields: [
      field({ field: 'customMaster', label: '申报地海关', type: 'select', optionSource: 'customs', maxLength: 4 }),
      field({ field: 'seqNo', label: '统一编号', readonly: true }),
      field({ field: 'preEntryId', label: '预录入编号', readonly: true }),
      field({ field: 'entryId', label: '海关编号', readonly: true }),
      field({ field: 'ieFlag', label: '进出口标志', type: 'select' }),
      field({ field: 'declTrnRel', label: '报关/转关', type: 'select' }),
      field({ field: 'ediId', label: '报关标志', type: 'select' }),
      field({ field: 'entryType', label: '报关单类型', type: 'select' }),
      field({ field: 'iePort', label: '进出境关别', type: 'select', optionSource: 'customs', maxLength: 4 }),
      field({ field: 'ieDate', label: '进出口日期', type: 'date' }),
      field({ field: 'manualNo', label: '备案号', maxLength: 12 }),
      field({ field: 'contrNo', label: '合同协议号', maxLength: 32 }),
      field({ field: 'tradeMode', label: '监管方式', type: 'select', optionSource: 'tradeMode', maxLength: 4 }),
      field({ field: 'licenseNo', label: '许可证号', maxLength: 20 }),
      field({ field: 'type', label: '单据类型', maxLength: 6 }),
      field({ field: 'promiseItmes', label: '价格说明', maxLength: 32 }),
    ],
  },
  {
    title: '企业与申报人',
    fields: [
      field({ field: 'tradeCode', label: '收发货人代码', maxLength: 18 }),
      field({ field: 'tradeCoScc', label: '收发货人统一编码', maxLength: 18 }),
      field({ field: 'tradeName', label: '收发货人名称', maxLength: 100, span: 24 }),
      field({ field: 'ownerCode', label: '生产销售单位代码', maxLength: 18 }),
      field({ field: 'ownerCodeScc', label: '生产销售单位统一编码', maxLength: 18 }),
      field({ field: 'ownerName', label: '生产销售单位名称', maxLength: 100, span: 24 }),
      field({ field: 'agentCode', label: '申报单位代码', maxLength: 10 }),
      field({ field: 'agentCodeScc', label: '申报单位统一编码', maxLength: 18 }),
      field({ field: 'agentName', label: '申报单位名称', maxLength: 100, span: 24 }),
      field({ field: 'copCode', label: '录入单位代码', maxLength: 9 }),
      field({ field: 'copCodeScc', label: '录入单位统一编码', maxLength: 18 }),
      field({ field: 'copName', label: '录入单位名称', maxLength: 70, span: 24 }),
      field({ field: 'declareName', label: '申报人员姓名', maxLength: 50 }),
      field({ field: 'inputerName', label: '录入员名称', maxLength: 32 }),
      field({ field: 'typistNo', label: '录入员 IC 卡号', maxLength: 30 }),
    ],
  },
  {
    title: '运输、贸易与重量',
    fields: [
      field({ field: 'trafMode', label: '运输方式', type: 'select', optionSource: 'transport', maxLength: 1 }),
      field({ field: 'trafName', label: '运输工具名称', maxLength: 50, span: 24 }),
      field({ field: 'billNo', label: '提运单号', maxLength: 32, span: 24 }),
      field({ field: 'billType', label: '备案清单类型', type: 'select', optionSource: 'invtType', maxLength: 1 }),
      field({ field: 'tradeCountry', label: '启运国/运抵国', type: 'select', optionSource: 'country', maxLength: 3 }),
      field({ field: 'tradeAreaCode', label: '贸易国别', type: 'select', optionSource: 'country', maxLength: 3 }),
      field({ field: 'distinatePort', label: '经停港/指运港', type: 'select', optionSource: 'port', maxLength: 6 }),
      field({ field: 'transMode', label: '成交方式', type: 'select', optionSource: 'transac' }),
      field({ field: 'wrapType', label: '包装种类', type: 'select', optionSource: 'wrap', maxLength: 2 }),
      field({ field: 'packNo', label: '件数', type: 'number' }),
      field({ field: 'grossWet', label: '毛重 KG' }),
      field({ field: 'netWt', label: '净重 KG' }),
      field({ field: 'feeMark', label: '运费标记', maxLength: 1 }),
      field({ field: 'feeRate', label: '运费/率' }),
      field({ field: 'feeCurr', label: '运费币制', type: 'select', optionSource: 'currency', maxLength: 3 }),
      field({ field: 'insurMark', label: '保险费标记', maxLength: 1 }),
      field({ field: 'insurRate', label: '保险费/率' }),
      field({ field: 'insurCurr', label: '保险费币制', type: 'select', optionSource: 'currency', maxLength: 3 }),
      field({ field: 'otherMark', label: '杂费标记', maxLength: 1 }),
      field({ field: 'otherRate', label: '杂费/率' }),
      field({ field: 'otherCurr', label: '杂费币制', type: 'select', optionSource: 'currency', maxLength: 3 }),
    ],
  },
  {
    title: '检验检疫与备注',
    fields: [
      field({ field: 'despPortCode', label: '启运港代码', type: 'select', optionSource: 'port', maxLength: 8 }),
      field({ field: 'entyPortCode', label: '入境口岸代码', type: 'select', optionSource: 'entryPort', maxLength: 8 }),
      field({ field: 'goodsPlace', label: '货物存放地点', maxLength: 100, span: 24 }),
      field({ field: 'blNo', label: 'B/L 号', maxLength: 50 }),
      field({ field: 'apprNo', label: '批准文号', maxLength: 32 }),
      field({ field: 'cutMode', label: '征免性质', type: 'select', optionSource: 'levyType', maxLength: 3 }),
      field({ field: 'specDeclFlag', label: '特种业务标识', maxLength: 10 }),
      field({ field: 'markNo', label: '标记唛码', type: 'textarea', span: 24 }),
      field({ field: 'noteS', label: '备注', type: 'textarea', span: 24 }),
    ],
  },
];

export const headListColumns: CitTableColumn[] = [
  { title: '统一编号', dataIndex: 'seqNo', width: 140, ellipsis: true },
  { title: '预录入编号', dataIndex: 'preEntryId', width: 120, ellipsis: true },
  { title: '海关编号', dataIndex: 'entryId', width: 140, ellipsis: true },
  { title: '收发货人', dataIndex: 'tradeName', width: 180, ellipsis: true },
  { title: '进出口', dataIndex: 'ieFlag', width: 70, align: 'center' },
];

export const goodsRequiredFields = [
  'codeTs',
  'declPrice',
  'declTotal',
  'dutyMode',
  'firstUnit',
  'firstQty',
  'gUnit',
  'gName',
  'gNo',
  'gQty',
  'originCountry',
  'secondQty',
  'tradeCurr',
  'destinationCountry',
  'districtCode',
];

function goodsField(config: CitFieldConfig): CitFieldConfig {
  return {
    type: 'input',
    span: 12,
    required: goodsRequiredFields.includes(config.field),
    ...config,
  };
}

export const goodsFormFields: CitFieldConfig[] = [
  goodsField({ field: 'gNo', label: '商品序号', type: 'number' }),
  goodsField({ field: 'codeTs', label: '商品编号', type: 'select', optionSource: 'complex', maxLength: 10 }),
  goodsField({ field: 'gName', label: '商品名称', maxLength: 255, span: 24 }),
  goodsField({ field: 'gModel', label: '规格型号', type: 'textarea', span: 24 }),
  goodsField({ field: 'gQty', label: '成交数量' }),
  goodsField({ field: 'gUnit', label: '成交单位', type: 'select', optionSource: 'unit', maxLength: 3 }),
  goodsField({ field: 'declPrice', label: '申报单价' }),
  goodsField({ field: 'declTotal', label: '申报总价' }),
  goodsField({ field: 'tradeCurr', label: '成交币制', type: 'select', optionSource: 'currency', maxLength: 3 }),
  goodsField({ field: 'originCountry', label: '原产国', type: 'select', optionSource: 'country', maxLength: 3 }),
  goodsField({ field: 'destinationCountry', label: '最终目的国', type: 'select', optionSource: 'country', maxLength: 3 }),
  goodsField({ field: 'dutyMode', label: '征免方式', type: 'select', optionSource: 'levyMode', maxLength: 1 }),
  goodsField({ field: 'firstQty', label: '第一法定数量' }),
  goodsField({ field: 'firstUnit', label: '第一法定单位', type: 'select', optionSource: 'unit', maxLength: 3 }),
  goodsField({ field: 'secondQty', label: '第二法定数量' }),
  goodsField({ field: 'secondUnit', label: '第二计量单位', type: 'select', optionSource: 'unit', maxLength: 3 }),
  goodsField({ field: 'districtCode', label: '境内目的地/货源地', type: 'select', optionSource: 'district', maxLength: 5 }),
  goodsField({ field: 'ciqCode', label: '监管类别', maxLength: 20 }),
  goodsField({ field: 'contrItem', label: '备案序号', type: 'number' }),
];

export const goodsColumns: CitTableColumn[] = [
  { title: '项号', dataIndex: 'gNo', width: 70, align: 'center' },
  { title: '商品编号', dataIndex: 'codeTs', width: 110 },
  { title: '商品名称', dataIndex: 'gName', width: 180, ellipsis: true },
  { title: '规格型号', dataIndex: 'gModel', width: 220, ellipsis: true },
  { title: '成交数量', dataIndex: 'gQty', width: 100, align: 'right' },
  { title: '成交单位', dataIndex: 'gUnit', width: 90, align: 'center' },
  { title: '单价', dataIndex: 'declPrice', width: 90, align: 'right' },
  { title: '总价', dataIndex: 'declTotal', width: 100, align: 'right' },
  { title: '币制', dataIndex: 'tradeCurr', width: 80, align: 'center' },
  { title: '原产国', dataIndex: 'originCountry', width: 90, align: 'center' },
  { title: '最终目的国', dataIndex: 'destinationCountry', width: 100, align: 'center' },
  { title: '征免方式', dataIndex: 'dutyMode', width: 90, align: 'center' },
];

const compactHeadFields = [
  { field: 'decHeadId', label: '表头ID', readonly: true },
  { field: 'decListId', label: '商品ID', readonly: true },
];

export const relatedTabConfigs: RelatedTabConfig[] = [
  {
    key: 'decContainer',
    title: '集装箱',
    parentField: 'decHeadId',
    parentSource: 'head',
    columns: [
      { title: '集装箱号', dataIndex: 'containerId', width: 140 },
      { title: '规格', dataIndex: 'containerMd', width: 100 },
      { title: '商品项号关系', dataIndex: 'goodsNo', width: 180, ellipsis: true },
      { title: '自重', dataIndex: 'containerWt', width: 90, align: 'right' },
    ],
    formFields: [
      ...compactHeadFields.slice(0, 1),
      { field: 'decListId', label: '商品ID' },
      { field: 'containerId', label: '集装箱号', required: true, maxLength: 32 },
      { field: 'containerMd', label: '集装箱规格', required: true, maxLength: 32 },
      { field: 'goodsNo', label: '商品项号关系', required: true, maxLength: 255 },
      { field: 'containerWt', label: '集装箱自重' },
    ],
  },
  {
    key: 'decLicenseDocus',
    title: '随附单证',
    parentField: 'decHeadId',
    parentSource: 'head',
    columns: [
      { title: '单证代码', dataIndex: 'docuCode', width: 100 },
      { title: '单证编号', dataIndex: 'certCode', width: 180 },
    ],
    formFields: [
      ...compactHeadFields.slice(0, 1),
      { field: 'docuCode', label: '随附单证代码', required: true, type: 'select', optionSource: 'cert', maxLength: 1 },
      { field: 'certCode', label: '随附单证编号', required: true, maxLength: 32 },
    ],
  },
  {
    key: 'decRequestCert',
    title: '检验检疫证书',
    parentField: 'decHeadId',
    parentSource: 'head',
    columns: [{ title: '申请证书代码', dataIndex: 'appCertCode', width: 180 }],
    formFields: [
      ...compactHeadFields.slice(0, 1),
      { field: 'appCertCode', label: '申请证书代码', required: true, type: 'select', optionSource: 'cert', maxLength: 50 },
    ],
  },
  {
    key: 'decGoodsLimit',
    title: '货物许可证',
    parentField: 'decHeadId',
    parentSource: 'head',
    columns: [
      { title: '商品序号', dataIndex: 'goodsNo', width: 90, align: 'center' },
      { title: '许可证类别', dataIndex: 'licTypeCode', width: 120 },
      { title: '许可证编号', dataIndex: 'licenceNo', width: 180 },
    ],
    formFields: [
      ...compactHeadFields,
      { field: 'goodsNo', label: '商品序号', required: true, type: 'number' },
      { field: 'licTypeCode', label: '许可证类别', required: true, maxLength: 5 },
      { field: 'licenceNo', label: '许可证编号', required: true, maxLength: 40 },
    ],
  },
  {
    key: 'decOtherPack',
    title: '其他包装',
    parentField: 'decHeadId',
    parentSource: 'head',
    columns: [
      { title: '包装数量', dataIndex: 'packQty', width: 110, align: 'right' },
      { title: '包装种类', dataIndex: 'packType', width: 110 },
    ],
    formFields: [
      ...compactHeadFields.slice(0, 1),
      { field: 'packQty', label: '包装数量', required: true },
      { field: 'packType', label: '包装种类', required: true, type: 'select', optionSource: 'wrap', maxLength: 2 },
    ],
  },
  {
    key: 'decCopLimit',
    title: '企业资质',
    parentField: 'decHeadId',
    parentSource: 'head',
    columns: [{ title: '资质类别代码', dataIndex: 'entQualifTypeCode', width: 150 }],
    formFields: [
      ...compactHeadFields.slice(0, 1),
      { field: 'entQualifTypeCode', label: '资质类别代码', required: true, maxLength: 5 },
    ],
  },
  {
    key: 'decMarkLob',
    title: '标记唛码附件',
    parentField: 'decHeadId',
    parentSource: 'head',
    columns: [
      { title: '附件名称', dataIndex: 'attachName', width: 180, ellipsis: true },
      { title: '附件类型', dataIndex: 'attachType', width: 120 },
    ],
    formFields: [
      ...compactHeadFields.slice(0, 1),
      { field: 'attachName', label: '附件名称', maxLength: 100 },
      { field: 'attachType', label: '附件类型', maxLength: 20 },
    ],
  },
  {
    key: 'decFreeTxt',
    title: '自由文本',
    parentField: 'decHeadId',
    parentSource: 'head',
    columns: [
      { title: '监管仓号', dataIndex: 'relId', width: 140 },
      { title: '报关员联系方式', dataIndex: 'bonNo', width: 160 },
      { title: '备案清单号', dataIndex: 'decNo', width: 140 },
      { title: '关联理由', dataIndex: 'cusFie', width: 140, ellipsis: true },
    ],
    formFields: [
      ...compactHeadFields.slice(0, 1),
      { field: 'relId', label: '监管仓号' },
      { field: 'bonNo', label: '报关员联系方式' },
      { field: 'decNo', label: '备案清单号' },
      { field: 'decBpNo', label: '报关单联系单号' },
      { field: 'relManNo', label: '关联备案号' },
      { field: 'voyNo', label: '航次号' },
      { field: 'cusFie', label: '关联理由/自定义字段', type: 'textarea', span: 24 },
    ],
  },
  {
    key: 'decRisk',
    title: '风险评估',
    parentField: 'decHeadId',
    parentSource: 'head',
    columns: [
      { title: '风险结果', dataIndex: 'risk', width: 120 },
      { title: '处理日期', dataIndex: 'signDate', width: 160 },
      { title: '备注', dataIndex: 'note', width: 220, ellipsis: true },
    ],
    formFields: [
      ...compactHeadFields.slice(0, 1),
      { field: 'risk', label: '风险评估结果', maxLength: 10 },
      { field: 'sign', label: '数字签名信息', maxLength: 255 },
      { field: 'signDate', label: '处理日期', type: 'date' },
      { field: 'note', label: '备注', type: 'textarea', span: 24 },
    ],
  },
  {
    key: 'decCopPromise',
    title: '企业承诺',
    parentField: 'decHeadId',
    parentSource: 'head',
    columns: [{ title: '声明材料代码', dataIndex: 'declaratioMaterialCode', width: 160 }],
    formFields: [
      ...compactHeadFields.slice(0, 1),
      { field: 'declaratioMaterialCode', label: '证明/声明材料代码', required: true, maxLength: 10 },
    ],
  },
  {
    key: 'decTpAccess',
    title: '两段准入',
    parentField: 'decHeadId',
    parentSource: 'head',
    columns: [
      { title: '转场申请', dataIndex: 'transitionApply', width: 100 },
      { title: '转入场所', dataIndex: 'transitionSite', width: 140 },
      { title: '附条件提离', dataIndex: 'conditionalLiftoffApply', width: 120 },
    ],
    formFields: [
      ...compactHeadFields.slice(0, 1),
      { field: 'transitionApply', label: '转场申请', type: 'select', options: yesNoOptions },
      { field: 'transitionSite', label: '转入场所场地', maxLength: 11 },
      { field: 'conditionalLiftoffApply', label: '附条件提离申请', type: 'select', options: yesNoOptions },
      { field: 'portDestMergeCheckApply', label: '口岸目的地合并检查', type: 'select', options: yesNoOptions },
    ],
  },
  {
    key: 'decSupplement',
    title: '补充申报',
    parentField: 'decListId',
    parentSource: 'goods',
    columns: [
      { title: '补充类型', dataIndex: 'supType', width: 120 },
      { title: '商品ID', dataIndex: 'decListId', width: 90 },
      { title: '货物说明', dataIndex: 'goodsNote', width: 180, ellipsis: true },
      { title: '其他说明', dataIndex: 'otherNote', width: 180, ellipsis: true },
    ],
    formFields: [
      ...compactHeadFields,
      { field: 'supType', label: '补充申报类型' },
      { field: 'gNo', label: '商品序号', type: 'number' },
      { field: 'brandCn', label: '中文品牌' },
      { field: 'brandEn', label: '英文品牌' },
      { field: 'goodsNote', label: '货物说明', type: 'textarea', span: 24 },
      { field: 'otherNote', label: '其他说明', type: 'textarea', span: 24 },
    ],
  },
  {
    key: 'ecoRelation',
    title: '原产地关系',
    parentField: 'decListId',
    parentSource: 'goods',
    columns: [
      { title: '单证类型', dataIndex: 'certType', width: 120 },
      { title: '原产地证书编号', dataIndex: 'ecoCertNo', width: 160 },
      { title: '商品ID', dataIndex: 'decListId', width: 90 },
    ],
    formFields: [
      ...compactHeadFields,
      { field: 'certType', label: '单证类型', type: 'select', optionSource: 'cert' },
      { field: 'decGNo', label: '报关单商品序号' },
      { field: 'ecoCertNo', label: '原产地证书编号' },
      { field: 'ecoGNo', label: '原产地证书商品序号' },
      { field: 'extendFiled', label: '扩展字段' },
    ],
  },
  {
    key: 'decControlModel',
    title: '禁限管制要素',
    parentField: 'decListId',
    parentSource: 'goods',
    columns: [
      { title: '要素序号', dataIndex: 'elementNo', width: 110 },
      { title: '要素值', dataIndex: 'elementValue', width: 220, ellipsis: true },
      { title: '申报类别', dataIndex: 'declarationCategoryCode', width: 120 },
    ],
    formFields: [
      ...compactHeadFields,
      { field: 'elementNo', label: '要素序号', required: true },
      { field: 'elementValue', label: '要素值', type: 'textarea', span: 24 },
      { field: 'declarationCategoryCode', label: '申报类别代码', required: true, maxLength: 4 },
    ],
  },
];

function collectOptionSources(fields: CitFieldConfig[]) {
  return fields.map((item) => item.optionSource).filter(Boolean) as ParaOptionSourceKey[];
}

export const singleWindowParaOptionSources = Array.from(
  new Set([
    ...headSections.flatMap((section) => collectOptionSources(section.fields)),
    ...collectOptionSources(goodsFormFields),
    ...relatedTabConfigs.flatMap((tab) => collectOptionSources(tab.formFields)),
  ])
);
