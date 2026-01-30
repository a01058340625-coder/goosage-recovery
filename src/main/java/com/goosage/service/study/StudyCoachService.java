package com.goosage.service.study;

import java.util.List;

import org.springframework.stereotype.Service;

import com.goosage.service.study.action.NextActionDto;
import com.goosage.service.study.action.NextActionService;
import com.goosage.service.study.action.NextActionType;
import com.goosage.service.study.dto.StudyCoachResponse;
import com.goosage.service.study.dto.StudyStateDto;
import com.goosage.service.study.forge.ForgeTriggerService;
import com.goosage.service.study.forge.ForgeTriggerService.ForgePrepareResult;
import com.goosage.service.study.interpret.StudyInterpretationService;

@Service
public class StudyCoachService {

    private final StudyStateService studyStateService;
    private final StudyInterpretationService interpretationService;
    private final NextActionService nextActionService;
    private final ForgeTriggerService forgeTriggerService;

    public StudyCoachService(
            StudyStateService studyStateService,
            StudyInterpretationService interpretationService,
            NextActionService nextActionService,
            ForgeTriggerService forgeTriggerService
    ) {
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

        // 2) TODAY_DONE 정합성 고정(최우선)
        if (state != null && state.eventsCount() >= 1 && state.quizSubmits() >= 1) {
            nextAction = new NextActionDto(
                    NextActionType.TODAY_DONE,
                    "TODAY_DONE",
                    null,
                    false,
                    "오늘 학습은 이미 시작됐어. 이제는 가볍게 유지하는 게 핵심이야."
            );
        }

     // 3) v1.2: requiresForge=true면 템플릿 자동 준비 + 실패 시 fallback
     // (단, TODAY_DONE이면 forge 준비할 필요가 없으니 스킵)
        if (nextAction != null
                && nextAction.type() != NextActionType.TODAY_DONE
                && nextAction.requiresForge()
                && nextAction.knowledgeId() != null) {

            ForgeTriggerService.ForgePrepareResult prep =
                    forgeTriggerService.prepare(nextAction.knowledgeId(), "summary-v1");

            // ✅ 추가: 응답에 실을 값 저장
            forgeMode = prep.mode();      // "REUSE" / "CREATED" / "FAILED"
            forgeError = prep.error();    // 실패 메시지(없으면 null)

            if (!prep.success()) {
                // 실패를 명확히 표시하고 싶으면 mode를 FAILED로 강제도 가능
                // forgeMode = "FAILED";

                nextAction = new NextActionDto(
                        NextActionType.JUST_OPEN,
                        "Forge 준비 실패: 오늘은 가볍게 시작하자",
                        null,
                        false,
                        "Forge 준비에 실패해도 루프는 끊기지 않는다. 오늘은 1개만 열자."
                );
            }
        }



        // 4) 해석/문구 생성
        String interpretation = interpretationService.buildInterpretation(state, nextAction);
        Copy copy = applyV11CopyRules(state, nextAction);

        // 5) 응답 (return 1번만)
        return new StudyCoachResponse(
                state,
                interpretation,
                nextAction,
                forgeMode,
                forgeError,
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
        String nextActionReason = "다음 행동 1개를 하면 루프가 열린다";

        // TODAY_DONE
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

        // REVIEW_WRONG_ONE
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

        // JUST_OPEN
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
