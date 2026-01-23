package com.goosage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.common.UnauthorizedException;
import com.goosage.service.study.StudyCoachService;
import com.goosage.service.study.dto.StudyCoachResponse;

import jakarta.servlet.http.HttpSession;

@RestController
public class StudyCoachController {

    private final StudyCoachService studyCoachService;

    public StudyCoachController(StudyCoachService studyCoachService) {
        this.studyCoachService = studyCoachService;
    }

    @GetMapping("/study/coach")
    public ApiResponse<StudyCoachResponse> coach(HttpSession session) {

        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) {
            throw new UnauthorizedException("UNAUTHORIZED");
        }

        return ApiResponse.ok(studyCoachService.coach(userId));
    }
}
