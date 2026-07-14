/** @jest-environment node */

import { createDocumentPolling } from '../../../src/views/custom/task/document/documentPolling';

describe('document polling', () => {
  beforeEach(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it('reloads active rows every three seconds and stops when no row is active', async () => {
    const reload = jest.fn().mockResolvedValue(undefined);
    const polling = createDocumentPolling({ reload, isVisible: () => true });

    polling.setActive(true);
    await jest.advanceTimersByTimeAsync(2_999);
    expect(reload).not.toHaveBeenCalled();
    await jest.advanceTimersByTimeAsync(1);
    expect(reload).toHaveBeenCalledTimes(1);
    await jest.advanceTimersByTimeAsync(3_000);
    expect(reload).toHaveBeenCalledTimes(2);

    polling.setActive(false);
    expect(jest.getTimerCount()).toBe(0);
    await jest.advanceTimersByTimeAsync(30_000);
    expect(reload).toHaveBeenCalledTimes(2);
  });

  it('pauses while the document is hidden and resumes when it becomes visible', async () => {
    let visible = true;
    const reload = jest.fn().mockResolvedValue(undefined);
    const polling = createDocumentPolling({ reload, isVisible: () => visible });

    polling.setActive(true);
    visible = false;
    polling.handleVisibilityChange();
    expect(jest.getTimerCount()).toBe(0);
    await jest.advanceTimersByTimeAsync(30_000);
    expect(reload).not.toHaveBeenCalled();

    visible = true;
    polling.handleVisibilityChange();
    await jest.advanceTimersByTimeAsync(3_000);
    expect(reload).toHaveBeenCalledTimes(1);
  });

  it('backs off failed reloads from 3 to 10 to 30 seconds and resets after success', async () => {
    const reload = jest.fn().mockRejectedValueOnce(new Error('network 1')).mockRejectedValueOnce(new Error('network 2')).mockResolvedValue(undefined);
    const polling = createDocumentPolling({ reload, isVisible: () => true });

    polling.setActive(true);
    await jest.advanceTimersByTimeAsync(3_000);
    expect(reload).toHaveBeenCalledTimes(1);
    await jest.advanceTimersByTimeAsync(9_999);
    expect(reload).toHaveBeenCalledTimes(1);
    await jest.advanceTimersByTimeAsync(1);
    expect(reload).toHaveBeenCalledTimes(2);
    await jest.advanceTimersByTimeAsync(29_999);
    expect(reload).toHaveBeenCalledTimes(2);
    await jest.advanceTimersByTimeAsync(1);
    expect(reload).toHaveBeenCalledTimes(3);
    await jest.advanceTimersByTimeAsync(3_000);
    expect(reload).toHaveBeenCalledTimes(4);
  });

  it('clears its timer permanently when the page unmounts', async () => {
    const reload = jest.fn().mockResolvedValue(undefined);
    const polling = createDocumentPolling({ reload, isVisible: () => true });

    polling.setActive(true);
    polling.stop();
    expect(jest.getTimerCount()).toBe(0);
    await jest.advanceTimersByTimeAsync(30_000);
    expect(reload).not.toHaveBeenCalled();
  });
});
