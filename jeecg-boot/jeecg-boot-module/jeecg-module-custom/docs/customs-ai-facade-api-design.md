# Customs AI facade API design

> Date: 2026-07-01
> Scope: JeecgBoot `jeecg-module-custom`
> Decision: JeecgBoot is the external facade. `customs-ai` is the internal parsing engine.

## 1. Architecture boundary

External customers must not call `customs-ai` directly.

JeecgBoot is responsible for:

- External API authentication and customer identity.
- Upload URL issuance and file metadata management.
- Parse task creation, status query, result query, callback delivery.
- Mapping parse results into CIT business tables such as `DEC_HEAD` and `DEC_LIST`.
- Displaying parsed results in the existing single-window page.

`customs-ai` is responsible for:

- Running the document parsing pipeline.
- Returning normalized declaration data such as `DecHead` and `DecList`.
- Keeping parsing intermediate data, run steps, and AI diagnostics.

Recommended relationship:

```text
Customer system
  -> JeecgBoot external API
  -> object storage upload
  -> JeecgBoot task service
  -> RabbitMQ parse request exchange (routing key = companyCode)
  -> customs-ai worker queue customs.parse.request.<companyCode>
  -> customs-ai pipeline
  -> RabbitMQ parse result exchange
  -> JeecgBoot result consumer imports result into CIT tables
  -> customer polls JeecgBoot or receives callback from JeecgBoot
```

Task creation must be fast and durable. JeecgBoot persists the task as `queued`, sends a RabbitMQ message, and returns the task id immediately. The `customs-ai` worker performs the slower work: download the uploaded file, run the parsing pipeline, and publish a final result message. JeecgBoot then imports `DecHead`/`DecList` and pushes callback if requested.

Final MQ boundary:

- JeecgBoot only publishes parse request messages. It does not consume the long-running parse request queue.
- `customs-ai` workers consume parse request messages. Each worker binds its own queue by `companyCode`, so routing is done by RabbitMQ instead of by workers filtering unrelated company messages.
- `customs-ai` publishes parse result messages after success or final failure.
- JeecgBoot consumes parse result messages, updates `CUSTOM_API_TASK`, imports `DecHead`/`DecList`, and performs the customer callback.

## 2. External customer flow

The target flow uses separated upload and task creation:

```text
1. Login or get API token.
2. Apply for an upload URL.
3. Upload the file to object storage.
4. Confirm upload completion.
5. Create a parse task with fileId and callbackUrl.
6. Query task status.
7. Pull parse result or wait for callback push.
```

`callbackUrl` belongs to the parse task, not to the uploaded file. It must be submitted when creating the task.

## 3. API endpoints exposed by JeecgBoot

### 3.1 Get access token

```text
POST /custom/api/auth/token
```

Request:

```json
{
  "appKey": "client_xxx",
  "appSecret": "secret_xxx"
}
```

Response:

```json
{
  "accessToken": "eyJxxx",
  "tokenType": "Bearer",
  "expiresIn": 7200
}
```

All following customer API requests must carry:

```text
Authorization: Bearer <accessToken>
```

### 3.2 Manage customer apps

Logged-in JeecgBoot operators manage external customer credentials in the Java facade, not in `customs-ai`.

```text
GET    /custom/api/app/list
POST   /custom/api/app/add
PUT    /custom/api/app/edit
POST   /custom/api/app/resetSecret?id={id}
POST   /custom/api/app/clearAccessToken?id={id}
DELETE /custom/api/app/delete?id={id}
DELETE /custom/api/app/deleteBatch?ids={ids}
GET    /custom/api/app/checkAppKey?appKey={appKey}&id={id}
```

Frontend page:

```text
/custom/api/app
component: custom/api/app/index
```

Create and reset operations return the plaintext `appSecret` once. List/detail responses must not expose `app_secret_hash` or `access_token_hash`.

`companyCode` is the RabbitMQ routing key. Use `CUSTOMS` for the common worker; use a dedicated code only when a matching `customs.parse.request.<companyCode>` worker queue exists.

### 3.3 Apply for upload URL

```text
POST /custom/api/files/upload-url
```

Request:

```json
{
  "filename": "declaration-docs.zip",
  "contentType": "application/zip",
  "fileSize": 12345678,
  "sha256": "optional file hash",
  "clientFileId": "optional customer file id"
}
```

Response:

```json
{
  "fileId": "file_abc123",
  "storageType": "cos",
  "objectKey": "uploads/2026/07/file_abc123/declaration-docs.zip",
  "uploadMethod": "PUT",
  "uploadUrl": "https://bucket.cos.ap-shanghai.myqcloud.com/xxx?sign=xxx",
  "headers": {
    "Content-Type": "application/zip"
  },
  "expiresAt": "2026-07-01T10:15:00+08:00"
}
```

