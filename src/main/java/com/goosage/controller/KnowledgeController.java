package com.goosage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.common.ApiResponse;
import com.goosage.dto.KnowledgeDto;
import com.goosage.service.KnowledgeService;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }
    
    @PostMapping("/from-qa/{qaId}")
    public ApiResponse<?> fromQa(@PathVariable long qaId) {
        return ApiResponse.ok("변환 완료", knowledgeService.fromQa(qaId));
    }
    



    /** 전체 조회 */
    @GetMapping
    public ApiResponse<List<KnowledgeDto>> list() {
        return ApiResponse.ok(knowledgeService.findAll());
    }

    /** 저장 */
    @PostMapping
    public ApiResponse<KnowledgeDto> save(@RequestBody KnowledgeDto req) {
        return ApiResponse.ok("저장 완료", knowledgeService.save(req));
    }
}
