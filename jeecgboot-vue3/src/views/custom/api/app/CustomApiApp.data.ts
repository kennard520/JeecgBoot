import { BasicColumn, FormSchema } from '/@/components/Table';
import { checkAppKey } from './CustomApiApp.api';

const enabledOptions = [
  { label: '启用', value: '1' },
  { label: '停用', value: '0' },
];

export const columns: BasicColumn[] = [
  {
    title: 'AppKey',
    dataIndex: 'appKey',
    width: 180,
    ellipsis: true,
  },
  {
    title: '客户编码',
    dataIndex: 'customerCode',
    width: 120,
  },
  {
    title: '解析路由',
    dataIndex: 'companyCode',
    width: 120,
  },
  {
    title: '状态',
    dataIndex: 'enabled',
    width: 80,
    customRender({ text }) {
      return `${text}` === '1' ? '启用' : '停用';
    },
  },
  {
    title: '限流',
    dataIndex: 'rateLimit',
    width: 80,
    customRender({ text }) {
      return text ?? '-';
    },
  },
  {
    title: '已有Token',
    dataIndex: 'hasAccessToken',
    width: 90,
    customRender({ text }) {
      return text ? '是' : '否';
    },
  },
  {
    title: 'Token过期时间',
    dataIndex: 'tokenExpireAt',
    width: 160,
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    width: 160,
  },
  {
    title: '更新时间',
    dataIndex: 'updatedAt',
    width: 160,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: 'AppKey',
    field: 'appKey',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '客户编码',
    field: 'customerCode',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '解析路由',
    field: 'companyCode',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '状态',
    field: 'enabled',
    component: 'Select',
    componentProps: {
      options: enabledOptions,
    },
    colProps: { span: 6 },
  },
];

export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: '',
    component: 'Input',
    show: false,
  },
  {
    field: 'appKey',
    label: 'AppKey',
    component: 'Input',
    required: true,
    dynamicDisabled: ({ values }) => !!values.id,
    dynamicRules: ({ values, model }) => {
      return [
        {
          required: true,
          validator: async (_, value) => {
            if (!value) {
              return Promise.reject('请输入 AppKey');
            }
            const res = await checkAppKey({ id: model.id, appKey: value });
            if (res?.exists) {
              return Promise.reject('AppKey 已存在');
            }
            return Promise.resolve();
          },
        },
      ];
    },
  },
  {
    field: 'customerCode',
    label: '客户编码',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '例如 YINMEINA、GENERAL',
    },
  },
  {
    field: 'companyCode',
    label: '解析路由',
    component: 'Input',
    required: true,
    defaultValue: 'CUSTOMS',
    helpMessage: 'RabbitMQ 路由键。通用解析填 CUSTOMS，独立 worker 填对应 companyCode。',
  },
  {
    field: 'rateLimit',
    label: '限流',
    component: 'InputNumber',
    defaultValue: 60,
    componentProps: {
      min: 0,
      precision: 0,
    },
  },
  {
    field: 'enabled',
    label: '是否启用',
    component: 'JSwitch',
    defaultValue: '1',
    componentProps: {
      options: ['1', '0'],
      labelOptions: ['启用', '停用'],
    },
  },
];
