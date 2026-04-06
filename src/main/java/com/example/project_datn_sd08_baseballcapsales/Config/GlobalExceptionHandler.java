package com.example.project_datn_sd08_baseballcapsales.Config;

import com.example.project_datn_sd08_baseballcapsales.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Yêu cầu không hợp lệ";
        return new ResponseEntity<>(ApiResponse.error(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Đã xảy ra lỗi";
        return new ResponseEntity<>(ApiResponse.error(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex, WebRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Đã xảy ra lỗi không xác định";
        return new ResponseEntity<>(ApiResponse.error(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
