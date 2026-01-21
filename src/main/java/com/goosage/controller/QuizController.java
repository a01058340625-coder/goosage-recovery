package com.goosage.controller;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import com.goosage.dto.quiz.QuizRetryQuestion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.common.UnauthorizedException;
import com.goosage.dto.quiz.QuizMapper;
import com.goosage.dto.quiz.QuizRetryResponse;
import com.goosage.dto.quiz.QuizSubmitRequest;
import com.goosage.dto.quiz.QuizSubmitResponse;
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
        long userId = requireUserId(session);
        QuizSubmitResponse res = quizService.submit(userId, knowledgeId, request);
        return ApiResponse.ok(res);
    }

    @GetMapping("/knowledge/{id}/quiz/wrong")
    public ApiResponse<QuizRetryResponse> wrong(
            @PathVariable("id") long knowledgeId,
            HttpSession session
    ) {
        requireUserId(session);

        QuizResultDao.QuizResultRow latest = quizService.findLatestResult(knowledgeId);

        List<QuizRetryQuestion> wrong = new ArrayList<>();
        if (latest != null) {
            List<Map<String, Object>> details = quizService.extractWrongDetails(latest.detailsJson());

            for (Map<String, Object> d : details) {
                // extractWrongDetails가 어떤 키로 주는지에 맞춰서 맞추면 됨
                int qid = Integer.parseInt(String.valueOf(d.get("no")));          // 또는 "question_idx"
                String qText = String.valueOf(d.get("question"));                 // 또는 "qText"
                wrong.add(new QuizRetryQuestion(qid, qText));
            }
        }

        return ApiResponse.ok(QuizMapper.toWrongResponse(knowledgeId, latest, wrong));
    }
    


    @GetMapping("/knowledge/{id}/quiz/retry")
    public ApiResponse<QuizRetryResponse> retry(
            @PathVariable("id") long knowledgeId,
            HttpSession session
    ) {
        long userId = requireUserId(session);
        QuizRetryResponse res = quizService.retry(userId, knowledgeId);
        return ApiResponse.ok(res);
    }

    
    private long requireUserId(HttpSession session) {
        Object uidObj = session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (uidObj == null) throw new UnauthorizedException("UNAUTHORIZED");

        if (uidObj instanceof Long) return (Long) uidObj;
        return Long.parseLong(String.valueOf(uidObj));
    }
}
