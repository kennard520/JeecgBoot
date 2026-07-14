package org.jeecg.modules.custom.api.exception;

import org.jeecg.common.api.vo.Result;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "org.jeecg.modules.custom.api.controller")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomApiExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<?>> malformedJson(HttpMessageNotReadableException error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(HttpStatus.BAD_REQUEST.value(), "malformed JSON request"));
    }

    @ExceptionHandler(CustomApiUnauthorizedException.class)
    public ResponseEntity<Result<?>> unauthorized(CustomApiUnauthorizedException error) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(HttpStatus.UNAUTHORIZED.value(), error.getMessage()));
    }

    @ExceptionHandler(CustomApiNotFoundException.class)
    public ResponseEntity<Result<?>> notFound(CustomApiNotFoundException error) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.error(HttpStatus.NOT_FOUND.value(), error.getMessage()));
    }

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
