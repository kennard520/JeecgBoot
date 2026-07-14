/** @jest-environment node */

import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';

describe('frontend deployment workflow', () => {
  const workflow = readFileSync(resolve(process.cwd(), '../.github/workflows/deploy.yml'), 'utf8');

  it('triggers when the Vue application changes', () => {
    expect(workflow).toContain("- 'jeecgboot-vue3/**'");
  });

  it('builds and uploads the production frontend artifact', () => {
    expect(workflow).toContain('pnpm build');
    expect(workflow).toContain('frontend-dist.tar.gz');
  });

  it('deploys the artifact to the current Nginx document root with validation and reload', () => {
    expect(workflow).toContain('/var/wwwroot/cit/dist');
    expect(workflow).toContain('nginx -t');
    expect(workflow).toContain('systemctl reload nginx');
  });

  it('checks the served index before success and rolls back frontend and backend on failure', () => {
    const healthCheck = "curl -fkSs https://127.0.0.1/ -H 'Host: smart-entry.citclub.org'";
    expect(workflow).toContain(healthCheck);
    expect(workflow).toContain('grep -q \'id="app"\'');
    expect(workflow.indexOf(healthCheck)).toBeLessThan(workflow.indexOf('echo "Deploy success"'));

    const rollbackFunction = workflow.slice(workflow.indexOf('rollback_release()'), workflow.indexOf('rm -rf "$FRONTEND_STAGE"'));
    expect(rollbackFunction).toContain('mv "$FRONTEND_BACKUP" "$FRONTEND_ROOT"');
    expect(rollbackFunction).toContain('rollback_backend');
    const backendRollbackFunction = workflow.slice(workflow.indexOf('rollback_backend()'), workflow.indexOf('rollback_release()'));
    expect(backendRollbackFunction).toContain('run_container "$OLD_IMAGE"');

    const failureBranch = workflow.slice(workflow.indexOf('Frontend health check failed'), workflow.indexOf('echo "Deploy success"'));
    expect(failureBranch).toContain('rollback_release');

    const invalidArtifactBranch = workflow.slice(
      workflow.indexOf('Frontend artifact extraction failed'),
      workflow.indexOf('if [ -e "$FRONTEND_ROOT"')
    );
    expect(invalidArtifactBranch).toContain('rollback_backend');
    expect(invalidArtifactBranch).toContain('Frontend artifact is missing index.html');
  });
});
