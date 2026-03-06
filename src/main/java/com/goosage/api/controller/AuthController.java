package com.goosage.api.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.AuthService;
import com.goosage.auth.SessionConst;
import com.goosage.entity.User;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ApiResponse<Map<String, Object>> signup(@RequestBody Map<String, String> body) {
        User user = authService.signup(body.get("email"), body.get("password"));
        return ApiResponse.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail()
        ));
    }

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

    private long requireUserId(HttpSession session) {
        Object v = session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (v == null) throw new IllegalArgumentException("UNAUTHORIZED");
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        throw new IllegalArgumentException("UNAUTHORIZED");
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me(HttpSession session) {
        long userId = requireUserId(session);
        User user = authService.mustFindById(userId);
        return ApiResponse.ok(Map.of("id", user.getId(), "email", user.getEmail()));
    }
}