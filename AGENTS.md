# JeecgBoot Project Map

This file is the high-signal project map for coding agents working in this repository. Keep it factual and update it when module boundaries, commands, or runtime assumptions change.

## Overview

JeecgBoot is a full-stack AI low-code platform. This checkout contains:

- `jeecg-boot/`: Java backend, Maven multi-module, Spring Boot 3.5.5, Java 17 by default, project version `3.9.1`.
- `jeecgboot-vue3/`: Vue 3 frontend, Vite 6, TypeScript, Ant Design Vue 4, Pinia, vue-router, UnoCSS, VXE Table, version `3.9.1`.
- `docker-compose.yml`: single-service-style Docker stack for MySQL, Redis, pgvector, backend, frontend.
- `docker-compose-cloud.yml`: cloud/microservice Docker stack with Nacos, Gateway, System, Demo, Sentinel, XXL-Job, frontend.
- `README.md` and `README-AI.md`: product-level documentation, not code-structure docs.

Backend API defaults to `http://localhost:8080/jeecg-boot`. Frontend dev server defaults to port `3100` and proxies `/jeecgboot` to that backend context.

## Repository Shape

```text
.
├── jeecg-boot/                 # backend Maven workspace
│   ├── db/                     # MySQL/Nacos/XXL-Job SQL and DB Dockerfile
│   ├── jeecg-boot-base-core/   # shared backend core, config, common utilities
│   ├── jeecg-module-system/    # system API, system business, monolith start app
│   ├── jeecg-boot-module/      # optional business modules: demo, AIRAG
│   └── jeecg-server-cloud/     # Spring Cloud runtime: gateway, nacos, starts, visual tools
└── jeecgboot-vue3/             # frontend Vite/Vue application
    ├── build/                  # Vite plugins, build scripts, theme variable generation
    ├── electron/               # Electron packaging/runtime support
    ├── mock/                   # mock API data
    ├── src/                    # application source
    ├── tests/                  # Jest mocks/spec and a small test upload server
    └── types/                  # global TypeScript declarations
```

Current rough source scale: backend has about 923 Java files and 110 XML files; frontend `src` has about 769 Vue SFCs and 571 TS/TSX files.

## Backend Map

Root backend POM: `jeecg-boot/pom.xml`.

- Maven coordinates: `org.jeecgframework.boot3:jeecg-boot-parent:3.9.1`.
- Default modules: `jeecg-boot-base-core`, `jeecg-module-system`, `jeecg-boot-module`.
- Profile `SpringCloud` is also `activeByDefault` and adds `jeecg-server-cloud`.
- Environment profiles include `dev` default, `test`, `docker`, `prod`; `application.yml` resolves `spring.profiles.active` from Maven property `profile.name`.
- Root build config skips tests via surefire by default.

Important backend modules:

- `jeecg-boot-base-core`
  - Shared infrastructure and conventions under `org.jeecg.common` and `org.jeecg.config`.
  - Includes common API/result types, aspects, constants, exception handling, security/Shiro, sign/filter/firewall config, MyBatis Plus config, OSS config, utilities.
  - Mapper scan is configured in `org.jeecg.config.mybatis.MybatisPlusSaasConfig` for `org.jeecg.**.mapper*`.

- `jeecg-module-system/jeecg-system-api`
  - Shared system API contracts.
  - `jeecg-system-local-api`: direct in-process API implementation contracts for monolith mode.
  - `jeecg-system-cloud-api`: Feign-facing API layer for cloud mode.

- `jeecg-module-system/jeecg-system-biz`
  - Main system business implementation.
  - Key packages under `org.jeecg.modules`: `system`, `message`, `quartz`, `monitor`, `openapi`, `oss`, `cas`, `api`, `ngalain`.
  - Standard package pattern is `controller`, `entity`, `mapper`, `mapper/xml`, `service`, `service/impl`, plus `vo`, `model`, `util`, `job` as needed.
  - Contains code generation templates in `src/main/resources/jeecg/code-template*`; do not change these unless the task is about generated output.
  - Depends on `jeecg-system-local-api`, JimuReport/JimuBI, and AIRAG. The POM currently declares `jeecg-boot-module-airag` twice; do not tidy unrelated POM details unless asked.

