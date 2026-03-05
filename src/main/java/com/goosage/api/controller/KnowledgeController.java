package com.goosage.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.KnowledgeService;
import com.goosage.app.KnowledgeTemplateService;
import com.goosage.app.QuizService;
import com.goosage.app.TemplateService;
import com.goosage.auth.SessionConst;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.knowledge.KnowledgeCreateRequest;
import com.goosage.dto.knowledge.KnowledgeMapper;
import com.goosage.dto.knowledge.KnowledgeResponse;
import com.goosage.dto.quiz.QuizResponse;
import com.goosage.dto.quiz.QuizResultResponse;
import com.goosage.dto.quiz.QuizSubmitRequest;
import com.goosage.dto.quiz.QuizSubmitResponse;
import com.goosage.dto.template.TemplateDto;
import com.goosage.dto.template.TemplateResponse;
import com.goosage.support.web.ApiResponse;
import com.goosage.support.web.UnauthorizedException;

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeTemplateService knowledgeTemplateService;
    private final TemplateService templateService;
    private final QuizService quizService;

    public KnowledgeController(
            KnowledgeService knowledgeService,
            KnowledgeTemplateService knowledgeTemplateService,
            TemplateService templateService,
            QuizService quizService
    ) {
        this.knowledgeService = knowledgeService;
        this.knowledgeTemplateService = knowledgeTemplateService;
        this.templateService = templateService;
        this.quizService = quizService;
    }

    // =========================
    // auth helper
    // =========================
    private long requireUserId(HttpSession session) {
        Object uidObj = session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (uidObj == null) throw new UnauthorizedException("UNAUTHORIZED");

        if (uidObj instanceof Long) return (Long) uidObj;

        // 세션에 String/Integer로 들어갈 수도 있어서 방어
        return Long.parseLong(String.valueOf(uidObj));
    }

    // =========================
    // CRUD
    // =========================

    @PostMapping
    public ApiResponse<KnowledgeResponse> create(
            @RequestBody KnowledgeCreateRequest req,
            HttpSession session
    ) {
        requireUserId(session);

        KnowledgeDto saved = knowledgeService.save(KnowledgeMapper.toDto(req));
        return ApiResponse.ok(KnowledgeMapper.toResponse(saved));
    }

    @GetMapping
    public ApiResponse<List<KnowledgeResponse>> list(HttpSession session) {
        requireUserId(session);

        List<KnowledgeDto> list = knowledgeService.findAll();
        return ApiResponse.ok(KnowledgeMapper.toResponseList(list));
    }

    @GetMapping("/{id}")
    public ApiResponse<KnowledgeResponse> getOne(
            @PathVariable Long id,
            HttpSession session
    ) {
        requireUserId(session);

        KnowledgeDto k = knowledgeService.mustFindById(id);
        return ApiResponse.ok(KnowledgeMapper.toResponse(k));
    }


    @GetMapping("/{id}/template/summary-v1")
    public ApiResponse<TemplateResponse> summaryV1(
            @PathVariable Long id,
            HttpSession session
    ) {
        requireUserId(session);

        KnowledgeDto knowledge = knowledgeService.mustFindById(id);
        String resultText = knowledgeTemplateService.toSummaryV1(knowledge);

        TemplateResponse response = new TemplateResponse(
                knowledge.getId(),
                "SUMMARY_V1",
                resultText
        );

        return ApiResponse.ok(response);
    }

    @GetMapping("/{id}/template/quiz-v1")
    public ApiResponse<QuizResponse> quizV1(
            @PathVariable Long id,
            HttpSession session
    ) {
        requireUserId(session);

        KnowledgeDto knowledge = knowledgeService.mustFindById(id);

        QuizResponse response = new QuizResponse(
                knowledge.getId(),
                "QUIZ_V1",
                knowledgeTemplateService.toQuizV1(knowledge)
        );

        return ApiResponse.ok(response);
    }

    // =========================
    // template v2 (캐싱/저장 기반)
    // =========================

    @GetMapping("/{id}/template/summary-v2")
    public ApiResponse<TemplateResponse> summaryV2(
            @PathVariable Long id,
            HttpSession session
    ) {
        requireUserId(session);

        KnowledgeDto knowledge = knowledgeService.mustFindById(id);
        TemplateDto saved = templateService.getOrCreateSummaryV2(knowledge);

        TemplateResponse response = new TemplateResponse(
                knowledge.getId(),
                saved.getTemplateType(),
                saved.getResultText()
        );

        return ApiResponse.ok(response);
    }

    @GetMapping("/{id}/template/quiz-v2")
    public ApiResponse<QuizResponse> quizV2(
            @PathVariable Long id,
            HttpSession session
    ) {
        requireUserId(session);

        KnowledgeDto knowledge = knowledgeService.mustFindById(id);
        TemplateDto saved = templateService.getOrCreateQuizV2(knowledge);

        // v2 결과에서 "1) ..." "2) ..." 같은 라인만 추출
        String[] lines = saved.getResultText().split("\\R");
        java.util.List<String> qs = new java.util.ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("1) ") || line.startsWith("2) ") || line.startsWith("3) ")) {
                qs.add(line);
            }
        }

        QuizResponse response = new QuizResponse(
                knowledge.getId(),
                saved.getTemplateType(),
                qs
        );

        return ApiResponse.ok(response);
    }

    // =========================
    // quiz results
    // =========================

    @GetMapping("/{id}/quiz/results")
    public ApiResponse<List<QuizResultResponse>> quizResults(
            @PathVariable Long id,
            HttpSession session
    ) {
        requireUserId(session);
        return ApiResponse.ok(quizService.findResults(id));
    }

    // =========================
    // run (one cycle)
    // =========================

    @PostMapping("/{id}/run")
    public ApiResponse<QuizSubmitResponse> runOneCycle(
            @PathVariable("id") long knowledgeId,
            HttpSession session
    ) {
        long userId = requireUserId(session);

        // (선택) 미리 summary 생성 - 기존 로직 유지
        knowledgeTemplateService.toSummaryV1(
                knowledgeService.mustFindById(knowledgeId)
        );

        // 제출 요청 (현재는 빈 요청으로 1바퀴 돌림)
        QuizSubmitRequest req = new QuizSubmitRequest();
        QuizSubmitResponse res = quizService.submit(userId, knowledgeId, req);

        return ApiResponse.ok(res);
    }
}
