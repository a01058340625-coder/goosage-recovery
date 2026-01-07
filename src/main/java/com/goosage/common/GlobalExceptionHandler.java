package com.goosage.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ✅ 예외가 터져도 "항상 같은 JSON(ApiResponse)"으로 내려주기
 * - 지금은 ApiResponse.fail(message) 형태로 통일
 * - 나중에 error code, traceId 등을 추가하고 싶으면 여기서 확장하면 됨
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ✅ 404: 리소스가 없을 때
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException e) {
        // ApiResponse.fail은 data=null 형태로 내려감
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getMessage()));
    }

    /**
     * ✅ 400: 요청 값이 이상할 때(필요하면 서비스/컨트롤러에서 IllegalArgumentException 던짐)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getMessage()));
    }

    /**
     * ✅ 500: 나머지(진짜 서버 오류)
     * - 개발 중에는 message를 내려줘도 되지만,
     * - 운영에서는 "Internal Server Error"로 숨기는 편이 안전
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleServerError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("Internal Server Error"));
    }
}