### 3.4 Confirm upload completion

```text
POST /custom/api/files/{fileId}/complete
```

Request:

```json
{
  "fileSize": 12345678,
  "sha256": "optional file hash",
  "etag": "optional object storage etag"
}
```

Response:

```json
{
  "fileId": "file_abc123",
  "status": "uploaded",
  "filename": "declaration-docs.zip",
  "fileSize": 12345678
}
```

### 3.5 Create parse task

```text
POST /custom/api/tasks
```

Request:

```json
{
  "fileId": "file_abc123",
  "clientTaskId": "CUS-20260701-001",
  "direction": "import",
  "callbackUrl": "https://client.example.com/api/customs-ai/callback",
  "callbackSecret": "optional callback secret",
  "responseMode": "both",
  "companyCode": "CUSTOMS",
  "metadata": {
    "bizNo": "HT20260701001",
    "operator": "zhangsan",
    "source": "api"
  }
}
```

`companyCode` is the MQ routing code and must match a `customs-ai` worker `AGENT_CODE`, for example `CUSTOMS`, `ILLUMNA-CUSTOMS`, or `ILLUMNA-AGNES`.

Response:

```json
{
  "taskId": "task_xyz789",
  "runNo": 1,
  "fileId": "file_abc123",
  "clientTaskId": "CUS-20260701-001",
  "status": "queued",
  "createdAt": "2026-07-01T10:16:00+08:00"
}
```

### 3.6 Query task status

```text
GET /custom/api/tasks/{taskId}
```

Response:

```json
{
  "taskId": "task_xyz789",
  "clientTaskId": "CUS-20260701-001",
  "status": "running",
  "stage": "extracting",
  "progress": 65,
  "error": null
}
```

Task statuses:

```text
queued
running
succeeded
failed
cancelled
```

### 3.7 Get parse result

```text
GET /custom/api/tasks/{taskId}/result
```

Response:

```json
{
  "taskId": "task_xyz789",
  "clientTaskId": "CUS-20260701-001",
  "status": "succeeded",
  "declareData": {
    "DecHead": {},
    "DecList": []
  },
  "warnings": []
}
```

If the task is not finished, return a business error such as:

```json
{
  "code": "TASK_NOT_FINISHED",
  "message": "Task is not finished",
  "status": "running"
}
```

## 4. Callback pushed by JeecgBoot

If `callbackUrl` is provided when creating the task, JeecgBoot pushes the final result after parsing and importing are complete.

Request:

```text
POST {callbackUrl}
```

Headers:

```text
X-CustomsAI-Task-Id: task_xyz789
X-CustomsAI-Run-No: 1
X-CustomsAI-Signature: sha256=<hmac>
```

Body:

```json
{
  "event": "task.completed",
  "taskId": "task_xyz789",
  "clientTaskId": "CUS-20260701-001",
  "fileId": "file_abc123",
  "runNo": 1,
  "status": "succeeded",
  "declareData": {
    "DecHead": {},
    "DecList": []
  },
  "error": null,
  "finishedAt": "2026-07-01T10:20:00+08:00"
}
```

For failed tasks, use:

```json
{
  "event": "task.failed",
  "taskId": "task_xyz789",
  "clientTaskId": "CUS-20260701-001",
  "fileId": "file_abc123",
  "runNo": 1,
  "status": "failed",
  "declareData": null,
  "error": {
    "code": "PARSE_FAILED",
    "message": "Document parse failed"
  },
  "finishedAt": "2026-07-01T10:20:00+08:00"
}
```

## 5. Storage decision

Use object storage for external uploads.

Recommended first provider: Tencent Cloud COS, because the production server is on Tencent Cloud.

Do not couple business logic to COS. Use a storage abstraction:

```text
ObjectStorageService
  - createUploadUrl(...)
  - statObject(...)
  - openInputStream(...)
  - downloadToLocal(...)
```

The parsing pipeline can still run on local files. JeecgBoot downloads the uploaded object to a local working directory before calling `customs-ai` if needed.

## 6. Suggested data model

### 6.1 External API app

```text
CUSTOM_API_APP
- id
- app_key
- app_secret_hash
- customer_code
- company_code
- enabled
- rate_limit
- access_token_hash
- token_expire_at
- created_at
- updated_at
```

### 6.2 Uploaded file

