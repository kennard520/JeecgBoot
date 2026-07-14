export interface DocumentPollingOptions {
  reload: () => Promise<unknown>;
  isVisible: () => boolean;
  delays?: readonly [number, number, number];
}

export interface DocumentPollingController {
  setActive(active: boolean): void;
  handleVisibilityChange(): void;
  stop(): void;
}

export function createDocumentPolling(options: DocumentPollingOptions): DocumentPollingController {
  const delays = options.delays || [3_000, 10_000, 30_000];
  let timer: ReturnType<typeof setTimeout> | undefined;
  let active = false;
  let stopped = false;
  let failureIndex = 0;

  function clearTimer() {
    if (timer !== undefined) {
      clearTimeout(timer);
      timer = undefined;
    }
  }

  function schedule() {
    clearTimer();
    if (stopped || !active || !options.isVisible()) {
      return;
    }
    timer = setTimeout(() => {
      timer = undefined;
      void poll();
    }, delays[failureIndex]);
  }

  async function poll() {
    if (stopped || !active || !options.isVisible()) {
      return;
    }
    try {
      await options.reload();
      failureIndex = 0;
    } catch (_error) {
      failureIndex = Math.min(failureIndex + 1, delays.length - 1);
    } finally {
      schedule();
    }
  }

  return {
    setActive(nextActive: boolean) {
      active = nextActive;
      if (!active) {
        failureIndex = 0;
        clearTimer();
        return;
      }
      schedule();
    },
    handleVisibilityChange() {
      clearTimer();
      schedule();
    },
    stop() {
      stopped = true;
      active = false;
      clearTimer();
    },
  };
}
