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

        String interpretation = interpretationService.buildInterpretation(state, nextAction);

        Copy copy = applyV11CopyRules(nextAction);

        return new StudyCoachResponse(
                state,
                interpretation,
                nextAction,
                copy.suggestion(),
                copy.reason()
        );
    }

    private Copy applyV11CopyRules(NextActionDto nextAction) {
        // 기본값
        String suggestion = "지금 다음 행동을 실행해.";
        List<String> reason = List.of(
                "상태: 코치 상태를 확인했다",
                "왜: 다음 행동을 수행하면 루프가 진행된다",
                "효과: today/streak 집계가 즉시 반영된다"
        );

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
        }

        if (nextAction != null && nextAction.type() == NextActionType.JUST_OPEN) {
            suggestion = "오늘은 오답 이벤트 1개만 찍고 시작하자. (start 신호)";
            reason = List.of(
                    "상태: 아직 오늘 학습 기록이 없다",
                    "왜: 첫 행동 1개가 있어야 코치가 다음 행동을 더 정확히 추천한다",
                    "효과: today/streak 집계가 움직이고 루프가 열린다"
            );
        }

        return new Copy(suggestion, reason);
    }

    private record Copy(String suggestion, List<String> reason) {}
}
