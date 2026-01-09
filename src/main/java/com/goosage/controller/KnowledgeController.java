package com.goosage.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.common.ApiResponse;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.TemplateResponse;
import com.goosage.service.KnowledgeService;
import com.goosage.service.KnowledgeTemplateService;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeTemplateService knowledgeTemplateService;

    public KnowledgeController(
            KnowledgeService knowledgeService,
            KnowledgeTemplateService knowledgeTemplateService
    ) {
        this.knowledgeService = knowledgeService;
        this.knowledgeTemplateService = knowledgeTemplateService;
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

    /**
     * ✅ v0.4 Step2
     * Knowledge → Summary Template v1
     *
     * GET /knowledge/{id}/template/summary-v1
     */
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

    }

