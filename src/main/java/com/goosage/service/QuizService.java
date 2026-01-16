package com.goosage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.QuizAnswer;
import com.goosage.dto.QuizResultItem;
import com.goosage.dto.QuizResultResponse;
import com.goosage.dto.QuizRetryQuestion;
import com.goosage.dto.QuizRetryResponse;
import com.goosage.dto.QuizSubmitRequest;
import com.goosage.dto.QuizSubmitResponse;
import com.goosage.repository.QuizItemDao;
import com.goosage.repository.QuizResultDao;
import com.goosage.repository.QuizResultDao.QuizResultRow;

@Service
public class QuizService {

	private final KnowledgeService knowledgeService;
	private final QuizResultDao quizResultDao;
	private final ObjectMapper objectMapper;
	private final QuizItemDao quizItemDao;

	public QuizService(KnowledgeService knowledgeService, QuizResultDao quizResultDao, ObjectMapper objectMapper,
			QuizItemDao quizItemDao) {
		this.knowledgeService = knowledgeService;
		this.quizResultDao = quizResultDao;
		this.objectMapper = objectMapper;
		this.quizItemDao = quizItemDao;
	}
	
	public QuizRetryResponse retry(long userId, long knowledgeId) {

	    QuizResultDao.QuizResultRow latest =
	            quizResultDao.findLatestByUserAndKnowledgeId(userId, knowledgeId);

	    if (latest == null) {
	        return new QuizRetryResponse(knowledgeId, 0L, 0, List.of());
	    }

	    List<Map<String, Object>> details;
	    try {
	        details = objectMapper.readValue(latest.detailsJson(), List.class);
	    } catch (Exception e) {
	        throw new RuntimeException("details_json parse failed", e);
	    }

	    List<QuizRetryQuestion> qs = new ArrayList<>();

	    for (Map<String, Object> d : details) {
	        Object correctObj = d.get("correct");
	        boolean correct = (correctObj instanceof Boolean) ? (Boolean) correctObj : false;

	        if (!correct) {
	            int qid = Integer.parseInt(String.valueOf(d.get("no")));
	            String qText = String.valueOf(d.get("question"));
	            qs.add(new QuizRetryQuestion(qid, qText));
	        }
	    }

	    return new QuizRetryResponse(knowledgeId, latest.id(), qs.size(), qs);
	}


	public QuizSubmitResponse submit(long userId, long knowledgeId, QuizSubmitRequest request) {

		KnowledgeDto knowledge = knowledgeService.mustFindById(knowledgeId);

		// 질문/정답 세트
		Map<Integer, String> questionTextMap = buildQuizV1Questions(knowledge);
		Map<Integer, String> answerKeyMap = buildQuizV1AnswerKey();

		List<QuizResultItem> results = new ArrayList<>();
		List<Map<String, Object>> details = new ArrayList<>();

		int correctCount = 0;

		// 사용자 답안 맵핑
		Map<Integer, String> userAnswerMap = new HashMap<>();
		if (request != null && request.getAnswers() != null) {
			for (QuizAnswer a : request.getAnswers()) {
				if (a == null)
					continue;
				userAnswerMap.put(a.getQuestionId(), a.getAnswer());
			}
		}

		// 1~3 문항 처리
		for (int qid = 1; qid <= 3; qid++) {
			String qText = questionTextMap.getOrDefault(qid, "Q" + qid);
			String userAns = userAnswerMap.getOrDefault(qid, "");
			String expected = answerKeyMap.getOrDefault(qid, "");

			boolean correct = isCorrect(userAns, expected);
			if (correct)
				correctCount++;

			// UI 반환용
			results.add(new QuizResultItem(qid, qText, userAns, correct));

			// DB details_json용
			Map<String, Object> d = new HashMap<>();
			d.put("no", qid);
			d.put("question", qText);
			d.put("expected", expected);
			d.put("userAnswer", userAns);
			d.put("correct", correct);
			details.add(d);
		}

		// submit 1회 = DB insert 1회
		int total = 3;
		int percent = correctCount * 100 / total;

		try {
			String detailsJson = objectMapper.writeValueAsString(details);

			quizResultDao.save(userId, knowledgeId, total, correctCount, percent, detailsJson);

		} catch (Exception e) {
			throw new RuntimeException("quiz result save failed", e);
		}

		return new QuizSubmitResponse(knowledgeId, total, correctCount, results);
	}

