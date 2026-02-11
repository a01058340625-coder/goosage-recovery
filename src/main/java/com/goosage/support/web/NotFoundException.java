package com.goosage.support.web;

/**
 * ✅ 리소스가 존재하지 않을 때(404) 쓰는 예외
 * - GET /posts/999 같은 상황은 서버 오류(500)가 아니라 "없음(404)"이 맞다.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
