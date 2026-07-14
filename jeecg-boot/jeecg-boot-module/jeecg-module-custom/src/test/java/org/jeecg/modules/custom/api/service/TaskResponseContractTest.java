package org.jeecg.modules.custom.api.service;

import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.service.impl.CustomApiTaskServiceImpl;
import org.jeecg.modules.custom.api.vo.TaskResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class TaskResponseContractTest {

    @Test
    void taskStatusResponseIncludesCallbackDeliveryState() throws Exception {
        CustomApiTask task = new CustomApiTask()
                .setTaskId("task-1")
                .setCallbackStatus("success")
                .setCallbackError(null);
        Method mapper = CustomApiTaskServiceImpl.class
                .getDeclaredMethod("toResponse", CustomApiTask.class);
        mapper.setAccessible(true);

        TaskResponse response = (TaskResponse) mapper.invoke(new CustomApiTaskServiceImpl(), task);

        assertThat(response.getCallbackStatus()).isEqualTo("success");
        assertThat(response.getCallbackError()).isNull();
    }
}
