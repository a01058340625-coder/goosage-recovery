package com.goosage.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.dao.StudyEventDao;

import jakarta.servlet.http.HttpSession;

@RestController
public class StudyDebugController {

    private final StudyEventDao studyEventDao;

    public StudyDebugController(StudyEventDao studyEventDao) {
        this.studyEventDao = studyEventDao;
    }

    // ✅ 로그인 상태에서만 호출 가능(너 필터 규칙상 POST는 보호)
    @PostMapping("/study/debug/ping")
    public ApiResponse<String> ping(HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        studyEventDao.recordEvent(userId, "PING", null, null, null);
        return ApiResponse.ok("pong");
    }
}
