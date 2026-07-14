package org.jeecg.modules.custom.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.custom.api.entity.CustomCallbackDelivery;

import java.time.LocalDateTime;

public interface CustomCallbackDeliveryMapper extends BaseMapper<CustomCallbackDelivery> {

    @Update("UPDATE CUSTOM_CALLBACK_DELIVERY SET STATUS = 'SENDING', CLAIMED_AT = #{now}, "
            + "LAST_ATTEMPT_AT = #{now}, UPDATED_AT = #{now} WHERE ID = #{id} AND STATUS = 'PENDING' "
            + "AND (NEXT_ATTEMPT_AT IS NULL OR NEXT_ATTEMPT_AT <= #{now})")
    int claim(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Update("UPDATE CUSTOM_CALLBACK_DELIVERY SET STATUS = 'SUCCEEDED', HTTP_STATUS = #{httpStatus}, "
            + "CLAIMED_AT = NULL, DELIVERED_AT = #{now}, LAST_ERROR = NULL, UPDATED_AT = #{now} "
            + "WHERE ID = #{id} AND STATUS = 'SENDING'")
    int markSucceeded(@Param("id") Long id,
                      @Param("httpStatus") int httpStatus,
                      @Param("now") LocalDateTime now);

    @Update("UPDATE CUSTOM_CALLBACK_DELIVERY SET STATUS = 'PENDING', ATTEMPT_COUNT = #{attemptCount}, "
            + "NEXT_ATTEMPT_AT = #{nextAttemptAt}, CLAIMED_AT = NULL, HTTP_STATUS = #{httpStatus}, "
            + "LAST_ERROR = #{lastError}, UPDATED_AT = #{now} WHERE ID = #{id} AND STATUS = 'SENDING'")
    int scheduleRetry(@Param("id") Long id,
                      @Param("attemptCount") int attemptCount,
                      @Param("nextAttemptAt") LocalDateTime nextAttemptAt,
                      @Param("httpStatus") Integer httpStatus,
                      @Param("lastError") String lastError,
                      @Param("now") LocalDateTime now);

    @Update("UPDATE CUSTOM_CALLBACK_DELIVERY SET STATUS = 'DEAD', ATTEMPT_COUNT = #{attemptCount}, "
            + "NEXT_ATTEMPT_AT = NULL, CLAIMED_AT = NULL, HTTP_STATUS = #{httpStatus}, "
            + "LAST_ERROR = #{lastError}, UPDATED_AT = #{now} WHERE ID = #{id} AND STATUS = 'SENDING'")
    int markPermanentFailure(@Param("id") Long id,
                             @Param("attemptCount") int attemptCount,
                             @Param("httpStatus") Integer httpStatus,
                             @Param("lastError") String lastError,
                             @Param("now") LocalDateTime now);

    @Update("UPDATE CUSTOM_CALLBACK_DELIVERY SET STATUS = 'PENDING', CLAIMED_AT = NULL, "
            + "NEXT_ATTEMPT_AT = #{now}, LAST_ERROR = 'stale callback claim', UPDATED_AT = #{now} "
            + "WHERE STATUS = 'SENDING' AND CLAIMED_AT < #{cutoff}")
    int releaseStaleClaims(@Param("cutoff") LocalDateTime cutoff, @Param("now") LocalDateTime now);
}
