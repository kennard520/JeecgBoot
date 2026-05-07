import { onMounted, ref } from 'vue';
import { isParaSourceAutoload, queryParaOptions } from '../para.api';
import type { CitSelectOption, ParaOptionLoadingMap, ParaOptionMap, ParaOptionSourceKey } from '../types';

function mergeOptions(current: CitSelectOption[] = [], incoming: CitSelectOption[] = []) {
  const map = new Map<string | number, CitSelectOption>();
  [...current, ...incoming].forEach((item) => {
    map.set(item.value, item);
  });
  return Array.from(map.values());
}

export function useParaOptions(sources: ParaOptionSourceKey[]) {
  const optionMap = ref<ParaOptionMap>({});
  const optionLoadingMap = ref<ParaOptionLoadingMap>({});
  const pendingOptionKeys = new Set<string>();
  const loadedSourceKeys = new Set<ParaOptionSourceKey>();

  function normalizeOptionValue(value: unknown) {
    return value === undefined || value === null ? '' : String(value).trim();
  }

  function hasOption(source: ParaOptionSourceKey, value: string) {
    return Boolean(optionMap.value[source]?.some((item) => normalizeOptionValue(item.value) === value));
  }

  async function loadParaOptions(source: ParaOptionSourceKey, keyword?: string) {
    if (!keyword && loadedSourceKeys.has(source)) return;
    optionLoadingMap.value = { ...optionLoadingMap.value, [source]: true };
    try {
      const options = await queryParaOptions(source, keyword);
      optionMap.value = {
        ...optionMap.value,
        [source]: mergeOptions(optionMap.value[source], options),
      };
      if (!keyword) {
        loadedSourceKeys.add(source);
      }
    } finally {
      optionLoadingMap.value = { ...optionLoadingMap.value, [source]: false };
    }
  }

  async function ensureParaOption(source: ParaOptionSourceKey, value: unknown) {
    const text = normalizeOptionValue(value);
    if (!text || hasOption(source, text)) return;
    const key = `${source}:${text}`;
    if (pendingOptionKeys.has(key)) return;
    pendingOptionKeys.add(key);
    try {
      const options = await queryParaOptions(source, text);
      optionMap.value = {
        ...optionMap.value,
        [source]: mergeOptions(optionMap.value[source], options),
      };
    } finally {
      pendingOptionKeys.delete(key);
    }
  }

  async function loadInitialOptions() {
    const uniqueSources = Array.from(new Set(sources)).filter(isParaSourceAutoload);
    await Promise.all(uniqueSources.map((source) => loadParaOptions(source)));
  }

  onMounted(() => {
    loadInitialOptions();
  });

  return {
    optionMap,
    optionLoadingMap,
    loadParaOptions,
    ensureParaOption,
  };
}
