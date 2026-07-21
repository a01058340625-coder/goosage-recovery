package com.goosage.api.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.api.view.recovery.message.RecoveryMessageAnalyzeRequest;
import com.goosage.api.view.recovery.message.RecoveryMessageAnalyzeResponse;
import com.goosage.app.recovery.message.RecoveryMessageAnalysisService;
import com.goosage.app.recovery.message.RecoveryMessageProjection;
import com.goosage.auth.SessionConst;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
public class RecoveryMessageController {

    private final RecoveryMessageAnalysisService analysisService;

    public RecoveryMessageController(
            RecoveryMessageAnalysisService analysisService
    ) {
        this.analysisService = analysisService;
    }

    @PostMapping("/recovery/message/analyze")
    public ApiResponse<RecoveryMessageAnalyzeResponse> analyze(
            @RequestBody(required = false) RecoveryMessageAnalyzeRequest request,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute(
                SessionConst.LOGIN_USER_ID
        );

        if (userId == null) {
            return ApiResponse.fail("로그인이 필요합니다");
        }

        if (request == null) {
            return ApiResponse.fail("요청 본문이 필요합니다");
        }

        RecoveryMessageProjection projection =
                analysisService.analyze(
                        userId,
                        request.message(),
                        LocalDate.now(),
                        LocalDateTime.now()
                );

        return ApiResponse.ok(
                RecoveryMessageAnalyzeResponse.from(projection)
        );
    }
}