package org.jeecg.modules.custom.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.callback.CallbackSecretCipher;
import org.jeecg.modules.custom.api.callback.CallbackUrlPolicy;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomMqInbox;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.service.ICustomCallbackDeliveryService;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.service.ICustomMqOutboxService;
import org.jeecg.modules.custom.api.service.ICustomApiTaskService;
import org.jeecg.modules.custom.api.service.ICustomMqInboxService;
import org.jeecg.modules.custom.api.service.CustomApiIdempotencyService;
import org.jeecg.modules.custom.api.util.CanonicalRequestHasher;
import org.jeecg.modules.custom.api.util.CustomApiIds;
import org.jeecg.modules.custom.api.vo.TaskCreateRequest;
import org.jeecg.modules.custom.api.vo.TaskResponse;
import org.jeecg.modules.custom.api.vo.TaskResultResponse;
import org.jeecg.modules.custom.cit.entity.DecHead;
import org.jeecg.modules.custom.cit.entity.DecList;
import org.jeecg.modules.custom.cit.service.IDecHeadService;
import org.jeecg.modules.custom.cit.service.IDecListService;
import org.jeecg.modules.custom.task.entity.Document;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.jeecg.modules.custom.ai.service.CustomAgentAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class CustomApiTaskServiceImpl extends ServiceImpl<CustomApiTaskMapper, CustomApiTask> implements ICustomApiTaskService {
    private static final int ERROR_CODE_MAX_LENGTH = 100;
    private static final int ERROR_MESSAGE_MAX_LENGTH = 1000;
    private final Map<Class<?>, Map<String, Integer>> columnSizeCache = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(customApiImportModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .build();
    @Autowired
    private CustomApiTaskMapper customApiTaskMapper;
    @Autowired
    private ICustomApiFileService fileService;
    @Autowired
    private ICustomMqOutboxService outboxService;
    @Autowired
    private ICustomMqInboxService inboxService;
    @Autowired
    private ICustomCallbackDeliveryService callbackDeliveryService;
    @Autowired
    private IDocumentService documentService;
    @Autowired
    private IDecHeadService decHeadService;
    @Autowired
    private IDecListService decListService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private CustomAgentAccessService agentAccessService;
    @Autowired
    private CustomApiIdempotencyService idempotencyService;
    @Autowired
    private CanonicalRequestHasher requestHasher;
    @Autowired
    private CallbackUrlPolicy callbackUrlPolicy;
    @Autowired
    private CallbackSecretCipher callbackSecretCipher;

    @Override
    public TaskResponse createTask(CustomApiApp app, TaskCreateRequest request) {
        String idempotencyKey = request == null ? null : request.getIdempotencyKey();
        return createTask(app, request, idempotencyKey);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public TaskResponse createTask(CustomApiApp app, TaskCreateRequest request, String headerIdempotencyKey) {
        if (request == null || isBlank(request.getFileId())) {
            throw new JeecgBootException("fileId is required");
        }
        if (app == null || app.getId() == null) {
            throw new JeecgBootException("authenticated API app is required");
        }
        String agentCode = agentAccessService.requireApiAgent(app, request.getCompanyCode())
                .trim().toUpperCase(Locale.ROOT);
        String idempotencyKey = firstNonBlank(headerIdempotencyKey, request.getIdempotencyKey());
        String requestHash = requestHasher.hashTask(request, agentCode);
        CustomApiTask existing = idempotencyService.findTask(
                app.getId(), request.getClientTaskId(), idempotencyKey, requestHash);
        if (existing != null) {
            return toResponse(existing);
        }
        CustomApiFile file = fileService.requireUploadedFile(app, request.getFileId());

        String direction = isBlank(request.getDirection()) ? "import" : request.getDirection();
        if (!"import".equals(direction) && !"export".equals(direction)) {
            throw new JeecgBootException("direction must be import or export");
        }
        String responseMode = isBlank(request.getResponseMode()) ? "polling" : request.getResponseMode();
        if (!List.of("polling", "callback", "both").contains(responseMode)) {
            throw new JeecgBootException("responseMode must be polling, callback or both");
        }
        CallbackSecretCipher.EncryptedSecret encryptedSecret = null;
        if (!"polling".equals(responseMode)) {
            if (isBlank(request.getCallbackUrl()) || isBlank(request.getCallbackSecret())) {
                throw new JeecgBootException("callbackUrl and callbackSecret are required for callback mode");
            }
            callbackUrlPolicy.validate(request.getCallbackUrl());
            encryptedSecret = callbackSecretCipher.encrypt(request.getCallbackSecret());
        }

        String taskId = CustomApiIds.taskId();
        LocalDateTime now = LocalDateTime.now();
        Document document = new Document();
        document.markUploaded(file.getOriginalFilename(), storagePath(file), file.getStorageType(), file.getFileSize(), file.getContentType());
        document.markParseStarted(taskId);
        documentService.save(document);

        CustomApiTask task = new CustomApiTask()
                .setAppId(app.getId())
                .setTaskId(taskId)
                .setFileId(file.getFileId())
                .setCustomerCode(app.getCustomerCode())
                .setClientTaskId(request.getClientTaskId())
                .setIdempotencyKey(isBlank(idempotencyKey) ? null : idempotencyKey.trim())
                .setRequestHash(requestHash)
                .setDocumentId(document.getId())
                .setDirection(direction)
                .setCompanyCode(agentCode)
                .setCallbackUrl(request.getCallbackUrl())
                .setCallbackSecret(null)
                .setCallbackSecretCiphertext(encryptedSecret == null ? null : encryptedSecret.ciphertext())
                .setCallbackSecretKeyVersion(encryptedSecret == null ? null : encryptedSecret.keyVersion())
                .setResponseMode(responseMode)
                .setStatus(CustomApiTask.STATUS_QUEUED)
                .setStage("queued")
                .setProgress(0)
                .setQueuedAt(now)
                .setCustomsAiRunNo(1)
                .setVersion(0)
                .setMetadataJson(request.getMetadata() == null ? null : JSON.toJSONString(request.getMetadata()))
                .setCreatedAt(now);
        save(task);

        outboxService.enqueueParseTask(task, file, 1);
        return toResponse(task);
    }

    @Override
    public TaskResponse getTask(CustomApiApp app, String taskId) {
        return toResponse(requireOwnedTask(app, taskId));
    }

    @Override
    public TaskResultResponse getResult(CustomApiApp app, String taskId) {
        CustomApiTask task = requireOwnedTask(app, taskId);
        if (!CustomApiTask.STATUS_SUCCEEDED.equals(task.getStatus())) {
            throw new JeecgBootException("task is not finished, current status: " + task.getStatus());
        }
        Object declareData = isBlank(task.getResultJson()) ? null : JSON.parse(task.getResultJson());
        return new TaskResultResponse()
                .setTaskId(task.getTaskId())
                .setClientTaskId(task.getClientTaskId())
                .setStatus(task.getStatus())
                .setDeclareData(declareData)
                .setWarnings(new ArrayList<>());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleParseResult(Map<String, Object> message) {
        validateResultMessage(message);
        String eventId = stringValue(message, "eventId");
        String taskId = stringValue(message, "taskId");
        Integer runNo = intValue(message, "runNo");
        CustomMqInbox inbox = inboxService.receive(eventId, taskId, runNo,
                stringValue(message, "eventType"), payloadHash(message));
        if (inbox == null) {
            return;
        }

        CustomApiTask task = customApiTaskMapper.selectByTaskIdForUpdate(taskId);
        if (task == null) {
            throw new JeecgBootException("task not found: " + taskId);
        }
        validateResultOwnership(task, message);
        int currentRun = task.getCustomsAiRunNo() == null ? 1 : task.getCustomsAiRunNo();
        if (runNo < currentRun) {
            inboxService.markIgnored(inbox, "stale run " + runNo + ", current run is " + currentRun);
            return;
        }
        if (runNo > currentRun) {
            throw new JeecgBootException("result run " + runNo + " is ahead of task run " + currentRun);
        }
        if (isTerminal(task.getStatus())) {
            inboxService.markIgnored(inbox, "task is already terminal: " + task.getStatus());
            return;
        }

        String status = stringValue(message, "status");
        task.setCustomsAiJobId(stringValue(message, "customsAiJobId", task.getCustomsAiJobId()));
        if ("running".equals(status)) {
            updateRunning(task, stringValue(message, "stage"), intValue(message, "progress"));
            documentService.updateParseHeartbeat(taskId, task.getStage(), task.getProgress(),
                    task.getLastHeartbeatAt());
            inboxService.markProcessed(inbox);
            return;
        }
        if ("succeeded".equals(status)) {
            Object result = message.get("result");
            completeTask(task, result);
            callbackDeliveryService.enqueueTerminal(task, "task.completed", result, null, null);
            inboxService.markProcessed(inbox);
            return;
        }
        if ("failed".equals(status)) {
            String errorCode = stringValue(message, "errorCode");
            String errorMessage = stringValue(message, "errorMessage");
            failTask(task, errorCode, errorMessage);
            callbackDeliveryService.enqueueTerminal(
                    task, "task.failed", null, errorCode, errorMessage);
            inboxService.markProcessed(inbox);
            return;
        }
        throw new JeecgBootException("unsupported parse result status: " + status);
    }

    private void completeTask(CustomApiTask task, Object result) {
        Long decHeadId = importDeclaration(result, task);
        task.setDecHeadId(decHeadId);
        task.setResultJson(JSON.toJSONString(result));
        task.setStatus(CustomApiTask.STATUS_SUCCEEDED);
        task.setStage("completed");
        task.setProgress(100);
        task.setFinishedAt(LocalDateTime.now());
        if (shouldCallback(task)) {
            task.setCallbackStatus("pending").setCallbackError(null);
        }
        customApiTaskMapper.updateById(task);
        documentService.completeParse(task.getTaskId(), decHeadId);
    }

    private Long importDeclaration(Object result, CustomApiTask task) {
        JSONObject json = (JSONObject) JSON.toJSON(result);
        Object headObj = json.get("DecHead");
        if (headObj == null) {
            throw new JeecgBootException("DecHead is required when parse succeeded");
        }
        DecHead head = mapper.convertValue(headObj, DecHead.class);
        head.setId(null);
        head.setSourceTaskId(task.getTaskId());
        head.setCustomerCode(task.getCustomerCode());
        head.setSourceType(task.getAppId() == null ? "WEB" : "CUSTOM_API");
        fillRequiredHeadDefaults(head);
        trimStringColumns(head);
        if (!decHeadService.save(head) || head.getId() == null) {
            throw new JeecgBootException("save DecHead failed");
        }

        Object listObj = json.get("DecList");
        if (listObj instanceof List<?>) {
            List<DecList> goods = new ArrayList<>();
            for (Object item : (List<?>) listObj) {
                DecList decList = mapper.convertValue(item, DecList.class);
                decList.setId(null);
                decList.setDecHeadId(head.getId());
                fillRequiredListDefaults(decList);
                trimStringColumns(decList);
                goods.add(decList);
            }
            if (!goods.isEmpty() && !decListService.saveBatch(goods)) {
                throw new JeecgBootException("save DecList failed");
            }
        }
        return head.getId();
    }

    private void updateRunning(CustomApiTask task, String stage, Integer progress) {
        LocalDateTime heartbeat = LocalDateTime.now();
        task.setStatus(CustomApiTask.STATUS_RUNNING);
        task.setStage(stage);
        task.setProgress(progress);
        task.setLastHeartbeatAt(heartbeat);
        if (task.getStartedAt() == null) {
            task.setStartedAt(heartbeat);
        }
        task.setFinishedAt(null).setErrorCode(null).setErrorMessage(null);
        customApiTaskMapper.updateById(task);
    }

    private void failTask(CustomApiTask task, String errorCode, String errorMessage) {
        task.setStatus(CustomApiTask.STATUS_FAILED);
        task.setStage("failed");
        task.setProgress(100);
        task.setErrorCode(truncate(errorCode, ERROR_CODE_MAX_LENGTH));
        task.setErrorMessage(truncate(errorMessage, ERROR_MESSAGE_MAX_LENGTH));
        task.setFinishedAt(LocalDateTime.now());
        if (shouldCallback(task)) {
            task.setCallbackStatus("pending").setCallbackError(null);
        }
        customApiTaskMapper.updateById(task);
        documentService.failParse(task.getTaskId(), errorMessage);
    }

    private void validateResultMessage(Map<String, Object> message) {
        if (message == null) {
            throw new JeecgBootException("parse result message is required");
        }
        requireText(message, "eventId");
        requireText(message, "taskId");
        requireText(message, "customerCode");
        requireText(message, "agentCode");
        requireText(message, "stage");
        requireText(message, "occurredAt");
        if (!"parse.status".equals(requireText(message, "eventType"))) {
            throw new JeecgBootException("eventType must be parse.status");
        }
        if (!Integer.valueOf(2).equals(intValue(message, "schemaVersion"))) {
            throw new JeecgBootException("schemaVersion must be 2");
        }
        requirePositive(message, "runNo");
        requirePositive(message, "attemptNo");
        Integer progress = intValue(message, "progress");
        if (progress == null || progress < 0 || progress > 100) {
            throw new JeecgBootException("progress must be between 0 and 100");
        }
        String status = requireText(message, "status");
        if (!List.of("running", "succeeded", "failed").contains(status)) {
            throw new JeecgBootException("unsupported parse result status: " + status);
        }
        if ("succeeded".equals(status) && message.get("result") == null) {
            throw new JeecgBootException("result is required when parse succeeded");
        }
        if ("failed".equals(status)) {
            requireText(message, "errorCode");
            requireText(message, "errorMessage");
        }
        try {
            OffsetDateTime.parse(stringValue(message, "occurredAt"));
        } catch (Exception invalidTime) {
            throw new JeecgBootException("occurredAt must be an ISO-8601 timestamp");
        }
    }

    private void validateResultOwnership(CustomApiTask task, Map<String, Object> message) {
        if (!task.getCustomerCode().equals(stringValue(message, "customerCode"))) {
            throw new JeecgBootException("result customerCode does not match task");
        }
        if (!task.getCompanyCode().equals(stringValue(message, "agentCode"))) {
            throw new JeecgBootException("result agentCode does not match task");
        }
    }

    private String requireText(Map<String, Object> message, String key) {
        String value = stringValue(message, key);
        if (isBlank(value)) {
            throw new JeecgBootException(key + " is required in parse result message");
        }
        return value;
    }

    private int requirePositive(Map<String, Object> message, String key) {
        Integer value = intValue(message, key);
        if (value == null || value < 1) {
            throw new JeecgBootException(key + " must be positive");
        }
        return value;
    }

    private String payloadHash(Map<String, Object> message) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(mapper.writeValueAsBytes(message)));
        } catch (Exception e) {
            throw new JeecgBootException("hash parse result failed: " + e.getMessage());
        }
    }

    private boolean isTerminal(String status) {
        return List.of(CustomApiTask.STATUS_SUCCEEDED, CustomApiTask.STATUS_FAILED,
                CustomApiTask.STATUS_CANCELLED, CustomApiTask.STATUS_TIMEOUT).contains(status);
    }

    private boolean shouldCallback(CustomApiTask task) {
        return !isBlank(task.getCallbackUrl()) && !"polling".equals(task.getResponseMode());
    }

    private CustomApiTask requireOwnedTask(CustomApiApp app, String taskId) {
        if (isBlank(taskId)) {
            throw new JeecgBootException("taskId is required");
        }
        CustomApiTask task = getOne(new LambdaQueryWrapper<CustomApiTask>()
                .eq(CustomApiTask::getTaskId, taskId)
                .eq(CustomApiTask::getAppId, app.getId())
                .eq(CustomApiTask::getCustomerCode, app.getCustomerCode()), false);
        if (task == null) {
            throw new JeecgBootException("task not found");
        }
        return task;
    }

    private TaskResponse toResponse(CustomApiTask task) {
        Map<String, String> links = new LinkedHashMap<>();
        links.put("self", "/custom/api/tasks/" + task.getTaskId());
        links.put("result", "/custom/api/tasks/" + task.getTaskId() + "/result");
        return new TaskResponse()
                .setTaskId(task.getTaskId())
                .setRunNo(task.getCustomsAiRunNo())
                .setFileId(task.getFileId())
                .setClientTaskId(task.getClientTaskId())
                .setStatus(task.getStatus())
                .setStage(task.getStage())
                .setProgress(task.getProgress())
                .setError(task.getErrorMessage())
                .setCreatedAt(task.getCreatedAt())
                .setStartedAt(task.getStartedAt())
                .setFinishedAt(task.getFinishedAt())
                .setLinks(links);
    }

    private String storagePath(CustomApiFile file) {
        if (!isBlank(file.getStoragePath())) {
            return file.getStoragePath();
        }
        return file.getObjectKey();
    }

    private String stringValue(Map<String, Object> map, String key) {
        return stringValue(map, key, null);
    }

    private String stringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map == null ? null : map.get(key);
        return value == null ? defaultValue : String.valueOf(value);
    }

    private Integer intValue(Map<String, Object> map, String key) {
        Object value = map == null ? null : map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value != null) {
            return Integer.parseInt(String.valueOf(value));
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private void fillRequiredHeadDefaults(DecHead head) {
        if (head == null) {
            return;
        }
        String companyCode = firstNonBlank(head.getTradeCode(), head.getOwnerCode(), head.getAgentCode(), head.getCopCode(), "0000000000");
        String companyName = firstNonBlank(head.getTradeName(), head.getOwnerName(), head.getAgentName(), head.getCopName(), "未知企业");
        String companyScc = firstNonBlank(head.getTradeCoScc(), head.getOwnerCodeScc(), head.getAgentCodeScc(), "000000000000000000");
        if (isBlank(head.getAgentCode())) {
            head.setAgentCode(companyCode);
        }
        if (isBlank(head.getAgentName())) {
            head.setAgentName(companyName);
        }
        if (isBlank(head.getCopCode())) {
            head.setCopCode(companyCode);
        }
        if (isBlank(head.getCopName())) {
            head.setCopName(companyName);
        }
        if (isBlank(head.getOwnerName())) {
            head.setOwnerName(companyName);
        }
        if (isBlank(head.getTradeCode())) {
            head.setTradeCode(companyCode);
        }
        if (isBlank(head.getTradeName())) {
            head.setTradeName(companyName);
        }
        if (isBlank(head.getTradeCoScc())) {
            head.setTradeCoScc(companyScc);
        }
        if (isBlank(head.getAgentCodeScc())) {
            head.setAgentCodeScc(companyScc);
        }
        if (isBlank(head.getOwnerCodeScc())) {
            head.setOwnerCodeScc(companyScc);
        }
        if (isBlank(head.getDeclTrnRel())) {
            head.setDeclTrnRel("0");
        }
        if (isBlank(head.getEdiId())) {
            head.setEdiId("1");
        }
        if (isBlank(head.getEntryType())) {
            head.setEntryType("0");
        }
        if (isBlank(head.getInputerName())) {
            head.setInputerName("custom-api");
        }
        if (isBlank(head.getType())) {
            head.setType(firstNonBlank(head.getIeFlag(), "I"));
        }
        if (isBlank(head.getTypistNo())) {
            head.setTypistNo("custom-api");
        }
        if (isBlank(head.getPromiseItmes())) {
            head.setPromiseItmes("00000");
        }
        if (isBlank(head.getGoodsPlace())) {
            head.setGoodsPlace("");
        }
        if (isBlank(head.getDeclareName())) {
            head.setDeclareName("custom-api");
        }
    }

    private void fillRequiredListDefaults(DecList decList) {
        if (decList == null) {
            return;
        }
        if (decList.getGNo() == null) {
            decList.setGNo(1);
        }
        if (isBlank(decList.getCodeTs())) {
            decList.setCodeTs("0000000000");
        }
        if (isBlank(decList.getDutyMode())) {
            decList.setDutyMode("1");
        }
        if (decList.getDeclPrice() == null) {
            decList.setDeclPrice(BigDecimal.ZERO);
        }
        if (decList.getDeclTotal() == null) {
            decList.setDeclTotal(BigDecimal.ZERO);
        }
        if (isBlank(decList.getGUnit())) {
            decList.setGUnit(firstNonBlank(decList.getFirstUnit(), "035"));
        }
        if (isBlank(decList.getFirstUnit())) {
            decList.setFirstUnit(firstNonBlank(decList.getGUnit(), "035"));
        }
        if (decList.getGQty() == null) {
            decList.setGQty(BigDecimal.ZERO);
        }
        if (decList.getFirstQty() == null) {
            decList.setFirstQty(decList.getGQty());
        }
        if (decList.getSecondQty() == null) {
            decList.setSecondQty(BigDecimal.ZERO);
        }
        if (isBlank(decList.getGName())) {
            decList.setGName("");
        }
        if (isBlank(decList.getOriginCountry())) {
            decList.setOriginCountry("000");
        }
        if (isBlank(decList.getTradeCurr())) {
            decList.setTradeCurr("502");
        }
        if (isBlank(decList.getDestinationCountry())) {
            decList.setDestinationCountry("142");
        }
        if (isBlank(decList.getDistrictCode())) {
            decList.setDistrictCode("00000");
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private void trimStringColumns(Object entity) {
        if (entity == null) {
            return;
        }
        Map<String, Integer> columnSizes = columnSizeCache.computeIfAbsent(entity.getClass(), this::loadColumnSizes);
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (!String.class.equals(field.getType())) {
                continue;
            }
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField == null || tableField.value().isBlank()) {
                continue;
            }
            Integer maxLength = columnSizes.get(tableField.value().toUpperCase());
            if (maxLength == null || maxLength <= 0) {
                continue;
            }
            try {
                field.setAccessible(true);
                String value = (String) field.get(entity);
                if (value != null && value.length() > maxLength) {
                    log.warn("Custom API import value truncated, entity={}, field={}, column={}, length={} > {}",
                            entity.getClass().getSimpleName(), field.getName(), tableField.value(), value.length(), maxLength);
                    field.set(entity, value.substring(0, maxLength));
                }
            } catch (IllegalAccessException e) {
                throw new JeecgBootException("trim import field failed: " + field.getName());
            }
        }
    }

    private Map<String, Integer> loadColumnSizes(Class<?> entityClass) {
        TableName tableName = entityClass.getAnnotation(TableName.class);
        if (tableName == null || tableName.value().isBlank() || dataSource == null) {
            return Map.of();
        }
        Map<String, Integer> sizes = new ConcurrentHashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            loadColumnSizes(metaData, tableName.value().toUpperCase(), sizes);
            if (sizes.isEmpty()) {
                loadColumnSizes(metaData, tableName.value(), sizes);
            }
        } catch (Exception e) {
            log.warn("Custom API load column sizes failed, table={}", tableName.value(), e);
        }
        return sizes;
    }

    private void loadColumnSizes(DatabaseMetaData metaData, String tableName, Map<String, Integer> sizes) throws Exception {
        try (ResultSet columns = metaData.getColumns(null, null, tableName, "%")) {
            while (columns.next()) {
                sizes.put(columns.getString("COLUMN_NAME").toUpperCase(), columns.getInt("COLUMN_SIZE"));
            }
        }
    }

    private static SimpleModule customApiImportModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Date.class, new CustomApiDateDeserializer());
        return module;
    }

    private static class CustomApiDateDeserializer extends JsonDeserializer<Date> {
        private static final List<String> PATTERNS = List.of("yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd");

        @Override
        public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String value = parser.getValueAsString();
            if (value == null || value.isBlank()) {
                return null;
            }
            for (String pattern : PATTERNS) {
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                format.setLenient(false);
                try {
                    return format.parse(value);
                } catch (ParseException ignored) {
                }
            }
            return (Date) context.handleWeirdStringValue(
                    Date.class,
                    value,
                    "expected date format yyyy-MM-dd, yyyyMMdd or yyyy/MM/dd"
            );
        }
    }
}