- `jeecg-module-system/jeecg-system-start`
  - Monolith startup module.
  - Main class: `org.jeecg.JeecgSystemApplication`.
  - Default dev config: `src/main/resources/application-dev.yml`, port `8080`, servlet context `/jeecg-boot`, MySQL `127.0.0.1:3306/jeecg-boot`, Redis `127.0.0.1:6379`.
  - Other database profiles exist for DM8, Kingbase, Oracle, PostgreSQL, SQL Server, Docker, test, prod.

- `jeecg-boot-module/jeecg-module-demo`
  - Demo and reference business code under `org.jeecg.modules.demo`.
  - Includes cloud demo, mock/VXE data, shop demo, generated-style CRUD examples, XXL-Job samples.

- `jeecg-boot-module/jeecg-module-custom`
  - Custom business module loaded by `jeecg-module-system/jeecg-system-start`.
  - CIT business code generated from `jeecg-boot/db/其他数据库脚本/达梦/CIT.sql` lives under `org.jeecg.modules.custom.cit`.
  - CIT packages follow `controller`, `entity`, `mapper`, `mapper/xml`, `service`, `service/impl`; generated entity comments preserve SQL table/column comments.
  - REST paths use `/custom/cit/<lowerCamelTableName>`, for example `/custom/cit/decHead/list`.

- `jeecg-boot-module/jeecg-boot-module-airag`
  - AI/RAG feature module under `org.jeecg.modules.airag`.
  - Main areas: `app`, `llm`, `prompts`, `ocr`, `wordtpl`, `demo`.
  - Depends on `jeecg-system-local-api`, `jeecg-aiflow`, LangChain4j, pgvector support, Apache Tika, LiteFlow script engines, POI-TL.
  - Controllers include `AiragAppController`, `AiragChatController`, `AiragKnowledgeController`, `AiragModelController`, `AiragMcpController`, prompt dataset/controllers, OCR, and Word template APIs.

- `jeecg-server-cloud`
  - Cloud mode parent with `jeecg-cloud-gateway`, `jeecg-cloud-nacos`, `jeecg-system-cloud-start`, `jeecg-demo-cloud-start`, and `jeecg-visual`.
  - `jeecg-system-cloud-start` starts system business in cloud mode, depends on `jeecg-boot-starter-cloud` and `jeecg-system-biz`.
  - `jeecg-cloud-gateway` is Spring Cloud Gateway WebFlux with Nacos/Sentinel/Redis rate limiting support.
  - `jeecg-visual` contains monitor, sentinel dashboard, XXL-Job admin, and cloud test examples.

Common backend commands:

```bash
cd jeecg-boot
mvn -pl jeecg-module-system/jeecg-system-start -am spring-boot:run -Pdev
mvn -pl jeecg-module-system/jeecg-system-start -am package -Pdev
mvn test
```

The Maven build is network-dependent on Maven Central, Aliyun, and Jeecg repositories.

## Frontend Map

Frontend details are also documented in `jeecgboot-vue3/CLAUDE.md`. Prefer that file for deep frontend-specific guidance; this section is the root-level summary.

Root frontend config:

- Package manager: pnpm. `package.json` engines allow `^18 || >=20`, but README recommends Node 20+.
- Vite config: `jeecgboot-vue3/vite.config.ts`.
- Path aliases: `/@/` and `@/` map to `src/`; `/#/` and `#/` map to `types/`.
- Dev env: `jeecgboot-vue3/.env` and `.env.development`; `VITE_PORT=3100`, `VITE_GLOB_API_URL=/jeecgboot`, proxy to `http://localhost:8080/jeecg-boot`.

Main frontend boot sequence in `src/main.ts`:

1. Create Vue app.
2. Create router.
3. Install Pinia store.
4. Apply qiankun/main-app props if present.
5. Install i18n.
6. Initialize app config store.
7. Register external Jeecg packages through `registerPackages`.
8. Register global components.
9. Run SSO login handling.
10. Register `src/views/super/**/register.ts` modules.
11. Install router and route guards.
12. Register directives and error handling.
13. Register third-party components.
14. Initialize Electron support.
15. Wait for router readiness and mount.

Important frontend directories:

