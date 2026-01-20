package com.goosage.common;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.goosage.common.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// ✅ 중복(UK violation / idempotent) → 409 Conflict
	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ApiResponse<?> handleConflict(IllegalStateException e) {
	    String msg = e.getMessage();

	    // 우리가 만든 규칙: "DUPLICATE_SOURCE_SOURCEID:<id>"
	    if (msg != null && msg.startsWith("DUPLICATE_SOURCE_SOURCEID:")) {
	        String id = msg.substring("DUPLICATE_SOURCE_SOURCEID:".length());
	        return ApiResponse.fail("Already exists. id=" + id);
	    }

	    return ApiResponse.fail(msg != null ? msg : "Conflict");
	}

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleNotFound(NoSuchElementException e) {
        return ApiResponse.fail(e.getMessage());
    }

    // ✅ 그 외는 진짜 500 (최소한 로그는 남겨야 함)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleServerError(Exception e) {
        // 과제용이면 여기서 e.printStackTrace() 정도는 허용
        e.printStackTrace();
        return ApiResponse.fail("Internal Server Error");
    }
}
