package org.jeecg.modules.custom.api.config;

import org.jeecg.modules.custom.api.callback.CustomCallbackPublisher;
import org.jeecg.modules.custom.api.mq.CustomMqOutboxPublisher;
import org.jeecg.modules.custom.api.reconcile.CustomTaskReconciler;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import static org.assertj.core.api.Assertions.assertThat;

class CustomReliabilitySchedulingConfigTest {

    @Test
    void enablesSchedulingWithSeparateReliabilityAndCallbackPools() throws Exception {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(CustomReliabilitySchedulingConfig.class)) {
            assertThat(context.containsBean(
                    "org.springframework.context.annotation.internalScheduledAnnotationProcessor"))
                    .isTrue();
            ThreadPoolTaskScheduler reliability = context.getBean(
                    "customReliabilityTaskScheduler", ThreadPoolTaskScheduler.class);
            ThreadPoolTaskScheduler callback = context.getBean(
                    "customCallbackTaskScheduler", ThreadPoolTaskScheduler.class);

            assertThat(reliability).isNotSameAs(callback);
            assertThat(reliability.getScheduledThreadPoolExecutor().getCorePoolSize())
                    .isGreaterThanOrEqualTo(2);
            assertThat(callback.getScheduledThreadPoolExecutor().getCorePoolSize())
                    .isGreaterThanOrEqualTo(2);
        }

        assertScheduler(CustomMqOutboxPublisher.class, "publishPending",
                "customReliabilityTaskScheduler");
        assertScheduler(CustomTaskReconciler.class, "reconcileStaleTasks",
                "customReliabilityTaskScheduler");
        assertScheduler(CustomCallbackPublisher.class, "publishPending",
                "customCallbackTaskScheduler");
    }

    private void assertScheduler(Class<?> type, String method, String expected) throws Exception {
        Scheduled scheduled = type.getMethod(method).getAnnotation(Scheduled.class);
        assertThat(scheduled).isNotNull();
        assertThat(scheduled.scheduler()).isEqualTo(expected);
    }
}
