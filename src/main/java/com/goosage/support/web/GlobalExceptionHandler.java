package com.goosage.support.web;

import java.util.NoSuchElementException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 401 UNAUTHORIZED
     * - 로그인 안 됐을 때
     * - 세션 없음
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(e.getMessage()));
    }

    /**
     * 404 NOT FOUND
     * - 존재하지 않는 리소스
     * - knowledgeId 없음 등
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoSuchElementException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getMessage()));
    }

    /**
     * 409 CONFLICT
     * - source + sourceId 중복
     * - 유니크 제약조건 충돌
     */
    @ExceptionHandler({
            DuplicateKeyException.class,
            ConflictException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleConflict(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(e.getMessage()));
    }

    /**
     * 500 INTERNAL SERVER ERROR
     * - 진짜 서버 오류만 여기로
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleServerError(Exception e) {
        e.printStackTrace(); // 로그용 (운영에선 logger로)
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("INTERNAL_SERVER_ERROR"));
    }
}
