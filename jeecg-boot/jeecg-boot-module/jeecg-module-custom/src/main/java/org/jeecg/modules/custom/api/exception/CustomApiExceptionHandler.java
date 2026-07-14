package org.jeecg.modules.custom.api.exception;

import org.jeecg.common.api.vo.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "org.jeecg.modules.custom.api.controller")
public class CustomApiExceptionHandler {

    @ExceptionHandler(CustomApiConflictException.class)
    public ResponseEntity<Result<?>> conflict(CustomApiConflictException error) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Result.error(HttpStatus.CONFLICT.value(), error.getMessage()));
    }

    @ExceptionHandler(CustomApiRateLimitException.class)
    public ResponseEntity<Result<?>> rateLimit(CustomApiRateLimitException error) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(error.getRetryAfterSeconds()))
                .body(Result.error(HttpStatus.TOO_MANY_REQUESTS.value(), error.getMessage()));
    }
}
