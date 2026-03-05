package com.goosage.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.KnowledgeService;
import com.goosage.app.QaService;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.qa.QaRequest;
import com.goosage.dto.qa.QaResponse;
import com.goosage.support.web.ApiResponse;

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
