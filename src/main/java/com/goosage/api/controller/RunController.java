package com.goosage.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.KnowledgeService;
import com.goosage.app.KnowledgeTemplateService;
import com.goosage.app.QuizService;
import com.goosage.auth.SessionConst;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.quiz.QuizSubmitRequest;
import com.goosage.dto.quiz.QuizSubmitResponse;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
public class RunController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeTemplateService knowledgeTemplateService;
    private final QuizService quizService;

    public RunController(
            KnowledgeService knowledgeService,
            KnowledgeTemplateService knowledgeTemplateService,
            QuizService quizService
    ) {
        this.knowledgeService = knowledgeService;
        this.knowledgeTemplateService = knowledgeTemplateService;
        this.quizService = quizService;
    }

    @PostMapping("/run/knowledge/{id}")
    public ApiResponse<QuizSubmitResponse> runOneCycle(
            @PathVariable("id") long knowledgeId,
            HttpSession session
    ) {
        Object uidObj = session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (uidObj == null) return ApiResponse.fail("UNAUTHORIZED");

        long userId = (uidObj instanceof Long) ? (Long) uidObj : Long.parseLong(String.valueOf(uidObj));

        // 1) knowledge 확인
        KnowledgeDto k = knowledgeService.mustFindById(knowledgeId);

        // 2) summary 보장 (템플릿 저장까지)
        knowledgeTemplateService.toSummaryV1(k);

        // 3) quiz submit (빈 답안도 허용하는 정책이면 OK)
        QuizSubmitRequest req = new QuizSubmitRequest();
        QuizSubmitResponse res = quizService.submit(userId, knowledgeId, req);

        return ApiResponse.ok(res);
    }
    
    @GetMapping("/ping/run")
    public ApiResponse<String> pingRun() {
        return ApiResponse.ok("RUN_OK");
    }

}
