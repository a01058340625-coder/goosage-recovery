package com.goosage.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.common.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest req, HttpSession session) {
        AuthResponse res = authService.login(req);

        // ✅ 핵심 1줄(로그인 성공하면 세션에 저장)
        session.setAttribute(SessionConst.LOGIN_USER_ID, res.userId());

        return ApiResponse.ok(res);
    }
}
