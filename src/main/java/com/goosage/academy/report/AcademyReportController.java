package com.goosage.academy.report;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
