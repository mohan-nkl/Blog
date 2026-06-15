package com.mohan.blog.restapis;

import com.mohan.blog.restapis.dtos.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.mohan.blog.restapis")
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleNotFound(IllegalArgumentException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleForbidden(AccessDeniedException exception) {
        return build(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException exception) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(ApiError.validation(fieldErrors));
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiError.of(status, message));
    }
}