package com.goosage.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.RecoveryDebugService;
import com.goosage.auth.SessionConst;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
public class RecoveryDebugController {

    private final RecoveryDebugService studyDebugService;

    public RecoveryDebugController(RecoveryDebugService studyDebugService) {
        this.studyDebugService = studyDebugService;
    }

    @PostMapping("/study/debug/ping")
    public ApiResponse<String> ping(HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        studyDebugService.recordPing(userId);
        return ApiResponse.ok("pong");
    }
}
