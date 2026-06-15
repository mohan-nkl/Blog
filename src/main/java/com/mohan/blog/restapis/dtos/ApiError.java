package com.mohan.blog.restapis.dtos;

import org.springframework.http.HttpStatus;

import java.util.Map;

public record ApiError(int status, String error, String message, Map<String, String> fieldErrors) {

    public static ApiError of(HttpStatus status, String message) {
        return new ApiError(status.value(), status.getReasonPhrase(), message, null);
    }

    public static ApiError validation(Map<String, String> fieldErrors) {
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                fieldErrors);
    }
}