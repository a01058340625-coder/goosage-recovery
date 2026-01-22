package com.goosage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.service.StudyStreakService;

import jakarta.servlet.http.HttpSession;

@RestController
public class StudyStreakController {

    private final StudyStreakService studyStreakService;

    public StudyStreakController(StudyStreakService studyStreakService) {
        this.studyStreakService = studyStreakService;
    }

    @GetMapping("/study/streak")
    public ApiResponse<Integer> streak(HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) {
            return ApiResponse.fail("로그인이 필요합니다");
        }

        int streak = studyStreakService.getStreak(userId);
        return ApiResponse.ok(streak);
    }
}
