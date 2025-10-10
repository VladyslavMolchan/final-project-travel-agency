package com.epam.finaltask.exception;


import com.epam.finaltask.restcontroller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(
                new ApiResponse<>("VALIDATION_ERROR", "Validation failed", errors)
        );
    }

    @ExceptionHandler(PasswordsDoNotMatchException.class)
    public ResponseEntity<ApiResponse<String>> handlePasswordsMismatch(PasswordsDoNotMatchException ex) {
        log.warn("Passwords mismatch: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiResponse<>("PASSWORD_MISMATCH", ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidRole(InvalidRoleException ex) {
        log.warn("Invalid role: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiResponse<>("INVALID_ROLE", ex.getMessage(), null));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleUserExists(UserAlreadyExistsException ex) {
        log.warn("User already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("USER_EXISTS", ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidLogin(InvalidCredentialsException ex) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("INVALID_CREDENTIALS", ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidOrExpiredTokenException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidOrExpiredToken(InvalidOrExpiredTokenException ex) {
        log.warn("Invalid or expired token: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiResponse<>("INVALID_OR_EXPIRED_TOKEN", ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidRefresh(InvalidRefreshTokenException ex) {
        log.warn("Invalid refresh token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("INVALID_REFRESH_TOKEN", ex.getMessage(), null));
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntime(RuntimeException ex) {
        log.error("Unexpected runtime exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("ERROR", "Internal server error", null));
    }
}
