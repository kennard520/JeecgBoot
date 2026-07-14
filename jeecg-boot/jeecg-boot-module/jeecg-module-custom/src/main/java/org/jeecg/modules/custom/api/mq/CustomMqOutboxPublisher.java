package org.jeecg.modules.custom.api.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomMqOutbox;
import org.jeecg.modules.custom.api.mapper.CustomApiTaskMapper;
import org.jeecg.modules.custom.api.service.ICustomApiFileService;
import org.jeecg.modules.custom.api.service.ICustomMqOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class CustomMqOutboxPublisher {
    private final ICustomMqOutboxService outboxService;
    private final CustomApiTaskMqProducer producer;
    private final CustomApiTaskMapper taskMapper;
    private final ICustomApiFileService fileService;
    private final int batchSize;
    private final int maxAttempts;
    private final long baseDelaySeconds;
    private final long maxDelaySeconds;
    private final long claimTimeoutSeconds;
    private final String publisherId;

    @Autowired
    public CustomMqOutboxPublisher(
            ICustomMqOutboxService outboxService,
            CustomApiTaskMqProducer producer,
            CustomApiTaskMapper taskMapper,
            ICustomApiFileService fileService,
            @Value("${custom.api.outbox.batch-size:20}") int batchSize,
            @Value("${custom.api.outbox.max-attempts:8}") int maxAttempts,
            @Value("${custom.api.outbox.base-delay-seconds:2}") long baseDelaySeconds,
            @Value("${custom.api.outbox.max-delay-seconds:300}") long maxDelaySeconds,
            @Value("${custom.api.outbox.claim-timeout-seconds:300}") long claimTimeoutSeconds,
            @Value("${custom.api.outbox.publisher-id:${HOSTNAME:java}}") String publisherId) {
        this.outboxService = outboxService;
        this.producer = producer;
        this.taskMapper = taskMapper;
        this.fileService = fileService;
        this.batchSize = batchSize;
        this.maxAttempts = maxAttempts;
        this.baseDelaySeconds = baseDelaySeconds;
        this.maxDelaySeconds = maxDelaySeconds;
        this.claimTimeoutSeconds = claimTimeoutSeconds;
        this.publisherId = (publisherId == null || publisherId.isBlank() ? "java" : publisherId)
                + "-" + UUID.randomUUID();
    }

    @Scheduled(fixedDelayString = "${custom.api.outbox.publish-interval-ms:1000}",
            scheduler = "customReliabilityTaskScheduler")
    public void publishPending() {
        outboxService.releaseStaleClaims(claimTimeoutSeconds);
        for (CustomMqOutbox event : outboxService.findPublishable(batchSize)) {
            String claimToken = outboxService.claim(event.getId(), publisherId);
            if (claimToken == null) {
                continue;
            }
            try {
                event.setStatus(CustomMqOutbox.STATUS_SENDING)
                        .setClaimToken(claimToken).setClaimedBy(publisherId);
                CustomApiTask task = taskMapper.selectOne(new LambdaQueryWrapper<CustomApiTask>()
                        .eq(CustomApiTask::getTaskId, event.getAggregateId()));
                if (task == null) {
                    throw new IllegalStateException("task not found: " + event.getAggregateId());
                }
                CustomApiFile file = fileService.getOne(new LambdaQueryWrapper<CustomApiFile>()
                        .eq(CustomApiFile::getFileId, task.getFileId()), false);
                if (file == null) {
                    throw new IllegalStateException("file not found: " + task.getFileId());
                }
                event = outboxService.prepareForPublish(event, claimToken, task, file);
                producer.publishConfirmed(task, file, event);
                outboxService.markSent(event, claimToken);
            } catch (StaleOutboxRunException staleRun) {
                log.warn("Dead-letter stale custom MQ outbox, eventId={}", event.getEventId());
                try {
                    outboxService.reschedule(event, claimToken, message(staleRun),
                            1, baseDelaySeconds, maxDelaySeconds);
                } catch (OutboxClaimLostException claimLost) {
                    log.info("Skip stale custom MQ outbox dead-letter, eventId={}", event.getEventId());
                }
            } catch (OutboxClaimLostException claimLost) {
                log.info("Skip stale custom MQ outbox completion, eventId={}", event.getEventId());
            } catch (Exception e) {
                log.warn("Publish custom MQ outbox failed, eventId={}", event.getEventId(), e);
                try {
                    outboxService.reschedule(event, claimToken, message(e),
                            maxAttempts, baseDelaySeconds, maxDelaySeconds);
                } catch (OutboxClaimLostException claimLost) {
                    log.info("Skip stale custom MQ outbox reschedule, eventId={}", event.getEventId());
                }
            }
        }
    }

    private String message(Exception error) {
        return error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
    }
}
