package com.goosage.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.study.StudyCoachService;
import com.goosage.auth.SessionConst;
import com.goosage.domain.study.StudyCoachResult;
import com.goosage.support.web.ApiResponse;
import com.goosage.support.web.UnauthorizedException;

import jakarta.servlet.http.HttpSession;

@RestController
public class StudyCoachController {

    private final StudyCoachService studyCoachService;

    public StudyCoachController(StudyCoachService studyCoachService) {
        this.studyCoachService = studyCoachService;
    }

    @GetMapping("/study/coach")
    public ApiResponse<StudyCoachResult> coach(HttpSession session) {

        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) throw new UnauthorizedException("UNAUTHORIZED");

        System.out.println("[COACH-ENTRY] user=" + userId);

        StudyCoachResult result = studyCoachService.coach(userId);
        return ApiResponse.ok(result);
    }
}
