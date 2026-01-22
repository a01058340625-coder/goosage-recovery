package com.goosage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.service.StudyTodayResult;
import com.goosage.service.StudyTodayService;

import jakarta.servlet.http.HttpSession;

@RestController
public class StudyController {

    private final StudyTodayService studyTodayService;

    public StudyController(StudyTodayService studyTodayService) {
        this.studyTodayService = studyTodayService;
    }

    @GetMapping("/study/today")
    public ApiResponse<StudyTodayResult> today(HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) {
            return ApiResponse.fail("로그인이 필요합니다");
        }

        StudyTodayResult result = studyTodayService.getToday(userId);
        return ApiResponse.ok(result);
    }
}
