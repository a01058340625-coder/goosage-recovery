package com.goosage.api.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.infra.dao.admin.StudyEventsAdminDao;
import com.goosage.infra.dao.admin.StudyEventsAdminDao.StudyEventRow;
import com.goosage.support.web.ApiResponse;

@RestController
public class AdminObservabilityController {

    private final StudyEventsAdminDao dao;

    public AdminObservabilityController(StudyEventsAdminDao dao) {
        this.dao = dao;
    }

    @GetMapping("/admin/study-events/recent")
    public ApiResponse<List<StudyEventRow>> recent(
            @RequestParam long userId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ApiResponse.ok(dao.recent(userId, limit));
    }
}