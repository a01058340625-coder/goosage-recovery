package com.goosage.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.goosage.auth.SessionConst;
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

    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody Map<String, String> body) {
        User user = authService.signup(body.get("email"), body.get("password"));
        return Map.of("id", user.getId(), "email", user.getEmail());
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body, HttpSession session) {
        User user = authService.login(body.get("email"), body.get("password"));
        session.setAttribute(SessionConst.LOGIN_USER_ID, user.getId());
        return Map.of("id", user.getId(), "email", user.getEmail());
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        return Map.of("result", "OK");
    }
}
