package com.example.todolist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(LoginRequiredException.class)
    public ResponseEntity<ErrorResponse> handleLoginRequired(LoginRequiredException exception) {
        ErrorResponse response = new ErrorResponse(
                "LOGIN_REQUIRED",
                exception.getMessage(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    public record ErrorResponse(String code, String message, Instant timestamp) {
    }
}