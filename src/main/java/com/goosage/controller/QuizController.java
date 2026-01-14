package com.goosage.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.dto.QuizSubmitRequest;
import com.goosage.dto.QuizSubmitResponse;
import com.goosage.service.QuizService;

import jakarta.servlet.http.HttpSession;

@RestController
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/knowledge/{id}/quiz/submit")
    public ApiResponse<QuizSubmitResponse> submit(
            @PathVariable("id") long knowledgeId,
            @RequestBody QuizSubmitRequest request,
            HttpSession session
    ) {
        Object uidObj = session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (uidObj == null) {
            // 보통 필터가 401 처리하지만, 컨트롤러에서도 안전하게
            return ApiResponse.fail("UNAUTHORIZED");
        }

        long userId = (uidObj instanceof Long) ? (Long) uidObj : Long.parseLong(String.valueOf(uidObj));

        QuizSubmitResponse res = quizService.submit(userId, knowledgeId, request);
        return ApiResponse.ok(res);
    }
}
