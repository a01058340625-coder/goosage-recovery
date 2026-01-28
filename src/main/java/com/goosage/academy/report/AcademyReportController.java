package com.goosage.academy.report;

import com.goosage.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import static com.goosage.auth.SessionConst.LOGIN_USER_ID; // 너 상수에 맞게 수정

@RestController
@RequestMapping("/academy/courses")
public class AcademyReportController {

    private final AcademyReportService service;

    public AcademyReportController(AcademyReportService service) {
        this.service = service;
    }

    @GetMapping("/{courseId}/report")
    public ApiResponse<CourseReportResponse> report(@PathVariable long courseId, HttpSession session) {
        Long userId = (Long) session.getAttribute(LOGIN_USER_ID);
        if (userId == null) throw new RuntimeException("UNAUTHORIZED");

        return ApiResponse.ok(service.getCourseReport(userId, courseId));
    }
}
