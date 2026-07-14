package org.jeecg.modules.custom.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.custom.api.entity.CustomApiTask;

public interface CustomApiTaskMapper extends BaseMapper<CustomApiTask> {

    @Select("SELECT * FROM CUSTOM_API_TASK WHERE TASK_ID = #{taskId} FOR UPDATE")
    CustomApiTask selectByTaskIdForUpdate(@Param("taskId") String taskId);
}
