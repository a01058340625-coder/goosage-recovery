package com.goosage.academy.progress;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/academy")
public class AcademyProgressController {

    @GetMapping("/courses/{courseId}/progress")
    public ApiResponse<?> progress(@PathVariable long courseId, HttpSession session) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) return ApiResponse.fail("UNAUTHORIZED");

        // ✅ 컨트롤러 컴파일만 먼저 성공시키기 위한 임시 응답
        return ApiResponse.ok(
            java.util.Map.of(
                "courseId", courseId,
                "userId", userId,
                "message", "progress endpoint alive (service wiring later)"
            )
        );
    }
}