- `src/api`: shared API modules. `src/api/sys` covers login/user/menu/upload; many feature pages keep their API files beside their views.
- `src/utils/http/axios`: custom Axios wrapper exported as `defHttp`. Requests add timestamp/signature headers, token, tenant ID, and low-code app ID when applicable. Standard backend response is `{ code, result, message, success }`.
- `src/store/modules`: Pinia stores. Key stores are `user.ts`, `permission.ts`, `app.ts`, `locale.ts`, `multipleTab.ts`.
- `src/router`: static routes, dynamic route transform helpers, guards. Permission mode is `PermissionModeEnum.BACK`, so menu/routes/permission codes come from backend via `getBackMenuAndPerms()`.
- `src/layouts`: application shell, header/sidebar/tabs/page layouts.
- `src/components`: shared component library. Jeecg-specific components live in `src/components/jeecg`.
- `src/views/system`: system management pages such as user, role, menu, dict, depart, tenant, announcement, datasource, logs, OSS, message.
- `src/views/super/airag`: AIRAG frontend pages for AI app/chat, knowledge base, MCP, model, prompt datasets/evaluation, OCR, poster, writer, word template.
- `src/views/demo`: demo/reference pages.
- `src/qiankun`: micro-frontend host/child support.
- `src/electron` and top-level `electron/`: Electron runtime and packaging support.

Common frontend feature file pattern:

```text
FeatureName.api.ts       # API functions and URL constants
FeatureName.data.ts      # table columns, form schemas, search schemas
FeatureNameList.vue      # list/page entry
components/*Modal.vue    # create/edit/detail dialogs or drawers
```

For new Vue work, match local style first. In new Vue 3 code, prefer Composition API with `<script setup lang="ts">` unless the surrounding feature uses a different established pattern.

Custom frontend additions:

- `src/views/custom/cit/singleWindow`: China Customs Single Window-style CIT declaration page.
  - Route: `/custom/cit/single-window`; also usable from backend menu component path `custom/cit/singleWindow/index`.
  - Uses the generated backend REST APIs under `/custom/cit/*`.
  - Main files: `cit.api.ts` for endpoint wrappers, `cit.data.ts` for field/table/tab metadata, `composables/useSingleWindowDeclaration.ts` for page state, and split components under `components/`.
  - Layout follows the reference under `/Users/wayne/Downloads/singlewindow` conceptually: fixed dense toolbar, declaration head form, goods detail section, related-data tabs, and a right-side declaration list. It intentionally does not import the old Bootstrap/jQuery assets.

Common frontend commands:

```bash
cd jeecgboot-vue3
pnpm install
pnpm dev
pnpm build
pnpm build:docker
pnpm build:dockercloud
npx jest
```

There is a Jest config and `tests/test.spec.ts`, but no `test` script in `package.json`.

## Runtime And Data

- Default login in docs: `admin / 123456`.
- MySQL scripts: `jeecg-boot/db/jeecgboot-mysql-5.7.sql`.
- Nacos and XXL-Job SQL: `jeecg-boot/db/tables_nacos.sql`, `jeecg-boot/db/tables_xxl_job.sql`.
- AI/RAG pgvector settings are in `application-dev.yml` under `jeecg.ai-rag.embed-store`.
- Docker single stack exposes MySQL on `13306`, backend on `8080`, frontend on `80`, pgvector on `5432`.
- Docker cloud stack exposes gateway on `9999`, Nacos on `8848`, Sentinel on `9000`, XXL-Job on `9080`, frontend on `80`.

## Working Notes

- This is a large upstream-style project. Keep edits narrowly scoped and avoid cleanup churn in unrelated generated/templates/demo code.
- Backend code uses MyBatis Plus conventions; when adding CRUD behavior, follow the nearby `controller/service/mapper/entity/xml` pattern.
- Frontend dynamic routes normally come from backend menus. Adding a page usually needs both backend menu data and a frontend view/API pair unless it is a static route.
- External packages `@jeecg/online` and `@jeecg/aiflow` are dependencies, not source directories in this checkout. They are excluded from Vite `optimizeDeps`.
- Do not replace placeholder secrets such as `??` in checked-in YAML unless the user explicitly asks for environment configuration.
- Top-level `.gitignore` ignores `**/*.lock`, but `jeecgboot-vue3/pnpm-lock.yaml` is present in the checkout. Avoid regenerating lock files unless dependency changes require it.
- Root README and backend/frontend READMEs disagree slightly on the `3.9.1` release date; code versions and package versions are the more useful source for engineering work.
