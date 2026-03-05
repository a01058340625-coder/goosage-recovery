package com.goosage.academy.report;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/academy/courses")
public class AcademyReportController {

    private final AcademyReportService service;

    public AcademyReportController(AcademyReportService service) {
        this.service = service;
    }

    @GetMapping("/{courseId}/report/users")
    public ApiResponse<List<CourseUserReportResponse>> reportUsers(
            @PathVariable long courseId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) throw new RuntimeException("UNAUTHORIZED");
        return ApiResponse.ok(service.getCourseUserReports(userId, courseId));
    }

}
