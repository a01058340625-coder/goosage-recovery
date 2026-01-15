package com.goosage.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.QuizResponse;
import com.goosage.dto.QuizResultResponse;
import com.goosage.dto.QuizSubmitRequest;
import com.goosage.dto.QuizSubmitResponse;
import com.goosage.dto.TemplateDto;
import com.goosage.dto.TemplateResponse;
import com.goosage.service.KnowledgeService;
import com.goosage.service.KnowledgeTemplateService;
import com.goosage.service.QuizService;
import com.goosage.service.TemplateService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

	private final KnowledgeService knowledgeService;
	private final KnowledgeTemplateService knowledgeTemplateService;
	private final TemplateService templateService;
	private final QuizService quizService;

	public KnowledgeController(KnowledgeService knowledgeService, KnowledgeTemplateService knowledgeTemplateService,
			TemplateService templateService, QuizService quizService) {
		this.knowledgeService = knowledgeService;
		this.knowledgeTemplateService = knowledgeTemplateService;
		this.templateService = templateService;
		this.quizService = quizService;
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

		TemplateResponse response = new TemplateResponse(knowledge.getId(), "SUMMARY_V1", resultText);

		return ApiResponse.ok(response);
	}

	@GetMapping("/{id}/template/quiz-v1")
	public ApiResponse<QuizResponse> quizV1(@PathVariable Long id) {
		KnowledgeDto knowledge = knowledgeService.mustFindById(id);

		QuizResponse response = new QuizResponse(knowledge.getId(), "QUIZ_V1",
				knowledgeTemplateService.toQuizV1(knowledge));

		return ApiResponse.ok(response);
	}

	// ===== v2 (캐싱/저장 기반) =====

	@GetMapping("/{id}/template/summary-v2")
	public ApiResponse<TemplateResponse> summaryV2(@PathVariable Long id) {

		KnowledgeDto knowledge = knowledgeService.mustFindById(id);

		TemplateDto saved = templateService.getOrCreateSummaryV2(knowledge);

		TemplateResponse response = new TemplateResponse(knowledge.getId(), saved.getTemplateType(),
				saved.getResultText());

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

		QuizResponse response = new QuizResponse(knowledge.getId(), saved.getTemplateType(), qs);

		return ApiResponse.ok(response);
	}

	@GetMapping("/{id}/quiz/results")
	public ApiResponse<List<QuizResultResponse>> quizResults(@PathVariable Long id) {
		return ApiResponse.ok(quizService.findResults(id));
	}
	
	@PostMapping("/knowledge/{id}/run")
	public ApiResponse<QuizSubmitResponse> runOneCycle(
	        @PathVariable("id") long knowledgeId,
	        HttpSession session
	) {
	    Object uidObj = session.getAttribute(SessionConst.LOGIN_USER_ID);
	    if (uidObj == null) return ApiResponse.fail("UNAUTHORIZED");

	    long userId = (uidObj instanceof Long) ? (Long) uidObj : Long.parseLong(String.valueOf(uidObj));

	    // 1) summary 보장 (이미 있으면 그대로)
	    knowledgeTemplateService.toSummaryV1(knowledgeService.mustFindById(knowledgeId));

	    // 2) 퀴즈 자동 제출(빈 답안 or 기본 답안)
	    QuizSubmitRequest req = new QuizSubmitRequest(); // answers 비어있어도 동작하게 해둔 상태면 OK
	    QuizSubmitResponse res = quizService.submit(userId, knowledgeId, req);

	    return ApiResponse.ok(res);
	}


}
