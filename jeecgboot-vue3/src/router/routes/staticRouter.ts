import type { AppRouteRecordRaw } from '/@/router/types';
import { LAYOUT } from '/@/router/constant';

export const AI_ROUTE: AppRouteRecordRaw = {
  path: '',
  name: 'ai-parent',
  component: LAYOUT,
  meta: {
    title: 'ai',
  },
  children: [
    {
      path: '/ai',
      name: 'ai',
      component: () => import('/@/views/dashboard/ai/index.vue'),
      meta: {
        title: 'AI助手',
      },
    },
  ],
};

export const CUSTOM_CIT_ROUTE: AppRouteRecordRaw = {
  path: '/custom/cit',
  name: 'custom-cit-parent',
  component: LAYOUT,
  redirect: '/custom/cit/single-window',
  meta: {
    title: 'custom-cit',
  },
  children: [
    {
      path: 'single-window',
      name: 'CustomCitSingleWindow',
      component: () => import('/@/views/custom/cit/singleWindow/index.vue'),
      meta: {
        title: '中国海关单一窗口',
      },
    },
  ],
};

export const staticRoutesList = [AI_ROUTE, CUSTOM_CIT_ROUTE];
