import { Modal } from 'ant-design-vue';
import { defHttp } from '/@/utils/http/axios';
import type { CitEntityKey, CitPage, CitRecord } from './types';

const CIT_BASE_URL = '/custom/cit';

export const citApiPathMap: Record<CitEntityKey, string> = {
  citAttributes: `${CIT_BASE_URL}/citAttributes`,
  decHead: `${CIT_BASE_URL}/decHead`,
  decList: `${CIT_BASE_URL}/decList`,
  decContainer: `${CIT_BASE_URL}/decContainer`,
  decLicenseDocus: `${CIT_BASE_URL}/decLicenseDocus`,
  decRequestCert: `${CIT_BASE_URL}/decRequestCert`,
  decGoodsLimit: `${CIT_BASE_URL}/decGoodsLimit`,
  decGoodsLimitVin: `${CIT_BASE_URL}/decGoodsLimitVin`,
  decOtherPack: `${CIT_BASE_URL}/decOtherPack`,
  decCopLimit: `${CIT_BASE_URL}/decCopLimit`,
  decUser: `${CIT_BASE_URL}/decUser`,
  decMarkLob: `${CIT_BASE_URL}/decMarkLob`,
  decFreeTxt: `${CIT_BASE_URL}/decFreeTxt`,
  decSign: `${CIT_BASE_URL}/decSign`,
  trnHead: `${CIT_BASE_URL}/trnHead`,
  trnList: `${CIT_BASE_URL}/trnList`,
  trnContainer: `${CIT_BASE_URL}/trnContainer`,
  trnContaGoodsList: `${CIT_BASE_URL}/trnContaGoodsList`,
  decSupplement: `${CIT_BASE_URL}/decSupplement`,
  edocRelation: `${CIT_BASE_URL}/edocRelation`,
  ecoRelation: `${CIT_BASE_URL}/ecoRelation`,
  sddTax: `${CIT_BASE_URL}/sddTax`,
  decRisk: `${CIT_BASE_URL}/decRisk`,
  decCopPromise: `${CIT_BASE_URL}/decCopPromise`,
  decRoyaltyFee: `${CIT_BASE_URL}/decRoyaltyFee`,
  decTpAccess: `${CIT_BASE_URL}/decTpAccess`,
  decControlModel: `${CIT_BASE_URL}/decControlModel`,
};

export const getDecHeadExportUrl = `${citApiPathMap.decHead}/exportXls`;
export const getDecHeadImportUrl = `${citApiPathMap.decHead}/importExcel`;

export function getCitExportUrl(entityKey: CitEntityKey) {
  return `${citApiPathMap[entityKey]}/exportXls`;
}

export function getCitImportUrl(entityKey: CitEntityKey) {
  return `${citApiPathMap[entityKey]}/importExcel`;
}

export function queryCitPage<T extends CitRecord = CitRecord>(entityKey: CitEntityKey, params?: CitRecord) {
  return defHttp.get<CitPage<T>>({ url: `${citApiPathMap[entityKey]}/list`, params });
}

const citRecordKeyAliasMap: Record<string, string> = {
  GNo: 'gNo',
  gno: 'gNo',
  GName: 'gName',
  gname: 'gName',
  GModel: 'gModel',
  gmodel: 'gModel',
  GQty: 'gQty',
  gqty: 'gQty',
  GUnit: 'gUnit',
  gunit: 'gUnit',
};

function normalizeCitRecord<T extends CitRecord = CitRecord>(record: T): T {
  const result = { ...record };
  Object.entries(citRecordKeyAliasMap).forEach(([sourceKey, targetKey]) => {
    if (result[targetKey] === undefined && result[sourceKey] !== undefined) {
      result[targetKey] = result[sourceKey];
    }
  });
  return result;
}

export async function queryCitRecords<T extends CitRecord = CitRecord>(entityKey: CitEntityKey, params?: CitRecord) {
  const page = await queryCitPage<T>(entityKey, { pageNo: 1, pageSize: 200, ...params });
  const records = Array.isArray(page) ? page : page?.records || [];
  return records.map((item) => normalizeCitRecord<T>(item));
}

export function queryCitById<T extends CitRecord = CitRecord>(entityKey: CitEntityKey, id: string | number) {
  return defHttp.get<T>({ url: `${citApiPathMap[entityKey]}/queryById`, params: { id } }).then((record) => normalizeCitRecord<T>(record));
}

export function saveCitEntity<T extends CitRecord = CitRecord>(entityKey: CitEntityKey, params: T, isUpdate: boolean) {
  const url = `${citApiPathMap[entityKey]}/${isUpdate ? 'edit' : 'add'}`;
  return isUpdate ? defHttp.put({ url, params }) : defHttp.post({ url, params });
}

export function deleteCitEntity(entityKey: CitEntityKey, id: string | number, handleSuccess?: () => void) {
  return defHttp.delete({ url: `${citApiPathMap[entityKey]}/delete`, data: { id } }, { joinParamsToUrl: true }).then(() => {
    handleSuccess?.();
  });
}

export function batchDeleteCitEntity(entityKey: CitEntityKey, ids: Array<string | number>, handleSuccess?: () => void) {
  Modal.confirm({
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp
        .delete({ url: `${citApiPathMap[entityKey]}/deleteBatch`, data: { ids: ids.join(',') } }, { joinParamsToUrl: true })
        .then(() => {
          handleSuccess?.();
        });
    },
  });
}
