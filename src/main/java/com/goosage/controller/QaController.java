package com.goosage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.goosage.common.ApiResponse;
import com.goosage.dto.QaRequest;
import com.goosage.dto.QaResponse;
import com.goosage.service.QaService;

@RestController
@RequestMapping("/qa")
public class QaController {

    private final QaService qaService;

    public QaController(QaService qaService) {
        this.qaService = qaService;
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
}
