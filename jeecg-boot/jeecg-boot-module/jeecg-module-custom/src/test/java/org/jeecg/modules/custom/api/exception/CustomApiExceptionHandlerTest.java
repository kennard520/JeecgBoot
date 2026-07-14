package org.jeecg.modules.custom.api.exception;

import org.jeecg.common.api.vo.Result;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class CustomApiExceptionHandlerTest {

    @Test
    void hiddenCrossCustomerResourceReturnsHttp404() {
        CustomApiExceptionHandler handler = new CustomApiExceptionHandler();

        ResponseEntity<Result<?>> response = handler.notFound(
                new CustomApiNotFoundException("task not found"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody().getMessage()).isEqualTo("task not found");
    }
}
