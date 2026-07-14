package org.jeecg.modules.custom.api.mq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomMqOutbox;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CustomApiTaskMqProducerTest {

    @Test
    void waitsForAckConfirmAndPublishesPersistentMessage() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        AmqpAdmin admin = mock(AmqpAdmin.class);
        CustomApiTaskMqProducer producer = new CustomApiTaskMqProducer(rabbit, admin, 1000L);
        doAnswer(invocation -> {
            CorrelationData correlation = invocation.getArgument(3);
            correlation.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbit).send(any(String.class), any(String.class), any(Message.class), any(CorrelationData.class));
        CustomMqOutbox event = event();

        producer.publishConfirmed(task(), file(), event);

        ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
        verify(rabbit).send(eq(CustomApiMqConstant.PARSE_REQUEST_EXCHANGE), eq("CUSTOMS"),
                message.capture(), any(CorrelationData.class));
        assertThat(message.getValue().getMessageProperties().getDeliveryMode())
                .isEqualTo(MessageDeliveryMode.PERSISTENT);
        assertThat(message.getValue().getMessageProperties().getMessageId()).isEqualTo("event-1");
    }

    @Test
    void publishesPythonV2RequestFixtureWithoutDroppingRequiredFields() throws Exception {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        CustomApiTaskMqProducer producer = new CustomApiTaskMqProducer(
                rabbit, mock(AmqpAdmin.class), 1000L);
        doAnswer(invocation -> {
            CorrelationData correlation = invocation.getArgument(3);
            correlation.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbit).send(any(String.class), any(String.class), any(Message.class), any(CorrelationData.class));

        producer.publishConfirmed(task(), file(), event());

        ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
        verify(rabbit).send(eq(CustomApiMqConstant.PARSE_REQUEST_EXCHANGE), eq("CUSTOMS"),
                message.capture(), any(CorrelationData.class));
        JsonNode payload = JsonMapper.builder().build().readTree(message.getValue().getBody());
        assertThat(payload.path("eventId").asText()).isEqualTo("event-1");
        assertThat(payload.path("schemaVersion").asInt()).isEqualTo(2);
        assertThat(payload.path("taskId").asText()).isEqualTo("task-1");
        assertThat(payload.path("runNo").asInt()).isEqualTo(1);
        assertThat(payload.path("attemptNo").asInt()).isEqualTo(1);
        assertThat(payload.path("customerCode").asText()).isEqualTo("CUSTOMER-A");
        assertThat(payload.path("agentCode").asText()).isEqualTo("CUSTOMS");
        assertThat(payload.path("fileId").asText()).isEqualTo("file-1");
        assertThat(payload.path("fileSize").asLong()).isEqualTo(19L);
        assertThat(payload.path("sha256").asText()).matches("[0-9a-f]{64}");
        assertThat(payload.path("downloadUrl").asText()).isEqualTo(
                "https://java.internal/custom/api/internal/tasks/task-1/files/file-1/download");
    }

    @Test
    void rejectsNackSoOutboxRemainsRetryable() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        CustomApiTaskMqProducer producer = new CustomApiTaskMqProducer(rabbit, mock(AmqpAdmin.class), 1000L);
        doAnswer(invocation -> {
            CorrelationData correlation = invocation.getArgument(3);
            correlation.getFuture().complete(new CorrelationData.Confirm(false, "broker nack"));
            return null;
        }).when(rabbit).send(any(String.class), any(String.class), any(Message.class), any(CorrelationData.class));

        assertThatThrownBy(() -> producer.publishConfirmed(task(), file(), event()))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("broker nack");
    }

    @Test
    void rejectsAckedButUnroutableMandatoryReturn() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        CustomApiTaskMqProducer producer = new CustomApiTaskMqProducer(
                rabbit, mock(AmqpAdmin.class), 1000L);
        doAnswer(invocation -> {
            CorrelationData correlation = invocation.getArgument(3);
            correlation.setReturned(new ReturnedMessage(
                    invocation.getArgument(2), 312, "NO_ROUTE",
                    CustomApiMqConstant.PARSE_REQUEST_EXCHANGE, "CUSTOMS"));
            correlation.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbit).send(any(String.class), any(String.class), any(Message.class), any(CorrelationData.class));

        assertThatThrownBy(() -> producer.publishConfirmed(task(), file(), event()))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("unroutable");
    }

    private CustomMqOutbox event() {
        return new CustomMqOutbox().setId(1L).setEventId("event-1")
                .setExchangeName(CustomApiMqConstant.PARSE_REQUEST_EXCHANGE)
                .setRoutingKey("CUSTOMS").setAggregateVersion(1)
                .setPayloadJson("""
                        {"eventId":"event-1","eventType":"parse.requested","schemaVersion":2,
                         "taskId":"task-1","runNo":1,"attemptNo":1,"maxAttempts":3,
                         "customerCode":"CUSTOMER-A","agentCode":"CUSTOMS","companyCode":"CUSTOMS",
                         "fileId":"file-1","originalFilename":"case.zip","fileSize":19,
                         "sha256":"4f749827f0e4608f5d369e4f23e8c4e88f33372e629a1ea14a175c5c383154b5",
                         "downloadUrl":"https://java.internal/custom/api/internal/tasks/task-1/files/file-1/download"}
                        """)
                .setStatus(CustomMqOutbox.STATUS_SENDING);
    }

    private CustomApiTask task() {
        return new CustomApiTask().setTaskId("task-1").setFileId("file-1")
                .setCustomerCode("CUSTOMER-A").setCompanyCode("CUSTOMS").setCustomsAiRunNo(1);
    }

    private CustomApiFile file() {
        return new CustomApiFile().setFileId("file-1").setActualFileSize(19L)
                .setActualSha256("4f749827f0e4608f5d369e4f23e8c4e88f33372e629a1ea14a175c5c383154b5")
                .setStatus(CustomApiFile.STATUS_UPLOADED);
    }
}
