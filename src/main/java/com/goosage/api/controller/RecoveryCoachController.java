package com.goosage.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.recovery.RecoveryCoachService;
import com.goosage.auth.SessionConst;
import com.goosage.domain.recovery.RecoveryCoachResult;
import com.goosage.support.web.ApiResponse;
import com.goosage.support.web.UnauthorizedException;

import jakarta.servlet.http.HttpSession;

@RestController
public class RecoveryCoachController {

    private final RecoveryCoachService recoveryCoachService;

    public RecoveryCoachController(RecoveryCoachService recoveryCoachService) {
        this.recoveryCoachService = recoveryCoachService;
    }

    @GetMapping("/recovery/coach")
    public ApiResponse<RecoveryCoachResult> coach(HttpSession session) {

        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) throw new UnauthorizedException("UNAUTHORIZED");

        System.out.println("[COACH-ENTRY] user=" + userId);

        RecoveryCoachResult result = recoveryCoachService.coach(userId);
        return ApiResponse.ok(result);
    }
}