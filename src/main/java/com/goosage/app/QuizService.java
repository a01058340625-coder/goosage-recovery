package com.goosage.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.quiz.QuizAnswer;
import com.goosage.dto.quiz.QuizResultItem;
import com.goosage.dto.quiz.QuizResultResponse;
import com.goosage.dto.quiz.QuizRetryQuestion;
import com.goosage.dto.quiz.QuizRetryResponse;
import com.goosage.dto.quiz.QuizSubmitRequest;
import com.goosage.dto.quiz.QuizSubmitResponse;
import com.goosage.infra.dao.QuizItemDao;
import com.goosage.infra.dao.QuizResultDao;
import com.goosage.infra.dao.StudyEventDao;
import com.goosage.infra.dao.QuizResultDao.QuizResultRow;


@Service
public class QuizService {

	private final KnowledgeService knowledgeService;
	private final QuizResultDao quizResultDao;
	private final ObjectMapper objectMapper;
	private final QuizItemDao quizItemDao;
	private final StudyEventDao studyEventDao;

	public QuizService(QuizResultDao quizResultDao, KnowledgeService knowledgeService, QuizItemDao quizItemDao,
			StudyEventDao studyEventDao, ObjectMapper objectMapper) {

		this.quizResultDao = quizResultDao;
		this.knowledgeService = knowledgeService;
		this.quizItemDao = quizItemDao;
		this.studyEventDao = studyEventDao;
		this.objectMapper = objectMapper;
	}

	public QuizRetryResponse retry(long userId, long knowledgeId) {

		QuizResultDao.QuizResultRow latest = quizResultDao.findLatestByUserAndKnowledgeId(userId, knowledgeId);

		if (latest == null) {
			return new QuizRetryResponse(knowledgeId, 0L, List.of());

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

		return new QuizRetryResponse(knowledgeId, latest.id(), qs);

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

		int total = 3;
		int percent = correctCount * 100 / total;

		// ✅ 여기서 계산 (서비스 계층)
		int wrongCount = total - correctCount;

		try {
		    String detailsJson = objectMapper.writeValueAsString(details);

		    // ✅ wrongCount 포함해서 저장
		    quizResultDao.save(userId, knowledgeId, total, correctCount, percent, wrongCount, detailsJson);

		    studyEventDao.recordEvent(userId, "QUIZ_SUBMIT", "KNOWLEDGE", knowledgeId, detailsJson);

		} catch (Exception e) {
		    throw new RuntimeException("quiz result save failed", e);
		}

		return new QuizSubmitResponse(knowledgeId, total, correctCount, results);
	}

	private boolean isCorrect(String userAnswer, String expected) {
	    if (!StringUtils.hasText(expected)) {
	        return false; // ✅ 빈 expected는 자동정답 금지
	    }
	    if (userAnswer == null) return false;
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
	
	public QuizResultDao.QuizResultRow findLatestByUserAndKnowledgeId(long userId, long knowledgeId) {
	    return quizResultDao.findLatestByUserAndKnowledgeId(userId, knowledgeId);
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

			return details.stream().filter(d -> {
				Object c = d.get("correct");
				return (c instanceof Boolean) && !((Boolean) c);
			}).toList();

		} catch (Exception e) {
			throw new RuntimeException("quiz wrong parse failed", e);
		}
	}
	public String findLatestDetailsJson(long userId, long knowledgeId) {
	    var row = quizResultDao.findLatestByUserAndKnowledgeId(userId, knowledgeId);
	    return row == null ? null : row.detailsJson();
	}


}
