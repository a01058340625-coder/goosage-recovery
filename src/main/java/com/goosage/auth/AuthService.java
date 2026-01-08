package com.goosage.auth;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public AuthResponse login(LoginRequest req) {
        // TODO: 다음 스텝에서 DB(users) 조회 + BCrypt 매칭으로 진짜 로그인 구현

        // ✅ 지금은 "임시 로그인"으로 서버가 뜨게만(=흐름 확인용)
        // 이메일만 오면 userId=1로 로그인 성공 처리(임시)
        if (req.email() == null || req.email().isBlank()) {
            throw new IllegalArgumentException("email required");
        }
        return new AuthResponse(1L, req.email());
    }
}
