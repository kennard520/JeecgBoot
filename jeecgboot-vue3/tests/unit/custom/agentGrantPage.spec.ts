/** @jest-environment node */

import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';

describe('agent grant admin page wiring', () => {
  const source = readFileSync(resolve(process.cwd(), 'src/views/custom/ai/grant/index.vue'), 'utf8');
  const routes = readFileSync(resolve(process.cwd(), 'src/router/routes/staticRouter.ts'), 'utf8');

  it('uses one table confirmation and disables server pagination for the list response', () => {
    expect(source).toContain('popConfirm:');
    expect(source).not.toContain('import { Modal }');
    expect(source).toMatch(/pagination:\s*false/);
  });

  it('does not bypass backend menu permissions through a global static route', () => {
    expect(routes).not.toContain("path: 'ai/grant'");
    expect(routes).not.toContain("name: 'CustomAiAgentGrant'");
  });
});
