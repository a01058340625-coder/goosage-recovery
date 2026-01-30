package com.goosage.service.study;

import java.util.List;

import org.springframework.stereotype.Service;

import com.goosage.service.study.action.NextActionDto;
import com.goosage.service.study.action.NextActionService;
import com.goosage.service.study.action.NextActionType;
import com.goosage.service.study.dto.RiskLevel;
import com.goosage.service.study.dto.StudyCoachResponse;
import com.goosage.service.study.dto.StudyStateDto;
import com.goosage.service.study.forge.ForgeTriggerService;
import com.goosage.service.study.interpret.StudyInterpretationService;

@Service
public class StudyCoachService {

	private record Copy(String suggestion, List<String> reason, String nextActionReason) {
	}

	private final StudyStateService studyStateService;
	private final StudyInterpretationService interpretationService;
	private final NextActionService nextActionService;
	private final ForgeTriggerService forgeTriggerService;

	public StudyCoachService(StudyStateService studyStateService, StudyInterpretationService interpretationService,
			NextActionService nextActionService, ForgeTriggerService forgeTriggerService) {
		this.studyStateService = studyStateService;
		this.interpretationService = interpretationService;
		this.nextActionService = nextActionService;
		this.forgeTriggerService = forgeTriggerService;
	}

	public StudyCoachResponse coach(long userId) {

		String forgeMode = null;
		String forgeError = null;

		StudyStateDto state = studyStateService.getState(userId);

		// 1) 기본 행동 결정
		NextActionDto nextAction = nextActionService.decide(state);
		RiskLevel riskLevel = calculateRisk(state);

		// 2) TODAY_DONE 정합성 고정(최우선)
		if (state != null && state.eventsCount() >= 1 && state.quizSubmits() >= 1 && state.wrongReviews() == 0) {
			nextAction = new NextActionDto(NextActionType.TODAY_DONE, "TODAY_DONE", null, false,
					"오늘 학습은 이미 시작됐어. 이제는 가볍게 유지하는 게 핵심이야.");
		}

		// 3) v1.2: requiresForge=true면 템플릿 자동 준비 + 실패 시 fallback
		// (단, TODAY_DONE이면 forge 준비할 필요가 없으니 스킵)
		if (nextAction != null && nextAction.type() != NextActionType.TODAY_DONE && nextAction.requiresForge()
				&& nextAction.knowledgeId() != null) {

			ForgeTriggerService.ForgePrepareResult prep = forgeTriggerService.prepare(nextAction.knowledgeId(),
					"summary-v1");

			// ✅ 추가: 응답에 실을 값 저장
			forgeMode = prep.mode(); // "REUSE" / "CREATED" / "FAILED"
			forgeError = prep.error(); // 실패 메시지(없으면 null)

			if (!prep.success()) {
				// 실패를 명확히 표시하고 싶으면 mode를 FAILED로 강제도 가능
				// forgeMode = "FAILED";

				nextAction = new NextActionDto(NextActionType.JUST_OPEN, "Forge 준비 실패: 오늘은 가볍게 시작하자", null, false,
						"Forge 준비에 실패해도 루프는 끊기지 않는다. 오늘은 1개만 열자.");
			}
		}

		// 4) 해석/문구 생성
		String interpretation = interpretationService.buildInterpretation(state, nextAction);
		Copy copy = applyV11CopyRules(state, nextAction, riskLevel);

		// 5) 응답 (return 1번만)
		return new StudyCoachResponse(state, interpretation, nextAction, forgeMode, forgeError, copy.suggestion(),
				copy.reason(), riskLevel);

	}

