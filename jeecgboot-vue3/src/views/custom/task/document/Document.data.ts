import { BasicColumn, FormSchema } from '/@/components/Table';

export const DOCUMENT_STATUS = {
  NOT_STARTED: 'NOT_STARTED',
  PARSING: 'PARSING',
  COMPLETED: 'COMPLETED',
};

const statusOptions = [
  { label: '未开始', value: DOCUMENT_STATUS.NOT_STARTED },
  { label: '解析中', value: DOCUMENT_STATUS.PARSING },
  { label: '已完成', value: DOCUMENT_STATUS.COMPLETED },
];

export const columns: BasicColumn[] = [
  {
    title: '原始文件名',
    dataIndex: 'originalFilename',
    width: 220,
    ellipsis: true,
  },
  {
    title: '存储文件名',
    dataIndex: 'filename',
    width: 220,
    ellipsis: true,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 90,
    customRender({ text }) {
      return statusOptions.find((item) => item.value === text)?.label || text || '-';
    },
  },
  {
    title: '上传时间',
    dataIndex: 'uploadedAt',
    width: 160,
  },
  {
    title: '开始解析时间',
    dataIndex: 'startedAt',
    width: 160,
  },
  {
    title: '解析完成时间',
    dataIndex: 'finishedAt',
    width: 160,
  },
  {
    title: '解析任务ID',
    dataIndex: 'taskId',
    width: 180,
    ellipsis: true,
  },
  {
    title: '报关单ID',
    dataIndex: 'decHeadId',
    width: 110,
  },
  {
    title: '文件大小',
    dataIndex: 'fileSize',
    width: 100,
    align: 'right',
    customRender({ text }) {
      if (!text && text !== 0) return '-';
      const size = Number(text);
      if (size >= 1024 * 1024) return `${(size / 1024 / 1024).toFixed(2)} MB`;
      if (size >= 1024) return `${(size / 1024).toFixed(2)} KB`;
      return `${size} B`;
    },
  },
  {
    title: '错误信息',
    dataIndex: 'errorMessage',
    width: 220,
    ellipsis: true,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '原始文件名',
    field: 'originalFilename',
    component: 'Input',
  },
  {
    label: '状态',
    field: 'status',
    component: 'Select',
    componentProps: {
      options: statusOptions,
    },
  },
  {
    label: '解析任务ID',
    field: 'taskId',
    component: 'Input',
  },
  {
    label: '报关单ID',
    field: 'decHeadId',
    component: 'Input',
  },
];
