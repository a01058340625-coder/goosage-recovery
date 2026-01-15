package com.goosage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.dto.QuizSubmitRequest;
import com.goosage.dto.QuizSubmitResponse;
import com.goosage.repository.QuizResultDao;
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
            return ApiResponse.fail("UNAUTHORIZED");
        }

        long userId = (uidObj instanceof Long)
                ? (Long) uidObj
                : Long.parseLong(String.valueOf(uidObj));

        QuizSubmitResponse res = quizService.submit(userId, knowledgeId, request);
        return ApiResponse.ok(res);
    }

    @GetMapping("/knowledge/{id}/quiz/wrong")
    public ApiResponse<Map<String, Object>> wrong(
            @PathVariable("id") long knowledgeId,
            HttpSession session
    ) {
        Object uidObj = session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (uidObj == null) {
            return ApiResponse.fail("UNAUTHORIZED");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("knowledgeId", knowledgeId);

        // ✅ 최신 결과 1건 (없을 수도 있음)
        QuizResultDao.QuizResultRow latest = quizService.findLatestResult(knowledgeId);

        // ✅ 결과가 아직 없으면: 정상 응답(빈 오답)
        if (latest == null) {
            data.put("baseResultId", 0);
            data.put("wrong", List.of());
            return ApiResponse.ok(data);
        }

        // ✅ 결과가 있으면: details_json에서 오답만 필터
        List<Map<String, Object>> wrong = quizService.extractWrongDetails(latest.detailsJson());

        data.put("baseResultId", latest.id());
        data.put("wrong", wrong);

        return ApiResponse.ok(data);
    }
}
