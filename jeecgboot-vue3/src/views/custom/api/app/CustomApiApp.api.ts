import { Modal } from 'ant-design-vue';
import { defHttp } from '/@/utils/http/axios';

enum Api {
  list = '/custom/api/app/list',
  save = '/custom/api/app/add',
  edit = '/custom/api/app/edit',
  deleteOne = '/custom/api/app/delete',
  deleteBatch = '/custom/api/app/deleteBatch',
  resetSecret = '/custom/api/app/resetSecret',
  clearAccessToken = '/custom/api/app/clearAccessToken',
  checkAppKey = '/custom/api/app/checkAppKey',
}

export const list = (params) => defHttp.get({ url: Api.list, params });

export const saveOrUpdate = (params, isUpdate) => {
  const url = isUpdate ? Api.edit : Api.save;
  return isUpdate ? defHttp.put({ url, data: params }) : defHttp.post({ url, data: params });
};

export const resetSecret = (id: string | number) => {
  return defHttp.post({ url: Api.resetSecret, params: { id } }, { joinParamsToUrl: true });
};

export const clearAccessToken = (id: string | number) => {
  return defHttp.post({ url: Api.clearAccessToken, params: { id } }, { joinParamsToUrl: true });
};

export const checkAppKey = (params) => defHttp.get({ url: Api.checkAppKey, params });

export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

export const batchDelete = (params, handleSuccess) => {
  Modal.confirm({
    title: '确认删除',
    content: '是否删除选中的 API 客户配置？',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};
