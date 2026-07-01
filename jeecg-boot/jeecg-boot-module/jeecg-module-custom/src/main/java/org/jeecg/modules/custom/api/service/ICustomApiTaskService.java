package org.jeecg.modules.custom.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.custom.api.entity.CustomApiApp;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.vo.TaskCreateRequest;
import org.jeecg.modules.custom.api.vo.TaskResponse;
import org.jeecg.modules.custom.api.vo.TaskResultResponse;

import java.util.Map;

public interface ICustomApiTaskService extends IService<CustomApiTask> {
    TaskResponse createTask(CustomApiApp app, TaskCreateRequest request);

    TaskResponse getTask(CustomApiApp app, String taskId);

    TaskResultResponse getResult(CustomApiApp app, String taskId);

    void handleParseResult(Map<String, Object> message);
}