	private Copy applyV11CopyRules(StudyStateDto state, NextActionDto nextAction, RiskLevel riskLevel) {

		int quizSubmits = state.quizSubmits();
		int wrongReviews = state.wrongReviews();
		boolean studiedToday = state.studiedToday();
		int streakDays = state.streakDays();

		// 기본값
		String suggestion = "지금 다음 행동을 실행해.";
		List<String> reason = List.of("상태: 코치 상태를 확인했다", "왜: 다음 행동을 수행하면 루프가 진행된다", "효과: today/streak 집계가 즉시 반영된다");
		String nextActionReason = "다음 행동 1개를 하면 루프가 열린다";

		// (A) TODAY_DONE (nextAction으로 확정된 경우 최우선)
		if (nextAction != null && nextAction.type() == NextActionType.TODAY_DONE) {
			suggestion = "오늘은 이미 했어. 요약 1개만 보고 마무리하자.";
			reason = List.of("상태: 오늘 학습은 이미 완료했다", "왜: 마무리 1개(요약)가 기억을 고정한다", "효과: 부담 없이 streak를 안정화한다");
			nextActionReason = "요약 1개만 보면 오늘 학습이 ‘완료’로 고정된다";
			return new Copy(suggestion, reason, nextActionReason);
		}
		
		// (B) v1.3 습관화 레이어: 위험도 기반 문장 (TODAY_DONE 다음 우선)
		if (riskLevel == RiskLevel.DANGER) {
		    suggestion =
		            "지금 안 하면 streak가 끊긴다.\n" +
		            "딱 1개면 충분하다.\n" +
		            "완벽 말고, 연결만 유지하자.";

		    reason = List.of(
		            "streak가 2일 이상 이어진 상태에서 오늘이 비어 있다",
		            "오늘을 넘기면 연속 학습 흐름이 끊긴다",
		            "최소 행동 1개면 위험을 바로 해소할 수 있다"
		    );

		    nextActionReason = "지금 1개만 하면 streak 위험을 즉시 해소한다";
		    return new Copy(suggestion, reason, nextActionReason);
		}

		if (riskLevel == RiskLevel.WARNING) {
		    suggestion =
		            "오늘 비면 흐름이 약해진다.\n" +
		            "부담 없이 1개만 찍자.\n" +
		            "연결이 목표다.";

		    reason = List.of(
		            "어제 시작된 흐름이 아직 약하다",
		            "오늘 1개만 해도 연속성이 유지된다",
		            "지금은 양보다 연결이 중요하다"
		    );

		    nextActionReason = "오늘 1개만 해도 흐름이 유지된다";
		    return new Copy(suggestion, reason, nextActionReason);
		}


		// (B) 템플릿 #3: TODAY_DONE 느낌(정리/마감) - 타입 변경 없이 문장만
		if (quizSubmits >= 1 && wrongReviews == 0) {
			suggestion = "오늘은 한 번이라도 제출까지 했네. 이제 ‘정리’만 하면 끝.\n" + "지금은 새로 늘리기보다, 오늘 만든 흐름을 고정해.\n"
					+ "남는 10분이면 ‘1개만’ 노트로 정리하고 종료하자.";
			reason = List.of("제출까지 했다는 건 오늘 루프가 이미 성립했다는 뜻.", "오답 복습이 0이면, 부담 낮게 ‘정리’로 마감하기 딱 좋다.",
					"오늘을 ‘완료’로 찍어야 내일 streak/루틴이 쉬워진다.");
			nextActionReason = "마무리 1개를 하면 오늘 학습이 ‘완료’로 고정된다";
			return new Copy(suggestion, reason, nextActionReason);
		}

		// (C) 템플릿 #4: STREAK_RISK - 타입 변경 없이 문장만
		if (!studiedToday && streakDays >= 2) {
			suggestion = "지금은 ‘큰 진도’ 말고, streak만 살리는 최소 행동을 하자.\n" + "단 1개 이벤트만 찍어도 오늘은 성공이야.\n"
					+ "가볍게 JUST_OPEN 또는 짧은 퀴즈 1회로 복귀하자.";
			reason = List.of("streak가 2일 이상이면, 오늘 비면 ‘끊김 비용’이 커진다.", "작게라도 찍으면 내일 다시 가속 붙이기 쉬워진다.",
					"지금 필요한 건 성취가 아니라 ‘연결 유지’다.");
			nextActionReason = "최소 행동 1개만 해도 streak 위험을 막을 수 있다";
			return new Copy(suggestion, reason, nextActionReason);
		}

		// (D) 템플릿 #5: MANY_WRONGS - 타입 변경 없이 문장만
		if (wrongReviews >= 3) {
			suggestion = "오답이 쌓였으니 새로 만들지 말고 ‘오답 1개만’ 처리하자.\n" + "오늘은 REVIEW_WRONG_ONE 한 번만 성공시키면 충분해.\n"
					+ "지금은 넓히기보다, 약점 루프를 닫는 게 효율이 제일 크다.";
			reason = List.of("오답이 3개 이상이면 지금 확장하면 더 복잡해진다.", "오답 1개를 닫는 순간 nextAction 품질이 바로 좋아진다.",
					"학습 체감은 ‘새로’가 아니라 ‘막힌 곳이 풀릴 때’ 온다.");
			nextActionReason = "오답 1개만 닫아도 약점 루프가 다시 돈다";
			return new Copy(suggestion, reason, nextActionReason);
		}

		// (E) REVIEW_WRONG_ONE (기존 규칙 유지)
		if (nextAction != null && nextAction.type() == NextActionType.REVIEW_WRONG_ONE) {
			boolean forge = nextAction.requiresForge();

			suggestion = forge ? "Forge부터 열고 오답 1개만 복습해. (딱 1개면 충분해)" : "오답 1개만 복습해. (딱 1개면 충분해)";

			reason = List.of("상태: 오늘 오답 복습이 남아 있다",
					forge ? "왜: Forge 단계가 필요해서 먼저 준비하고 들어가야 한다" : "왜: 오답 1개만 처리해도 루프가 이어진다", "효과: 약점이 줄고 streak가 유지된다");

			nextActionReason = forge ? "Forge 준비 후 오답 1개만 처리하면 약점 루프가 다시 돈다" : "오답 1개만 처리해도 약점이 줄고 루프가 이어진다";

			return new Copy(suggestion, reason, nextActionReason);
		}

		// (F) JUST_OPEN (기존 규칙 유지)
		if (nextAction != null && nextAction.type() == NextActionType.JUST_OPEN) {
			suggestion = "오늘은 오답 이벤트 1개만 찍고 시작하자. (start 신호)";
			reason = List.of("상태: 아직 오늘 학습 기록이 없다", "왜: 첫 행동 1개가 있어야 코치가 다음 행동을 더 정확히 추천한다",
					"효과: today/streak 집계가 움직이고 루프가 열린다");
			nextActionReason = "첫 행동 1개가 있어야 코치 추천 루프가 열린다";
			return new Copy(suggestion, reason, nextActionReason);
		}

		return new Copy(suggestion, reason, nextActionReason);
	}

	private RiskLevel calculateRisk(StudyStateDto state) {
		if (state.studiedToday()) {
			return RiskLevel.SAFE;
		}
		if (state.streakDays() >= 2) {
			return RiskLevel.DANGER;
		}
		return RiskLevel.WARNING;
	}

}
