package org.jeecg.modules.custom.api.mapper;

import org.apache.ibatis.annotations.Select;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustomApiTaskMapperReconcileQueryTest {

    @Test
    void stalePredicatesAreAppliedBeforeTheBatchLimit() throws Exception {
        Method method = CustomApiTaskMapper.class.getMethod(
                "selectStaleCandidateTaskIds",
                LocalDateTime.class, LocalDateTime.class, LocalDateTime.class, int.class);
        Select select = method.getAnnotation(Select.class);
        String sql = String.join(" ", select.value())
                .replaceAll("\\s+", " ")
                .toUpperCase();

        assertThat(sql)
                .contains("CREATED_AT < #{TOTALCUTOFF}".toUpperCase())
                .contains("STATUS = 'QUEUED'")
                .contains("QUEUED_AT")
                .contains("STATUS = 'RUNNING'")
                .contains("LAST_HEARTBEAT_AT")
                .contains("LAST_HEARTBEAT_AT IS NULL OR LAST_HEARTBEAT_AT < #{HEARTBEATCUTOFF}".toUpperCase())
                .contains("STARTED_AT IS NULL OR STARTED_AT < #{HEARTBEATCUTOFF}".toUpperCase())
                .contains("QUEUED_AT IS NULL OR QUEUED_AT < #{HEARTBEATCUTOFF}".toUpperCase())
                .contains("ROWNUM <= #{LIMIT}".toUpperCase());
        assertThat(sql.indexOf("CREATED_AT < #{TOTALCUTOFF}".toUpperCase()))
                .isLessThan(sql.indexOf("ROWNUM <= #{LIMIT}".toUpperCase()));
    }
}
