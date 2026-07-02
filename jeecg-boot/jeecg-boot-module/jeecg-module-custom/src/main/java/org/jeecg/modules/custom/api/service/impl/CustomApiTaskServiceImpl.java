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
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.mq.CustomApiTaskMqProducer;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.service.ICustomApiTaskService;
import org.jeecg.modules.custom.api.util.CustomApiCrypto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
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
    private final RestTemplate callbackRestTemplate;

    @Autowired
    private ICustomApiFileService fileService;
    @Autowired
    private CustomApiTaskMqProducer taskMqProducer;
    @Autowired
    private IDocumentService documentService;
    @Autowired
    private IDecHeadService decHeadService;
    @Autowired
    private IDecListService decListService;
    @Autowired
    private DataSource dataSource;

    public CustomApiTaskServiceImpl() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(30000);
        this.callbackRestTemplate = new RestTemplate(factory);
    }

    @Override
    public TaskResponse createTask(CustomApiApp app, TaskCreateRequest request) {
        if (request == null || isBlank(request.getFileId())) {
            throw new JeecgBootException("fileId is required");
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

        String taskId = CustomApiIds.taskId();
        Document document = new Document();
        document.markUploaded(file.getOriginalFilename(), storagePath(file), file.getStorageType(), file.getFileSize(), file.getContentType());
        document.markParseStarted(taskId);
        documentService.save(document);

        CustomApiTask task = new CustomApiTask()
                .setTaskId(taskId)
                .setFileId(file.getFileId())
                .setCustomerCode(app.getCustomerCode())
                .setClientTaskId(request.getClientTaskId())
                .setDocumentId(document.getId())
                .setDirection(direction)
                .setCompanyCode(isBlank(request.getCompanyCode()) ? app.getCompanyCode() : request.getCompanyCode())
                .setCallbackUrl(request.getCallbackUrl())
                .setCallbackSecret(request.getCallbackSecret())
                .setResponseMode(responseMode)
                .setStatus(CustomApiTask.STATUS_QUEUED)
                .setStage("queued")
                .setProgress(0)
                .setMetadataJson(request.getMetadata() == null ? null : JSON.toJSONString(request.getMetadata()))
                .setCreatedAt(LocalDateTime.now());
        save(task);

        try {
            taskMqProducer.sendParseTask(task, file);
        } catch (Exception e) {
            markEnqueueFailed(task, e);
            throw new JeecgBootException("failed to enqueue parse task: " + e.getMessage());
        }
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
    public void handleParseResult(Map<String, Object> message) {
        String taskId = stringValue(message, "taskId");
        if (isBlank(taskId)) {
            throw new JeecgBootException("taskId is required in parse result message");
        }
        CustomApiTask task = getOne(new LambdaQueryWrapper<CustomApiTask>().eq(CustomApiTask::getTaskId, taskId), false);
        if (task == null) {
            throw new JeecgBootException("task not found: " + taskId);
        }
        String status = stringValue(message, "status");
        task.setCustomsAiJobId(stringValue(message, "customsAiJobId", task.getCustomsAiJobId()));
        Integer runNo = intValue(message, "runNo");
        if (runNo != null) {
            task.setCustomsAiRunNo(runNo);
        }
        if ("running".equals(status)) {
            updateRunning(task, stringValue(message, "stage", "running"), intValue(message, "progress") == null ? 20 : intValue(message, "progress"));
            return;
        }
        if ("succeeded".equals(status)) {
            Object result = message.get("result");
            if (result == null) {
                throw new JeecgBootException("result is required when parse succeeded");
            }
            completeTask(task, result);
            return;
        }
        if ("failed".equals(status)) {
            failTask(task, new JeecgBootException(stringValue(message, "errorMessage", "customs-ai parse failed")));
            return;
        }
        throw new JeecgBootException("unsupported parse result status: " + status);
    }

    private void markEnqueueFailed(CustomApiTask task, Exception e) {
        task.setStatus(CustomApiTask.STATUS_FAILED);
        task.setStage("enqueue_failed");
        task.setErrorCode(e.getClass().getSimpleName());
        task.setErrorMessage(e.getMessage());
        task.setFinishedAt(LocalDateTime.now());
        updateById(task);
        if (task.getTaskId() != null) {
            try {
                documentService.failParse(task.getTaskId(), e.getMessage());
            } catch (Exception ignored) {
            }
        }
    }

    private void completeTask(CustomApiTask task, Object result) {
        try {
            Long decHeadId = importDeclaration(result);
            task.setDecHeadId(decHeadId);
            task.setResultJson(JSON.toJSONString(result));
            task.setStatus(CustomApiTask.STATUS_SUCCEEDED);
            task.setStage("completed");
            task.setProgress(100);
            task.setFinishedAt(LocalDateTime.now());
            updateById(task);
            if (task.getDocumentId() != null && decHeadId != null) {
                documentService.completeParse(task.getTaskId(), decHeadId);
            }
            pushCallbackIfNeeded(task, result, null);
        } catch (Exception e) {
            log.error("Custom API complete task failed, taskId={}", task.getTaskId(), e);
            failTask(task, e);
        }
    }

    private Long importDeclaration(Object result) {
        JSONObject json = (JSONObject) JSON.toJSON(result);
        Object headObj = json.get("DecHead");
        if (headObj == null) {
            return null;
        }
        DecHead head = mapper.convertValue(headObj, DecHead.class);
        head.setId(null);
        fillRequiredHeadDefaults(head);
        trimStringColumns(head);
        decHeadService.save(head);

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
            if (!goods.isEmpty()) {
                decListService.saveBatch(goods);
            }
        }
        return head.getId();
    }

    private void updateRunning(CustomApiTask task, String stage, Integer progress) {
        task.setStatus(CustomApiTask.STATUS_RUNNING);
        task.setStage(stage);
        task.setProgress(progress);
        if (task.getStartedAt() == null) {
            task.setStartedAt(LocalDateTime.now());
        }
        updateById(task);
    }

    private void failTask(CustomApiTask task, Exception e) {
        task.setStatus(CustomApiTask.STATUS_FAILED);
        task.setStage("failed");
        task.setErrorCode(truncate(e.getClass().getSimpleName(), ERROR_CODE_MAX_LENGTH));
        task.setErrorMessage(truncate(e.getMessage(), ERROR_MESSAGE_MAX_LENGTH));
        task.setFinishedAt(LocalDateTime.now());
        updateById(task);
        if (task.getTaskId() != null) {
            try {
                documentService.failParse(task.getTaskId(), e.getMessage());
            } catch (Exception ignored) {
            }
        }
        pushCallbackIfNeeded(task, null, e);
    }

    private void pushCallbackIfNeeded(CustomApiTask task, Object result, Exception error) {
        if (isBlank(task.getCallbackUrl()) || "polling".equals(task.getResponseMode())) {
            return;
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("event", error == null ? "task.completed" : "task.failed");
        body.put("taskId", task.getTaskId());
        body.put("clientTaskId", task.getClientTaskId());
        body.put("fileId", task.getFileId());
        body.put("runNo", task.getCustomsAiRunNo());
        body.put("status", task.getStatus());
        body.put("declareData", result);
        body.put("error", error == null ? null : Map.of("code", error.getClass().getSimpleName(), "message", error.getMessage()));
        body.put("finishedAt", task.getFinishedAt());

        try {
            byte[] bytes = mapper.writeValueAsBytes(body);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-CustomsAI-Task-Id", task.getTaskId());
            if (task.getCustomsAiRunNo() != null) {
                headers.set("X-CustomsAI-Run-No", String.valueOf(task.getCustomsAiRunNo()));
            }
            if (!isBlank(task.getCallbackSecret())) {
                headers.set("X-CustomsAI-Signature", CustomApiCrypto.hmacSha256(task.getCallbackSecret(), bytes));
            }
            callbackRestTemplate.postForEntity(task.getCallbackUrl(), new HttpEntity<>(new String(bytes, StandardCharsets.UTF_8), headers), String.class);
            task.setCallbackStatus("success");
            task.setCallbackError(null);
        } catch (Exception callbackError) {
            task.setCallbackStatus("failed");
            task.setCallbackError(truncate(callbackError.getMessage(), ERROR_MESSAGE_MAX_LENGTH));
        }
        updateById(task);
    }

    private CustomApiTask requireOwnedTask(CustomApiApp app, String taskId) {
        if (isBlank(taskId)) {
            throw new JeecgBootException("taskId is required");
        }
        CustomApiTask task = getOne(new LambdaQueryWrapper<CustomApiTask>()
                .eq(CustomApiTask::getTaskId, taskId)
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
        if (tableName == null || tableName.value().isBlank()) {
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
