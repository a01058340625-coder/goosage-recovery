package com.goosage.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.api.view.recovery.RecoveryEventRequest;
import com.goosage.app.RecoveryEventService;
import com.goosage.auth.SessionConst;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
public class RecoveryEventController {

    private final RecoveryEventService recoveryEventService;

    public RecoveryEventController(RecoveryEventService recoveryEventService) {
        this.recoveryEventService = recoveryEventService;
    }

    @PostMapping("/recovery/events")
    public ApiResponse<Void> record(@RequestBody RecoveryEventRequest req, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) {
            return ApiResponse.fail("로그인이 필요합니다");
        }

        recoveryEventService.record(userId, req.type(), req.knowledgeId());
        return ApiResponse.ok(null);
    }
}