```text
CUSTOM_API_FILE
- id
- file_id
- customer_code
- client_file_id
- original_filename
- content_type
- file_size
- sha256
- storage_type
- bucket
- object_key
- status: pending/uploaded/expired/deleted
- created_at
- uploaded_at
```

### 6.3 Parse task

```text
CUSTOM_API_TASK
- id
- task_id
- file_id
- customer_code
- client_task_id
- document_id
- dec_head_id
- customs_ai_job_id
- customs_ai_run_no
- direction
- company_code
- callback_url
- callback_secret
- response_mode: polling/callback/both
- status: queued/running/succeeded/failed/cancelled
- stage
- progress
- metadata_json
- result_json
- error_code
- error_message
- callback_status
- callback_error
- created_at
- started_at
- finished_at
```

## 7. Integration with existing JeecgBoot CIT module

Existing relevant code:

- `org.jeecg.modules.custom.task.controller.DocumentController`
- `org.jeecg.modules.custom.task.service.impl.DocumentServiceImpl`
- `org.jeecg.modules.custom.task.entity.Document`
- `org.jeecg.modules.custom.cit.entity.DecHead`
- `org.jeecg.modules.custom.cit.entity.DecList`

Implemented in this module:

1. External API controllers under `org.jeecg.modules.custom.api.controller`.
2. File/task/app entities and mappers for `CUSTOM_API_APP`, `CUSTOM_API_FILE`, and `CUSTOM_API_TASK`.
3. `ObjectStorageService` for upload URL generation and object download.
4. `CustomsAiClient` for internal calls to `customs-ai`.
5. After `customs-ai` succeeds, best-effort mapping of `DecHead` and `DecList` into existing CIT tables.
6. Existing `Document` records are created and updated with `decHeadId`, `taskId`, and parsing status.

Development/local upload fallback:

```text
POST /custom/api/files/{fileId}/content
X-Custom-Upload-Token: <short-lived upload capability>
```

This endpoint is returned as `uploadUrl` when `jeecg.uploadType=local`. Its absolute URL is built from `custom.api.internal-base-url` so reverse proxies may expose a different public prefix from Spring's context path. It is also used when Tencent COS is selected but its secret ID, secret key, region, or bucket is missing, so an incomplete cloud configuration cannot produce an unusable presigned URL. A fully configured cloud/object storage mode returns a presigned `PUT` URL.

Runtime configuration:

```text
custom.api.token-ttl-seconds=7200
custom.api.upload-url-ttl-seconds=900
custom.api.internal-base-url=https://<public-jeecg-host>/<public-prefix>
custom.api.internal-token=<token for customs-ai file download>
```

MQ configuration:

```text
Request exchange: customs.parse.request.exchange
Request routing key: <companyCode>
Worker queue: customs.parse.request.<companyCode>
Result exchange: customs.parse.result.exchange
Result routing key: parse.result
Java result queue: customs.parse.result.java
Producer: org.jeecg.modules.custom.api.mq.CustomApiTaskMqProducer
Result consumer: org.jeecg.modules.custom.api.mq.CustomApiResultMqConsumer
AI request consumer: customs-ai libs.mq.customs_parse_worker.CustomsParseMqWorker
```

Parse request message body:

```json
{
  "taskId": "task_xxx",
  "fileId": "file_xxx",
  "customerCode": "customer_xxx",
  "clientTaskId": "optional-client-task",
  "companyCode": "CUSTOMS",
  "direction": "import",
  "originalFilename": "docs.zip",
  "contentType": "application/zip",
  "fileSize": 12345678,
  "downloadUrl": "http://jeecg/custom/api/internal/files/file_xxx/download",
  "downloadHeaders": {
    "X-Custom-Api-Internal-Token": "******"
  },
  "attemptNo": 1,
  "maxAttempts": 3,
  "metadata": "{}"
}
```

Parse result message body:

```json
{
  "taskId": "task_xxx",
  "companyCode": "CUSTOMS",
  "status": "succeeded",
  "stage": "completed",
  "progress": 100,
  "customsAiJobId": "task_xxx",
  "runNo": 1,
  "result": {
    "DecHead": {},
    "DecList": []
  },
  "finishedAt": "2026-07-01T10:20:00+08:00"
}
```

## 8. Compatibility with old watcher

The old watcher flow uses:

```text
oauth -> uploadByImport -> createImport -> detail
```

The new JeecgBoot facade flow is:

```text
auth/token -> files/upload-url -> object storage PUT -> files/{fileId}/complete -> tasks -> tasks/{taskId} -> tasks/{taskId}/result
```

If old clients cannot change immediately, JeecgBoot can add compatibility endpoints later. The canonical API for new clients should be the facade flow above.
