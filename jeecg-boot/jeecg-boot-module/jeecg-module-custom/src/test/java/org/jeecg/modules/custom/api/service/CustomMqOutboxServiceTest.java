package org.jeecg.modules.custom.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jeecg.modules.custom.ai.service.CustomAgentAccessService;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomMqOutbox;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.mapper.CustomMqOutboxMapper;
import org.jeecg.modules.custom.api.mq.OutboxClaimLostException;
import org.jeecg.modules.custom.api.security.InternalDownloadTokenService;
import org.jeecg.modules.custom.api.service.impl.CustomApiTaskServiceImpl;
import org.jeecg.modules.custom.api.service.impl.CustomMqOutboxServiceImpl;
import org.jeecg.modules.custom.api.util.CanonicalRequestHasher;
import org.jeecg.modules.custom.api.vo.TaskCreateRequest;
import org.jeecg.modules.custom.api.vo.TaskResponse;
import org.jeecg.modules.custom.task.entity.Document;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomMqOutboxServiceTest {

    @Test
    void createsOnePendingOutboxWithStableInternalDownloadForTaskRun() throws Exception {
        CustomMqOutboxMapper mapper = mock(CustomMqOutboxMapper.class);
        CustomMqOutboxServiceImpl service = new CustomMqOutboxServiceImpl(mapper, tokenService());
        doAnswer(invocation -> {
            ((CustomMqOutbox) invocation.getArgument(0)).setId(11L);
            return 1;
        }).when(mapper).insert(any(CustomMqOutbox.class));
        CustomApiTask task = task("task-1", "file-1", 1);
        CustomApiFile file = file("file-1");

        CustomMqOutbox event = service.enqueueParseTask(task, file, 1);

        assertThat(event.getId()).isEqualTo(11L);
        assertThat(event.getStatus()).isEqualTo(CustomMqOutbox.STATUS_PENDING);
        assertThat(event.getAggregateId()).isEqualTo("task-1");
        assertThat(event.getAggregateVersion()).isEqualTo(1);
        JsonNode payload = JsonMapper.builder().build().readTree(event.getPayloadJson());
        assertThat(payload.path("eventId").asText()).isEqualTo(event.getEventId());
        assertThat(payload.path("eventType").asText()).isEqualTo("parse.requested");
        assertThat(payload.path("schemaVersion").asInt()).isEqualTo(2);
        assertThat(payload.path("taskId").asText()).isEqualTo("task-1");
        assertThat(payload.path("runNo").asInt()).isEqualTo(1);
        assertThat(payload.path("attemptNo").asInt()).isEqualTo(1);
        assertThat(payload.path("customerCode").asText()).isEqualTo("CUSTOMER-A");
        assertThat(payload.path("agentCode").asText()).isEqualTo("CUSTOMS");
        assertThat(payload.path("companyCode").asText()).isEqualTo("CUSTOMS");
        assertThat(payload.path("fileId").asText()).isEqualTo("file-1");
        assertThat(payload.path("fileSize").asLong()).isEqualTo(128L);
        assertThat(payload.path("sha256").asText()).isEqualTo("b".repeat(64));
        assertThat(payload.path("downloadUrl").asText()).isEqualTo(
                "https://smart-entry.citclub.org/jeecgboot/custom/api/internal/tasks/task-1/files/file-1/download"
                        + "?runNo=1&expires=1784005500&signature="
                        + payload.path("downloadUrl").asText().substring(
                        payload.path("downloadUrl").asText().indexOf("signature=") + 10));
        assertThat(payload.path("downloadUrl").asText()).contains("runNo=1", "expires=1784005500", "signature=");
        assertThat(payload.has("downloadHeaders")).isFalse();
    }

    @Test
    void returnsExistingOutboxForSameTaskRunWithoutDuplicateInsert() {
        CustomMqOutboxMapper mapper = mock(CustomMqOutboxMapper.class);
        CustomMqOutboxServiceImpl service = new CustomMqOutboxServiceImpl(mapper, tokenService());
        CustomMqOutbox existing = new CustomMqOutbox().setId(5L).setAggregateId("task-1")
                .setAggregateVersion(1).setStatus(CustomMqOutbox.STATUS_SENT);
        when(mapper.selectOne(any())).thenReturn(existing);

        assertThat(service.enqueueParseTask(task("task-1", "file-1", 1), file("file-1"), 1))
                .isSameAs(existing);
        verify(mapper, never()).insert(any(CustomMqOutbox.class));
    }

    @Test
    void rejectsSchemaV2RequestWithoutVerifiedActualFileMetadata() {
        CustomMqOutboxServiceImpl service = new CustomMqOutboxServiceImpl(
                mock(CustomMqOutboxMapper.class), tokenService());
        CustomApiFile unverified = file("file-1").setActualFileSize(null).setActualSha256(null);

        assertThatThrownBy(() -> service.enqueueParseTask(task("task-1", "file-1", 1), unverified, 1))
                .isInstanceOf(org.jeecg.common.exception.JeecgBootException.class)
                .hasMessageContaining("actual file size");
    }

    @Test
    void apiTaskCreationPersistsOutboxInsteadOfPublishingTransport() throws Exception {
        CustomApiTaskServiceImpl taskService = spy(new CustomApiTaskServiceImpl());
        ICustomApiFileService fileService = mock(ICustomApiFileService.class);
        IDocumentService documentService = mock(IDocumentService.class);
        CustomAgentAccessService accessService = mock(CustomAgentAccessService.class);
        CustomApiIdempotencyService idempotency = mock(CustomApiIdempotencyService.class);
        CanonicalRequestHasher hasher = mock(CanonicalRequestHasher.class);
        ICustomMqOutboxService outboxService = mock(ICustomMqOutboxService.class);
        setField(taskService, "fileService", fileService);
        setField(taskService, "documentService", documentService);
        setField(taskService, "agentAccessService", accessService);
        setField(taskService, "idempotencyService", idempotency);
        setField(taskService, "requestHasher", hasher);
        setField(taskService, "outboxService", outboxService);
        doReturn(true).when(taskService).save(any(CustomApiTask.class));
        when(documentService.save(any(Document.class))).thenReturn(true);
        CustomApiApp app = new CustomApiApp().setId(7L).setCustomerCode("CUSTOMER-A").setEnabled(1);
        CustomApiFile file = file("file-1").setAppId(7L).setCustomerCode("CUSTOMER-A");
        when(fileService.requireUploadedFile(app, "file-1")).thenReturn(file);
        when(accessService.requireApiAgent(app, null)).thenReturn("CUSTOMS");
        when(hasher.hashTask(any(), eq("CUSTOMS"))).thenReturn("a".repeat(64));
        TaskCreateRequest request = new TaskCreateRequest();
        request.setFileId("file-1");

        TaskResponse response = taskService.createTask(app, request, null);

        assertThat(response.getStatus()).isEqualTo(CustomApiTask.STATUS_QUEUED);
        verify(outboxService).enqueueParseTask(any(CustomApiTask.class), eq(file), eq(1));
    }

    @Test
    void enqueueRequiresAnExistingTransaction() throws Exception {
        Transactional transactional = CustomMqOutboxServiceImpl.class
                .getMethod("enqueueParseTask", CustomApiTask.class, CustomApiFile.class, int.class)
                .getAnnotation(Transactional.class);

        assertThat(transactional).isNotNull();
        assertThat(transactional.propagation()).isEqualTo(Propagation.MANDATORY);
    }

    @Test
    void claimCreatesTokenAndPersistsPublisherIdentity() {
        CustomMqOutboxMapper mapper = mock(CustomMqOutboxMapper.class);
        CustomMqOutboxServiceImpl service = new CustomMqOutboxServiceImpl(mapper, tokenService());
        when(mapper.claim(eq(12L), anyString(), eq("java-a"), any(LocalDateTime.class)))
                .thenReturn(1);

        String claimToken = service.claim(12L, "java-a");

        assertThat(claimToken).isNotBlank();
        verify(mapper).claim(eq(12L), eq(claimToken), eq("java-a"), any(LocalDateTime.class));
    }

    @Test
    void stalePublisherCannotMarkReclaimedEventSent() {
        CustomMqOutboxMapper mapper = mock(CustomMqOutboxMapper.class);
        CustomMqOutboxServiceImpl service = new CustomMqOutboxServiceImpl(mapper, tokenService());
        when(mapper.markSent(eq(12L), eq("old-token"), any(LocalDateTime.class)))
                .thenReturn(0);

        assertThatThrownBy(() -> service.markSent(12L, "old-token"))
                .isInstanceOf(OutboxClaimLostException.class);
    }

    @Test
    void exhaustedPublishAtomicallyFailsTaskDocumentAndEnqueuesCallback() {
        CustomMqOutboxMapper mapper = mock(CustomMqOutboxMapper.class);
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        IDocumentService documentService = mock(IDocumentService.class);
        ICustomCallbackDeliveryService callbackService = mock(ICustomCallbackDeliveryService.class);
        CustomMqOutboxServiceImpl service = new CustomMqOutboxServiceImpl(
                mapper, tokenService(), taskMapper, documentService, callbackService);
        CustomMqOutbox event = new CustomMqOutbox().setId(12L).setEventId("event-1")
                .setAggregateId("task-1").setAggregateVersion(1).setAttemptCount(0)
                .setStatus(CustomMqOutbox.STATUS_SENDING).setClaimToken("claim-1");
        CustomApiTask task = task("task-1", "file-1", 1)
                .setId(21L).setCallbackUrl("https://callback.example/result")
                .setResponseMode("both");
        when(mapper.reschedule(eq(12L), eq("claim-1"), eq(CustomMqOutbox.STATUS_DEAD),
                eq(1), eq(null), eq("broker unavailable"), any(LocalDateTime.class)))
                .thenReturn(1);
        when(taskMapper.selectByTaskIdForUpdate("task-1")).thenReturn(task);

        service.reschedule(event, "claim-1", "broker unavailable", 1, 2L, 300L);

        assertThat(task.getStatus()).isEqualTo(CustomApiTask.STATUS_FAILED);
        assertThat(task.getErrorCode()).isEqualTo("OUTBOX_DEAD");
        verify(taskMapper).updateById(task);
        verify(documentService).failParse("task-1", "broker unavailable");
        verify(callbackService).enqueueTerminal(
                task, "task.failed", null, "OUTBOX_DEAD", "broker unavailable");
    }

    @Test
    void controlledReplayOnlyResetsDeadEventAndItsOutboxFailedTask() {
        CustomMqOutboxMapper mapper = mock(CustomMqOutboxMapper.class);
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        IDocumentService documentService = mock(IDocumentService.class);
        ICustomCallbackDeliveryService callbackService = mock(ICustomCallbackDeliveryService.class);
        CustomMqOutboxServiceImpl service = new CustomMqOutboxServiceImpl(
                mapper, tokenService(), taskMapper, documentService, callbackService);
        CustomMqOutbox dead = new CustomMqOutbox().setId(12L).setEventId("event-1")
                .setAggregateId("task-1").setAggregateVersion(1)
                .setStatus(CustomMqOutbox.STATUS_DEAD).setAttemptCount(8);
        CustomApiTask task = task("task-1", "file-1", 1).setId(21L)
                .setStatus(CustomApiTask.STATUS_FAILED).setErrorCode("OUTBOX_DEAD")
                .setErrorMessage("broker unavailable");
        when(mapper.selectOne(any())).thenReturn(dead);
        when(mapper.replayDead(eq(12L), any(LocalDateTime.class))).thenReturn(1);
        when(taskMapper.selectByTaskIdForUpdate("task-1")).thenReturn(task);

        CustomMqOutbox replayed = service.replayDead("event-1");

        assertThat(replayed.getStatus()).isEqualTo(CustomMqOutbox.STATUS_PENDING);
        assertThat(replayed.getAttemptCount()).isZero();
        assertThat(task.getStatus()).isEqualTo(CustomApiTask.STATUS_QUEUED);
        verify(documentService).markParseQueued("task-1");
    }

    private CustomApiTask task(String taskId, String fileId, int runNo) {
        return new CustomApiTask().setTaskId(taskId).setFileId(fileId).setCustomerCode("CUSTOMER-A")
                .setCompanyCode("CUSTOMS").setDirection("import").setStatus(CustomApiTask.STATUS_QUEUED)
                .setCustomsAiRunNo(runNo).setCreatedAt(LocalDateTime.now());
    }

    private CustomApiFile file(String fileId) {
        return new CustomApiFile().setFileId(fileId).setCustomerCode("CUSTOMER-A")
                .setOriginalFilename("case.zip").setContentType("application/zip")
                .setFileSize(999L).setSha256("c".repeat(64))
                .setActualFileSize(128L).setActualSha256("b".repeat(64))
                .setStatus(CustomApiFile.STATUS_UPLOADED);
    }

    private InternalDownloadTokenService tokenService() {
        return new InternalDownloadTokenService(
                "https://smart-entry.citclub.org/jeecgboot", "s".repeat(32), 300,
                Clock.fixed(Instant.parse("2026-07-14T05:00:00Z"), ZoneOffset.UTC));
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }
}
