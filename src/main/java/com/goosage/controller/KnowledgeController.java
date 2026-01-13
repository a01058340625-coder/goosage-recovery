package com.goosage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.goosage.common.ApiResponse;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.QuizResponse;
import com.goosage.dto.TemplateDto;
import com.goosage.dto.TemplateResponse;
import com.goosage.service.KnowledgeService;
import com.goosage.service.KnowledgeTemplateService;
import com.goosage.service.TemplateService;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeTemplateService knowledgeTemplateService;
    private final TemplateService templateService;

    public KnowledgeController(KnowledgeService knowledgeService,
                               KnowledgeTemplateService knowledgeTemplateService,
                               TemplateService templateService) {
        this.knowledgeService = knowledgeService;
        this.knowledgeTemplateService = knowledgeTemplateService;
        this.templateService = templateService;
    }

    /** ✅ 전체 조회 */
    @GetMapping
    public ApiResponse<List<KnowledgeDto>> list() {
        return ApiResponse.ok(knowledgeService.findAll());
    }

    /** ✅ 저장 */
    @PostMapping
    public ApiResponse<KnowledgeDto> save(@RequestBody KnowledgeDto req) {
        return ApiResponse.ok("저장 완료", knowledgeService.save(req));
    }

    @GetMapping("/{id}/template/summary-v1")
    public ApiResponse<TemplateResponse> summaryV1(@PathVariable Long id) {
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
    public ApiResponse<QuizResponse> quizV1(@PathVariable Long id) {
        KnowledgeDto knowledge = knowledgeService.mustFindById(id);

        QuizResponse response = new QuizResponse(
            knowledge.getId(),
            "QUIZ_V1",
            knowledgeTemplateService.toQuizV1(knowledge)
        );

        return ApiResponse.ok(response);
    }

    // ===== v2 (캐싱/저장 기반) =====

    @GetMapping("/{id}/template/summary-v2")
    public ApiResponse<TemplateResponse> summaryV2(@PathVariable Long id) {

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
    public ApiResponse<QuizResponse> quizV2(@PathVariable Long id) {

        KnowledgeDto knowledge = knowledgeService.mustFindById(id);

        TemplateDto saved = templateService.getOrCreateQuizV2(knowledge);

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
}
