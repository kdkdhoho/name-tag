package com.nametag.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ApiResponse<Void>> invalidRequest(IllegalArgumentException exception) {
    return ResponseEntity.badRequest()
        .body(ApiResponse.fail(400, "INVALID_REQUEST", exception.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  ResponseEntity<ApiResponse<Void>> upstreamFailure(IllegalStateException exception) {
    return ResponseEntity.status(502)
        .body(ApiResponse.fail(502, "UPSTREAM_UNAVAILABLE", exception.getMessage()));
  }
}
