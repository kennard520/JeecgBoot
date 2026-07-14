package org.jeecg.modules.custom.api.service;

import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomMqInbox;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.service.impl.CustomApiTaskServiceImpl;
import org.jeecg.modules.custom.cit.entity.DecHead;
import org.jeecg.modules.custom.cit.entity.DecList;
import org.jeecg.modules.custom.cit.service.IDecHeadService;
import org.jeecg.modules.custom.cit.service.IDecListService;
import org.jeecg.modules.custom.task.service.IDocumentService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomParseResultTransactionTest {

    @Test
    void duplicateEventDoesNotCreateAnotherDeclaration() throws Exception {
        Fixture fixture = fixture(task(1));
        when(fixture.inbox.receive(any(), any(), any(Integer.class), any(), any())).thenReturn(null);

        fixture.service.handleParseResult(succeeded(1));

        verify(fixture.taskMapper, never()).selectByTaskIdForUpdate(any());
        verify(fixture.decHeadService, never()).save(any());
    }

    @Test
    void sameResultEventWithDifferentJsonKeyOrderUsesSameInboxFingerprint() throws Exception {
        Fixture fixture = fixture(task(1));
        when(fixture.inbox.receive(any(), any(), any(Integer.class), any(), any()))
                .thenReturn(null);
        Map<String, Object> original = succeeded(1);
        Map<String, Object> reordered = reverseEntries(original);

        fixture.service.handleParseResult(original);
        fixture.service.handleParseResult(reordered);

        ArgumentCaptor<String> hashes = ArgumentCaptor.forClass(String.class);
        verify(fixture.inbox, times(2)).receive(
                any(), any(), any(Integer.class), any(), hashes.capture());
        assertThat(hashes.getAllValues()).hasSize(2);
        assertThat(hashes.getAllValues().get(0)).isEqualTo(hashes.getAllValues().get(1));
    }

    @Test
    void oldRunCannotOverrideNewerTaskRun() throws Exception {
        Fixture fixture = fixture(task(2));

        fixture.service.handleParseResult(succeeded(1));

        verify(fixture.inbox).markIgnored(eq(fixture.inboxRecord), any());
        verify(fixture.decHeadService, never()).save(any());
        verify(fixture.taskMapper, never()).updateById(any(CustomApiTask.class));
    }

    @Test
    void succeededResultImportsOneOwnedHeadAndListsThenCreatesCallbackDelivery() throws Exception {
        Fixture fixture = fixture(task(1));
        doAnswer(invocation -> {
            ((DecHead) invocation.getArgument(0)).setId(88L);
            return true;
        }).when(fixture.decHeadService).save(any(DecHead.class));

        fixture.service.handleParseResult(succeeded(1));

        ArgumentCaptor<DecHead> head = ArgumentCaptor.forClass(DecHead.class);
        verify(fixture.decHeadService).save(head.capture());
        assertThat(head.getValue().getSourceTaskId()).isEqualTo("task-1");
        assertThat(head.getValue().getCustomerCode()).isEqualTo("CUSTOMER-A");
        assertThat(head.getValue().getSourceType()).isEqualTo("WEB");
        verify(fixture.decListService).saveBatch(any());
        verify(fixture.documentService).completeParse("task-1", 88L);
        verify(fixture.callbackDelivery).enqueueTerminal(
                any(CustomApiTask.class), eq("task.completed"), any(), eq(null), eq(null));
        verify(fixture.inbox).markProcessed(fixture.inboxRecord);
        assertThat(fixture.task.getStatus()).isEqualTo(CustomApiTask.STATUS_SUCCEEDED);
        assertThat(fixture.task.getDecHeadId()).isEqualTo(88L);
    }

    @Test
    void decListFailureEscapesTransactionalBoundaryWithoutMarkingTaskOrDocumentComplete() throws Exception {
        Fixture fixture = fixture(task(1));
        doAnswer(invocation -> {
            ((DecHead) invocation.getArgument(0)).setId(88L);
            return true;
        }).when(fixture.decHeadService).save(any(DecHead.class));
        doThrow(new IllegalStateException("list insert failed"))
                .when(fixture.decListService).saveBatch(any());

        assertThatThrownBy(() -> fixture.service.handleParseResult(succeeded(1)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("list insert failed");

        verify(fixture.documentService, never()).completeParse(any(), any());
        verify(fixture.taskMapper, never()).updateById(any(CustomApiTask.class));
        verify(fixture.callbackDelivery, never()).enqueueTerminal(any(), any(), any(), any(), any());
        verify(fixture.inbox, never()).markProcessed(any());
        Transactional transactional = CustomApiTaskServiceImpl.class
                .getMethod("handleParseResult", Map.class).getAnnotation(Transactional.class);
        assertThat(transactional).isNotNull();
        assertThat(transactional.rollbackFor()).contains(Exception.class);
    }

    @Test
    void runningEventUpdatesTaskAndDocumentHeartbeat() throws Exception {
        Fixture fixture = fixture(task(1));

        fixture.service.handleParseResult(running(1));

        verify(fixture.taskMapper).updateById(fixture.task);
        verify(fixture.documentService).updateParseHeartbeat(
                eq("task-1"), eq("extracting"), eq(35), any(LocalDateTime.class));
        verify(fixture.inbox).markProcessed(fixture.inboxRecord);
        assertThat(fixture.task.getLastHeartbeatAt()).isNotNull();
    }

    @Test
    void byteLimitedColumnsTrimMultibyteValuesBeforeBatchInsert() throws Exception {
        Fixture fixture = fixture(task(1));
        doAnswer(invocation -> {
            ((DecHead) invocation.getArgument(0)).setId(88L);
            return true;
        }).when(fixture.decHeadService).save(any(DecHead.class));
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);
        ResultSet emptyColumns = mock(ResultSet.class);
        ResultSet listColumns = mock(ResultSet.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metadata);
        when(metadata.getColumns(null, null, "DEC_HEAD", "%")).thenReturn(emptyColumns);
        when(metadata.getColumns(null, null, "DEC_LIST", "%")).thenReturn(listColumns);
        when(emptyColumns.next()).thenReturn(false);
        when(listColumns.next()).thenReturn(true, false);
        when(listColumns.getString("COLUMN_NAME")).thenReturn("G_MODEL");
        when(listColumns.getInt("COLUMN_SIZE")).thenReturn(255);
        when(listColumns.getInt("CHAR_OCTET_LENGTH")).thenReturn(255);
        setField(fixture.service, "dataSource", dataSource);
        String oversizedByBytes = "4|3|" + "实验室用试剂|".repeat(30);

        fixture.service.handleParseResult(succeededWithGModel(oversizedByBytes));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<DecList>> lists = ArgumentCaptor.forClass(List.class);
        verify(fixture.decListService).saveBatch(lists.capture());
        String stored = lists.getValue().get(0).getGModel();
        assertThat(stored).startsWith("4|3|");
        assertThat(stored.getBytes(StandardCharsets.UTF_8).length).isLessThanOrEqualTo(255);
    }

    private Fixture fixture(CustomApiTask task) throws Exception {
        CustomApiTaskServiceImpl service = new CustomApiTaskServiceImpl();
        CustomApiTaskMapper taskMapper = mock(CustomApiTaskMapper.class);
        ICustomMqInboxService inbox = mock(ICustomMqInboxService.class);
        IDecHeadService decHeadService = mock(IDecHeadService.class);
        IDecListService decListService = mock(IDecListService.class);
        IDocumentService documentService = mock(IDocumentService.class);
        ICustomCallbackDeliveryService callbackDelivery = mock(ICustomCallbackDeliveryService.class);
        CustomMqInbox inboxRecord = new CustomMqInbox().setId(5L).setEventId("evt-result-1")
                .setTaskId("task-1").setRunNo(1).setStatus(CustomMqInbox.STATUS_RECEIVED);
        setField(service, "customApiTaskMapper", taskMapper);
        setField(service, "inboxService", inbox);
        setField(service, "decHeadService", decHeadService);
        setField(service, "decListService", decListService);
        setField(service, "documentService", documentService);
        setField(service, "callbackDeliveryService", callbackDelivery);
        when(inbox.receive(any(), any(), any(Integer.class), any(), any())).thenReturn(inboxRecord);
        when(taskMapper.selectByTaskIdForUpdate("task-1")).thenReturn(task);
        when(decListService.saveBatch(any())).thenReturn(true);
        return new Fixture(service, taskMapper, inbox, decHeadService, decListService,
                documentService, callbackDelivery, task, inboxRecord);
    }

    private CustomApiTask task(int runNo) {
        return new CustomApiTask().setId(1L).setTaskId("task-1").setFileId("file-1")
                .setCustomerCode("CUSTOMER-A").setCompanyCode("CUSTOMS")
                .setResponseMode("both").setCallbackUrl("https://callbacks.example/result")
                .setCustomsAiRunNo(runNo).setStatus(CustomApiTask.STATUS_RUNNING)
                .setCreatedAt(LocalDateTime.now().minusMinutes(2));
    }

    private Map<String, Object> succeeded(int runNo) {
        return Map.ofEntries(
                Map.entry("eventId", "evt-result-1"),
                Map.entry("eventType", "parse.status"),
                Map.entry("schemaVersion", 2),
                Map.entry("taskId", "task-1"),
                Map.entry("runNo", runNo),
                Map.entry("attemptNo", 1),
                Map.entry("customerCode", "CUSTOMER-A"),
                Map.entry("agentCode", "CUSTOMS"),
                Map.entry("status", "succeeded"),
                Map.entry("stage", "completed"),
                Map.entry("progress", 100),
                Map.entry("occurredAt", "2026-07-14T12:00:00Z"),
                Map.entry("result", Map.of(
                        "DecHead", Map.of("TradeName", "Customer A"),
                        "DecList", List.of(Map.of("GNo", 1, "GName", "Goods")))));
    }

    private Map<String, Object> succeededWithGModel(String gModel) {
        return Map.ofEntries(
                Map.entry("eventId", "evt-result-1"),
                Map.entry("eventType", "parse.status"),
                Map.entry("schemaVersion", 2),
                Map.entry("taskId", "task-1"),
                Map.entry("runNo", 1),
                Map.entry("attemptNo", 1),
                Map.entry("customerCode", "CUSTOMER-A"),
                Map.entry("agentCode", "CUSTOMS"),
                Map.entry("status", "succeeded"),
                Map.entry("stage", "completed"),
                Map.entry("progress", 100),
                Map.entry("occurredAt", "2026-07-14T12:00:00Z"),
                Map.entry("result", Map.of(
                        "DecHead", Map.of("TradeName", "Customer A"),
                        "DecList", List.of(Map.of(
                                "GNo", 1,
                                "GName", "Goods",
                                "GModel", gModel)))));
    }

    private Map<String, Object> running(int runNo) {
        return Map.ofEntries(
                Map.entry("eventId", "evt-result-1"),
                Map.entry("eventType", "parse.status"),
                Map.entry("schemaVersion", 2),
                Map.entry("taskId", "task-1"),
                Map.entry("runNo", runNo),
                Map.entry("attemptNo", 1),
                Map.entry("customerCode", "CUSTOMER-A"),
                Map.entry("agentCode", "CUSTOMS"),
                Map.entry("status", "running"),
                Map.entry("stage", "extracting"),
                Map.entry("progress", 35),
                Map.entry("occurredAt", "2026-07-14T12:00:00Z"));
    }

    private Map<String, Object> reverseEntries(Map<String, Object> source) {
        List<Map.Entry<String, Object>> entries = new ArrayList<>(source.entrySet());
        Collections.reverse(entries);
        Map<String, Object> reordered = new LinkedHashMap<>();
        entries.forEach(entry -> reordered.put(entry.getKey(), entry.getValue()));
        return reordered;
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

    private record Fixture(CustomApiTaskServiceImpl service,
                           CustomApiTaskMapper taskMapper,
                           ICustomMqInboxService inbox,
                           IDecHeadService decHeadService,
                           IDecListService decListService,
                           IDocumentService documentService,
                           ICustomCallbackDeliveryService callbackDelivery,
                           CustomApiTask task,
                           CustomMqInbox inboxRecord) {
    }
}
