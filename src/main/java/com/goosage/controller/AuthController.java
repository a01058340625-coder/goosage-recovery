package com.goosage.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.entity.User;
import com.goosage.service.AuthService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ApiResponse<Map<String, Object>> signup(@RequestBody Map<String, String> body) {
        User user = authService.signup(body.get("email"), body.get("password"));
        return ApiResponse.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail()
        ));
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(
            @RequestBody Map<String, String> body,
            HttpSession session
    ) {
        User user = authService.login(body.get("email"), body.get("password"));
        session.setAttribute(SessionConst.LOGIN_USER_ID, user.getId());

        return ApiResponse.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail()
        ));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.ok("LOGOUT_OK", null);
    }

    /**
     * ✅ 현재 로그인 사용자 조회 (/auth/me)
     */
    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me(HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);

        if (userId == null) {
            throw new IllegalArgumentException("UNAUTHORIZED");
        }

        User user = authService.mustFindById(userId);

        return ApiResponse.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail()
        ));
    }
}
