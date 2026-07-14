package org.jeecg.modules.custom.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.custom.api.entity.CustomMqOutbox;

import java.time.LocalDateTime;

public interface CustomMqOutboxMapper extends BaseMapper<CustomMqOutbox> {

    @Update("UPDATE CUSTOM_MQ_OUTBOX SET STATUS = 'SENDING', CLAIM_TOKEN = #{claimToken}, "
            + "CLAIMED_BY = #{claimedBy}, CLAIMED_AT = #{now}, UPDATED_AT = #{now} "
            + "WHERE ID = #{id} AND STATUS = 'PENDING' "
            + "AND (NEXT_ATTEMPT_AT IS NULL OR NEXT_ATTEMPT_AT <= #{now})")
    int claim(@Param("id") Long id,
              @Param("claimToken") String claimToken,
              @Param("claimedBy") String claimedBy,
              @Param("now") LocalDateTime now);

    @Update("UPDATE CUSTOM_MQ_OUTBOX SET PAYLOAD_JSON = #{payloadJson}, UPDATED_AT = #{now} "
            + "WHERE ID = #{id} AND STATUS = 'SENDING' AND CLAIM_TOKEN = #{claimToken}")
    int refreshPayload(@Param("id") Long id,
                       @Param("claimToken") String claimToken,
                       @Param("payloadJson") String payloadJson,
                       @Param("now") LocalDateTime now);

    @Update("UPDATE CUSTOM_MQ_OUTBOX SET STATUS = 'SENT', SENT_AT = #{now}, CLAIMED_AT = NULL, "
            + "CLAIM_TOKEN = NULL, CLAIMED_BY = NULL, LAST_ERROR = NULL, UPDATED_AT = #{now} "
            + "WHERE ID = #{id} AND STATUS = 'SENDING' AND CLAIM_TOKEN = #{claimToken} "
            + "AND AGGREGATE_ID = #{aggregateId} AND AGGREGATE_VERSION = #{aggregateVersion} "
            + "AND EXISTS (SELECT 1 FROM CUSTOM_API_TASK T WHERE T.TASK_ID = #{aggregateId} "
            + "AND NVL(T.CUSTOMS_AI_RUN_NO, 1) = #{aggregateVersion})")
    int markSent(@Param("id") Long id,
                 @Param("claimToken") String claimToken,
                 @Param("aggregateId") String aggregateId,
                 @Param("aggregateVersion") Integer aggregateVersion,
                 @Param("now") LocalDateTime now);

    @Update("UPDATE CUSTOM_MQ_OUTBOX SET STATUS = #{status}, ATTEMPT_COUNT = #{attemptCount}, "
            + "NEXT_ATTEMPT_AT = #{nextAttemptAt}, CLAIMED_AT = NULL, CLAIM_TOKEN = NULL, "
            + "CLAIMED_BY = NULL, LAST_ERROR = #{lastError}, UPDATED_AT = #{now} "
            + "WHERE ID = #{id} AND STATUS = 'SENDING' AND CLAIM_TOKEN = #{claimToken}")
    int reschedule(@Param("id") Long id,
                   @Param("claimToken") String claimToken,
                   @Param("status") String status,
                   @Param("attemptCount") int attemptCount,
                   @Param("nextAttemptAt") LocalDateTime nextAttemptAt,
                   @Param("lastError") String lastError,
                   @Param("now") LocalDateTime now);

    @Update("UPDATE CUSTOM_MQ_OUTBOX SET STATUS = 'PENDING', CLAIMED_AT = NULL, "
            + "CLAIM_TOKEN = NULL, CLAIMED_BY = NULL, "
            + "NEXT_ATTEMPT_AT = #{now}, LAST_ERROR = 'stale publisher claim', UPDATED_AT = #{now} "
            + "WHERE STATUS = 'SENDING' AND CLAIMED_AT < #{cutoff}")
    int releaseStaleClaims(@Param("cutoff") LocalDateTime cutoff, @Param("now") LocalDateTime now);

    @Update("UPDATE CUSTOM_MQ_OUTBOX SET STATUS = 'PENDING', ATTEMPT_COUNT = 0, "
            + "NEXT_ATTEMPT_AT = #{now}, CLAIMED_AT = NULL, CLAIM_TOKEN = NULL, CLAIMED_BY = NULL, "
            + "SENT_AT = NULL, LAST_ERROR = NULL, UPDATED_AT = #{now} "
            + "WHERE ID = #{id} AND STATUS = 'DEAD'")
    int replayDead(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Update("UPDATE CUSTOM_MQ_OUTBOX SET STATUS = 'REPLAYED', CLAIMED_AT = NULL, "
            + "CLAIM_TOKEN = NULL, CLAIMED_BY = NULL, NEXT_ATTEMPT_AT = NULL, "
            + "LAST_ERROR = 'replayed as run ' || #{newRunNo}, UPDATED_AT = #{now} "
            + "WHERE ID = #{id} AND STATUS = 'DEAD'")
    int markReplayed(@Param("id") Long id,
                     @Param("newRunNo") int newRunNo,
                     @Param("now") LocalDateTime now);
}
