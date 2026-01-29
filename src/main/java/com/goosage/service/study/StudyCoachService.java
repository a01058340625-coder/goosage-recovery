package com.goosage.service.study;

import java.util.List;

import org.springframework.stereotype.Service;

import com.goosage.service.study.action.NextActionDto;
import com.goosage.service.study.action.NextActionService;
import com.goosage.service.study.action.NextActionType;
import com.goosage.service.study.dto.StudyCoachResponse;
import com.goosage.service.study.dto.StudyStateDto;
import com.goosage.service.study.interpret.StudyInterpretationService;

@Service
public class StudyCoachService {

    private final StudyStateService studyStateService;
    private final StudyInterpretationService interpretationService;
    private final NextActionService nextActionService;

    public StudyCoachService(
            StudyStateService studyStateService,
            StudyInterpretationService interpretationService,
            NextActionService nextActionService
    ) {
        this.studyStateService = studyStateService;
        this.interpretationService = interpretationService;
        this.nextActionService = nextActionService;
    }

    public StudyCoachResponse coach(long userId) {
        StudyStateDto state = studyStateService.getState(userId);
        NextActionDto nextAction = nextActionService.decide(state);

        // ✅ v1.2-A: TODAY_DONE (nextAction 정합성 고정)
        if (state != null && state.quizSubmits() >= 1 && state.wrongReviews() == 0) {
        	nextAction = new NextActionDto(
        	        NextActionType.TODAY_DONE,
        	        "TODAY_DONE",
        	        null,
        	        false,
        	        "오늘 학습은 이미 시작됐어. 이제는 가볍게 유지하는 게 핵심이야."
        	);

        }

        String interpretation = interpretationService.buildInterpretation(state, nextAction);
        Copy copy = applyV11CopyRules(state, nextAction);

        return new StudyCoachResponse(
                state,
                interpretation,
                nextAction,
                copy.suggestion(),
                copy.reason()
        );

    }

    private Copy applyV11CopyRules(StudyStateDto state, NextActionDto nextAction) {
        // 기본값
        String suggestion = "지금 다음 행동을 실행해.";
        List<String> reason = List.of(
                "상태: 코치 상태를 확인했다",
                "왜: 다음 행동을 수행하면 루프가 진행된다",
                "효과: today/streak 집계가 즉시 반영된다"
        );
        String nextActionReason = "다음 행동 1개를 하면 루프가 열린다"; // ✅ v1.2-1

        // ✅ TODAY_DONE: nextAction 기준으로만 처리(중복 제거)
        if (nextAction != null && nextAction.type() == NextActionType.TODAY_DONE) {
            suggestion = "오늘은 이미 했어. 요약 1개만 보고 마무리하자.";
            reason = List.of(
                    "상태: 오늘 학습은 이미 완료했다",
                    "왜: 마무리 1개(요약)가 기억을 고정한다",
                    "효과: 부담 없이 streak를 안정화한다"
            );
            nextActionReason = "요약 1개만 보면 오늘 학습이 ‘완료’로 고정된다";
            return new Copy(suggestion, reason, nextActionReason);
        }

        // ✅ (오늘 목표 준수하려면 아래 2개는 주석 처리 권장)
        // STREAK_RISK
        if (state != null && !state.studiedToday() && state.streakDays() >= 3) {
            suggestion = "오늘 끊기기 직전이야. 최소 행동 1개만 하자.";
            reason = List.of(
                    "상태: 오늘 기록이 아직 없다",
                    "왜: 최소 행동 1개만 해도 streak를 지킬 수 있다",
                    "효과: 내일 코치 추천 정확도가 올라간다"
            );
            nextActionReason = "지금 최소 행동 1개가 streak를 살린다";
            return new Copy(suggestion, reason, nextActionReason);
        }

        // MANY_WRONGS
        if (state != null && state.wrongReviews() >= 3) {
            suggestion = "오답이 쌓였어. 오늘은 1개만 정리하자.";
            reason = List.of(
                    "상태: 약점(오답)이 누적됐다",
                    "왜: 오답은 빨리 처리할수록 고착을 막는다",
                    "효과: 다음 퀴즈 점수가 바로 오른다"
            );
            nextActionReason = "오답 1개만 처리해도 약점 누적이 멈춘다";
            return new Copy(suggestion, reason, nextActionReason);
        }

        // ===== 기존 규칙 =====
        if (nextAction != null && nextAction.type() == NextActionType.REVIEW_WRONG_ONE) {
            boolean forge = nextAction.requiresForge();

            suggestion = forge
                    ? "Forge부터 열고 오답 1개만 복습해. (딱 1개면 충분해)"
                    : "오답 1개만 복습해. (딱 1개면 충분해)";

            reason = List.of(
                    "상태: 오늘 오답 복습이 남아 있다",
                    forge ? "왜: Forge 단계가 필요해서 먼저 준비하고 들어가야 한다"
                            : "왜: 오답 1개만 처리해도 루프가 이어진다",
                    "효과: 약점이 줄고 streak가 유지된다"
            );

            nextActionReason = forge
                    ? "Forge 준비 후 오답 1개만 처리하면 약점 루프가 다시 돈다"
                    : "오답 1개만 처리해도 약점이 줄고 루프가 이어진다";

            return new Copy(suggestion, reason, nextActionReason);
        }

        if (nextAction != null && nextAction.type() == NextActionType.JUST_OPEN) {
            suggestion = "오늘은 오답 이벤트 1개만 찍고 시작하자. (start 신호)";
            reason = List.of(
                    "상태: 아직 오늘 학습 기록이 없다",
                    "왜: 첫 행동 1개가 있어야 코치가 다음 행동을 더 정확히 추천한다",
                    "효과: today/streak 집계가 움직이고 루프가 열린다"
            );
            nextActionReason = "첫 행동 1개가 있어야 코치 추천 루프가 열린다";

            return new Copy(suggestion, reason, nextActionReason);
        }

        return new Copy(suggestion, reason, nextActionReason);
    }

    private record Copy(String suggestion, List<String> reason, String nextActionReason) {}
}
