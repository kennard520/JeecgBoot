package org.jeecg.modules.custom.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomMqOutbox;

import java.util.List;

public interface ICustomMqOutboxService extends IService<CustomMqOutbox> {
    CustomMqOutbox enqueueParseTask(CustomApiTask task, CustomApiFile file, int runNo);

    List<CustomMqOutbox> findPublishable(int limit);

    boolean claim(Long id);

    void markSent(Long id);

    void reschedule(CustomMqOutbox event, String error, int maxAttempts,
                    long baseDelaySeconds, long maxDelaySeconds);

    void releaseStaleClaims(long claimTimeoutSeconds);
}
