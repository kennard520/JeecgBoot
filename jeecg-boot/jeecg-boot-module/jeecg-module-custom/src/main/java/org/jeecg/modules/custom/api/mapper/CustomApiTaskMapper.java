package org.jeecg.modules.custom.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.custom.api.entity.CustomApiTask;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomApiTaskMapper extends BaseMapper<CustomApiTask> {

    @Select("SELECT * FROM CUSTOM_API_TASK WHERE TASK_ID = #{taskId} FOR UPDATE")
    CustomApiTask selectByTaskIdForUpdate(@Param("taskId") String taskId);

    @Select("""
            SELECT TASK_ID
            FROM (
                SELECT ID, TASK_ID
                FROM CUSTOM_API_TASK
                WHERE STATUS IN ('queued', 'running')
                  AND (
                    CREATED_AT < #{totalCutoff}
                    OR (STATUS = 'queued'
                        AND NVL(QUEUED_AT, CREATED_AT) < #{queuedCutoff})
                    OR (STATUS = 'running'
                        AND CREATED_AT < #{heartbeatCutoff}
                        AND (LAST_HEARTBEAT_AT IS NULL
                            OR LAST_HEARTBEAT_AT < #{heartbeatCutoff})
                        AND (STARTED_AT IS NULL OR STARTED_AT < #{heartbeatCutoff})
                        AND (QUEUED_AT IS NULL OR QUEUED_AT < #{heartbeatCutoff}))
                  )
                ORDER BY ID
            )
            WHERE ROWNUM <= #{limit}
            """)
    List<String> selectStaleCandidateTaskIds(
            @Param("totalCutoff") LocalDateTime totalCutoff,
            @Param("queuedCutoff") LocalDateTime queuedCutoff,
            @Param("heartbeatCutoff") LocalDateTime heartbeatCutoff,
            @Param("limit") int limit);
}
