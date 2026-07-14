import { Modal } from 'ant-design-vue';
import { defHttp } from '/@/utils/http/axios';
import { buildDocumentUploadData } from './documentWorkflow';

enum Api {
  list = '/custom/task/document/list',
  deleteOne = '/custom/task/document/delete',
  deleteBatch = '/custom/task/document/deleteBatch',
  uploadZip = '/custom/task/document/uploadZip',
  startParse = '/custom/task/document/startParse',
  myAgents = '/custom/ai/agents/mine',
}

export const list = (params) => defHttp.get({ url: Api.list, params });

export const getMyAgents = () => defHttp.get({ url: Api.myAgents });

export const uploadZip = async (file: File, agentCode: string) => {
  const response = await defHttp.uploadFile({ url: Api.uploadZip }, { file, data: buildDocumentUploadData(agentCode) }, { isReturnResponse: true });
  if (!response?.success || response?.code !== 200) {
    throw new Error(response?.message || '上传失败');
  }
  return response.result;
};

export const startParse = (id: string | number) => {
  return defHttp.post({ url: Api.startParse, params: { id } }, { joinParamsToUrl: true });
};

export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

export const batchDelete = (params, handleSuccess) => {
  Modal.confirm({
    title: '确认删除',
    content: '仅未开始的文档允许删除，是否删除选中数据？',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};
