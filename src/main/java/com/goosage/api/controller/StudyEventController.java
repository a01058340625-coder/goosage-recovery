package com.goosage.api.controller;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import com.goosage.app.StudyEventService;
import com.goosage.auth.SessionConst;
import com.goosage.api.view.study.StudyEventRequest;
import com.goosage.support.web.ApiResponse;


@RestController
public class StudyEventController {

    private final StudyEventService studyEventService;

    public StudyEventController(StudyEventService studyEventService) {
        this.studyEventService = studyEventService;
    }

    @PostMapping("/study/events")
    public ApiResponse<Void> record(@RequestBody StudyEventRequest req, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        studyEventService.record(userId, req.type(), req.knowledgeId());
        return ApiResponse.ok(null);
    }
}