	private boolean isCorrect(String userAnswer, String expected) {
		// v0.6 UX 전략:
		// expected 비어있으면 "입력만 하면 정답"
		if (!StringUtils.hasText(expected)) {
			return StringUtils.hasText(userAnswer);
		}
		if (userAnswer == null)
			return false;
		return userAnswer.trim().equalsIgnoreCase(expected.trim());
	}

	private Map<Integer, String> buildQuizV1Questions(KnowledgeDto k) {
		Map<Integer, String> m = new HashMap<>();
		String base = (k != null && StringUtils.hasText(k.getContent())) ? k.getContent() : "";

		String hint = base.length() > 120 ? base.substring(0, 120) + "..." : base;

		m.put(1, "이 지식의 핵심 키워드 1개를 써라.");
		m.put(2, "이 지식을 한 줄로 요약해라.");
		m.put(3, "이 지식을 실제로 어디에 쓰는지 예시 1개를 들어라. (힌트: " + hint + ")");
		return m;
	}

	private Map<Integer, String> buildQuizV1AnswerKey() {
		// v0.6: 정답 강제 없음 (입력 중심 학습 루프)
		return new HashMap<>();
	}

	public List<QuizResultResponse> findResults(long knowledgeId) {

		List<QuizResultRow> rows = quizResultDao.findByKnowledgeId(knowledgeId);

		return rows.stream().map(r -> {
			try {
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> details = objectMapper.readValue(r.detailsJson(), List.class);

				return new QuizResultResponse(r.id(), r.totalCount(), r.correctCount(), r.scorePercent(), details,
						r.createdAt());
			} catch (Exception e) {
				throw new RuntimeException("quiz result parse failed", e);
			}
		}).toList();
	}
	
	public QuizResultDao.QuizResultRow findLatestResult(long knowledgeId) {
	    return quizResultDao.findLatestByKnowledgeId(knowledgeId); // DAO가 없으면 null 반환하도록
	}




	private void ensureQuizItems(long knowledgeId, String contentHint) {
		if (quizItemDao.exists(knowledgeId))
			return;

		// Q1
		quizItemDao.insert(knowledgeId, 1, "이 지식의 핵심 키워드 1개를 써라.", "핵심 키워드 1개 (예: 파이프라인/세션/Flyway 등)");

		// Q2
		quizItemDao.insert(knowledgeId, 2, "이 지식을 한 줄로 요약해라.", "한 줄 요약 (주어+핵심동사+효과). 예: 저장→요약→퀴즈→결과로 학습 루프를 만든다.");

		// Q3
		quizItemDao.insert(knowledgeId, 3, "이 지식을 실제로 어디에 쓰는지 예시 1개를 들어라. (힌트: " + contentHint + ")",
				"사용 예시 1개. 예: 복습 자동화, 오답노트 기반 반복학습, API 테스트 루틴 고정 등");
	}
	
	public List<Map<String, Object>> extractWrongDetails(String detailsJson) {
	    try {
	        @SuppressWarnings("unchecked")
	        List<Map<String, Object>> details = objectMapper.readValue(detailsJson, List.class);

	        return details.stream()
	                .filter(d -> {
	                    Object c = d.get("correct");
	                    return (c instanceof Boolean) && !((Boolean) c);
	                })
	                .toList();

	    } catch (Exception e) {
	        throw new RuntimeException("quiz wrong parse failed", e);
	    }
	}


}
