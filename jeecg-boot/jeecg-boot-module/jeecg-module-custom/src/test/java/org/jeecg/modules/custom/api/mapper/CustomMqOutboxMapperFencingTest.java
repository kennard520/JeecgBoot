package org.jeecg.modules.custom.api.mapper;

import org.apache.ibatis.annotations.Update;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustomMqOutboxMapperFencingTest {

    @Test
    void payloadRefreshAndSentTransitionUseClaimAndCurrentAggregateVersion() throws Exception {
        Method refresh = CustomMqOutboxMapper.class.getMethod(
                "refreshPayload", Long.class, String.class, String.class, LocalDateTime.class);
        assertThat(sql(refresh)).contains("CLAIM_TOKEN = #{CLAIMTOKEN}");

        Method sent = CustomMqOutboxMapper.class.getMethod(
                "markSent", Long.class, String.class, String.class, Integer.class,
                LocalDateTime.class);
        assertThat(sql(sent))
                .contains("CLAIM_TOKEN = #{CLAIMTOKEN}")
                .contains("AGGREGATE_ID = #{AGGREGATEID}")
                .contains("AGGREGATE_VERSION = #{AGGREGATEVERSION}")
                .contains("CUSTOM_API_TASK")
                .contains("CUSTOMS_AI_RUN_NO");
    }

    private String sql(Method method) {
        Update update = method.getAnnotation(Update.class);
        assertThat(update).isNotNull();
        return String.join(" ", update.value()).replaceAll("\\s+", " ").toUpperCase();
    }
}
