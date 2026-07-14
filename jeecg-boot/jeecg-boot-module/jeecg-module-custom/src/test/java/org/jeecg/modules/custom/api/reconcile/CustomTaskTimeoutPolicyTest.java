package org.jeecg.modules.custom.api.reconcile;

import org.junit.jupiter.api.Test;
import org.jeecg.modules.custom.api.security.InternalDownloadTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CustomTaskTimeoutPolicyTest {

    @Test
    void serviceAndScannerDefaultToTheFourHourMqWindow() {
        assertValueDefault(InternalDownloadTokenService.class, 2,
                "${custom.api.internal-download-ttl-seconds:18000}");
        assertValueDefault(InternalDownloadTokenService.class, 3,
                "${custom.api.reconcile.total-timeout-seconds:14400}");
        assertTimeoutDefault(CustomTaskReconcileService.class, 7);
        assertTimeoutDefault(CustomTaskReconciler.class, 5);
    }

    private void assertTimeoutDefault(Class<?> type, int parameterIndex) {
        assertValueDefault(type, parameterIndex,
                "${custom.api.reconcile.total-timeout-seconds:14400}");
    }

    private void assertValueDefault(Class<?> type, int parameterIndex, String expected) {
        Constructor<?> constructor = Arrays.stream(type.getConstructors())
                .filter(candidate -> candidate.isAnnotationPresent(Autowired.class))
                .findFirst()
                .orElseThrow();
        Parameter parameter = constructor.getParameters()[parameterIndex];
        Value value = parameter.getAnnotation(Value.class);

        assertThat(value).isNotNull();
        assertThat(value.value()).isEqualTo(expected);
    }
}
