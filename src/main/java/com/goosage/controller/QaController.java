package com.goosage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.goosage.common.ApiResponse;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.qa.QaRequest;
import com.goosage.dto.qa.QaResponse;
import com.goosage.service.KnowledgeService;
import com.goosage.service.QaService;

@RestController
@RequestMapping("/qa")
public class QaController {

    private final QaService qaService;
    private final KnowledgeService knowledgeService;

    public QaController(QaService qaService, KnowledgeService knowledgeService) {
        this.qaService = qaService;
        this.knowledgeService = knowledgeService;
    }

    /** 1) 질문 저장 */
    @PostMapping
    public ApiResponse<QaResponse> create(@RequestBody QaRequest req) {
        return ApiResponse.ok(qaService.create(req));
    }

    /** 2) 목록 */
    @GetMapping
    public ApiResponse<List<QaResponse>> findAll() {
        return ApiResponse.ok(qaService.findAll());
    }

    /** 3) 답변 채우기 */
    @PutMapping("/{id}/answer")
    public ApiResponse<QaResponse> answer(@PathVariable long id, @RequestBody QaRequest req) {
        return ApiResponse.ok(qaService.answer(id, req));
    }

    /** 4) QA → Knowledge 변환 (idempotent) */
    @PostMapping("/{id}/convert")
    public ApiResponse<KnowledgeDto> convertToKnowledge(@PathVariable long id) {
        return ApiResponse.ok(knowledgeService.convertQaToKnowledge(id));
    }
}
