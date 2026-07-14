package org.jeecg.modules.custom.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class CustomReliabilitySchedulingConfig {

    @Bean(name = "customReliabilityTaskScheduler", destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler customReliabilityTaskScheduler(
            @Value("${custom.api.scheduling.reliability-pool-size:2}") int poolSize) {
        return scheduler("custom-reliability-", poolSize);
    }

    @Bean(name = "customCallbackTaskScheduler", destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler customCallbackTaskScheduler(
            @Value("${custom.api.scheduling.callback-pool-size:4}") int poolSize) {
        return scheduler("custom-callback-", poolSize);
    }

    private ThreadPoolTaskScheduler scheduler(String prefix, int poolSize) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Math.max(2, poolSize));
        scheduler.setThreadNamePrefix(prefix);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }
}
