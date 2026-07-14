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

    String claim(Long id, String claimedBy);

    void markSucceeded(CustomCallbackDelivery delivery, String claimToken, int httpStatus);

    void scheduleRetry(CustomCallbackDelivery delivery, String claimToken, Integer httpStatus,
                       String error, Duration delay);

    void markPermanentFailure(CustomCallbackDelivery delivery, String claimToken,
                              Integer httpStatus, String error);

    void releaseStaleClaims(long claimTimeoutSeconds);

    CustomCallbackDelivery replayDead(String deliveryId);
}
