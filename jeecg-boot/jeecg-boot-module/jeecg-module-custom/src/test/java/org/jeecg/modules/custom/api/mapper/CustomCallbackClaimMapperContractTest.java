package org.jeecg.modules.custom.api.mapper;

import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.custom.api.entity.CustomCallbackDelivery;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustomCallbackClaimMapperContractTest {

    @Test
    void callbackTransitionsAndTaskStatusAreFencedByClaimTokenAndRun() throws Exception {
        assertThat(CustomCallbackDelivery.class.getMethod("getClaimToken")).isNotNull();
        assertThat(CustomCallbackDelivery.class.getMethod("getClaimedBy")).isNotNull();

        Method claim = CustomCallbackDeliveryMapper.class.getMethod(
                "claim", Long.class, String.class, String.class, LocalDateTime.class);
        assertTokenCas(claim);
        assertThat(normalized(claim.getAnnotation(Update.class)))
                .contains("EXISTS (SELECT 1 FROM CUSTOM_API_TASK")
                .contains("CUSTOM_API_TASK.TASK_ID = CUSTOM_CALLBACK_DELIVERY.TASK_ID")
                .contains("CUSTOM_API_TASK.CUSTOMS_AI_RUN_NO")
                .contains("CUSTOM_CALLBACK_DELIVERY.RUN_NO")
                .contains("CUSTOM_API_TASK.STATUS IN ('SUCCEEDED', 'FAILED', 'TIMEOUT', 'CANCELLED')");
        assertTokenCas(CustomCallbackDeliveryMapper.class.getMethod(
                "markSucceeded", Long.class, String.class, int.class, LocalDateTime.class));
        assertTokenCas(CustomCallbackDeliveryMapper.class.getMethod(
                "scheduleRetry", Long.class, String.class, int.class, LocalDateTime.class,
                Integer.class, String.class, LocalDateTime.class));
        assertTokenCas(CustomCallbackDeliveryMapper.class.getMethod(
                "markPermanentFailure", Long.class, String.class, int.class,
                Integer.class, String.class, LocalDateTime.class));

        Method taskUpdate = CustomApiTaskMapper.class.getMethod(
                "updateCallbackStatus", String.class, Integer.class, String.class, String.class);
        String taskSql = normalized(taskUpdate.getAnnotation(Update.class));
        assertThat(taskSql)
                .contains("TASK_ID = #{TASKID}")
                .contains("CUSTOMS_AI_RUN_NO")
                .contains("#{RUNNO}");
    }

    private void assertTokenCas(Method method) {
        String sql = normalized(method.getAnnotation(Update.class));
        assertThat(sql)
                .contains("CLAIM_TOKEN")
                .contains("#{CLAIMTOKEN}");
    }

    private String normalized(Update update) {
        assertThat(update).isNotNull();
        return String.join(" ", update.value()).replaceAll("\\s+", " ").toUpperCase();
    }
}
