package org.jeecg.modules.custom.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomCallbackDelivery;

import java.time.Duration;
import java.util.List;

public interface ICustomCallbackDeliveryService extends IService<CustomCallbackDelivery> {
    CustomCallbackDelivery enqueueTerminal(CustomApiTask task, String eventType, Object result,
                                            String errorCode, String errorMessage);

    List<CustomCallbackDelivery> findDue(int limit);

    boolean claim(Long id);

    void markSucceeded(CustomCallbackDelivery delivery, int httpStatus);

    void scheduleRetry(CustomCallbackDelivery delivery, Integer httpStatus,
                       String error, Duration delay);

    void markPermanentFailure(CustomCallbackDelivery delivery, Integer httpStatus, String error);

    void releaseStaleClaims(long claimTimeoutSeconds);
}
