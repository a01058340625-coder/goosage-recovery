package com.goosage.service.study;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.stereotype.Service;

import com.goosage.forge.ForgePrepareResult;
import com.goosage.forge.ForgeStatus;
import com.goosage.forge.ForgeTriggerService;
import com.goosage.service.study.action.NextActionDto;
import com.goosage.service.study.action.NextActionService;
import com.goosage.service.study.action.NextActionType;
import com.goosage.service.study.dto.RiskLevel;
import com.goosage.service.study.dto.StudyCoachResponse;
import com.goosage.service.study.dto.StudyStateDto;
import com.goosage.service.study.interpret.StudyInterpretationService;

@Service
public class StudyCoachService {

    private record Copy(String suggestion, List<String> reason, String nextActionReason) {
    }
    
    private static final Logger log = LoggerFactory.getLogger(StudyCoachService.class);

    private final StudyStateService studyStateService;
    private final StudyInterpretationService interpretationService;
    private final NextActionService nextActionService;
    private final ForgeTriggerService forgeTriggerService;

    public StudyCoachService(
            StudyStateService studyStateService,
            StudyInterpretationService interpretationService,
            NextActionService nextActionService,
            ForgeTriggerService forgeTriggerService) {
        this.studyStateService = studyStateService;
        this.interpretationService = interpretationService;
        this.nextActionService = nextActionService;
        this.forgeTriggerService = forgeTriggerService;
    }

    public StudyCoachResponse coach(long userId) {

        String forgeMode = null;
        String forgeError = null;

        StudyStateDto state = studyStateService.getState(userId);
        
        log.info("[COACH_START] userId={} ymd={} studiedToday={} streakDays={} events={} quiz={} wrong={} recentKid={}",
                userId,
                state != null ? state.ymd() : null,
                state != null ? state.studiedToday() : null,
                state != null ? state.streakDays() : null,
                state != null ? state.eventsCount() : null,
                state != null ? state.quizSubmits() : null,
                state != null ? state.wrongReviews() : null,
                state != null ? state.recentKnowledgeId() : null
        );


        // 1) 기본 행동 결정
        NextActionDto nextAction = nextActionService.decide(state);

        // 2) TODAY_DONE 정합성 고정(최우선) - nextAction 자체를 확정해버림
        boolean todayDone =
                state != null
                && (state.studiedToday() || state.eventsCount() >= 1)
                && state.quizSubmits() >= 1
                && state.wrongReviews() == 0;

        if (todayDone) {
            nextAction = new NextActionDto(
                    NextActionType.TODAY_DONE,
                    "TODAY_DONE",
                    null,
                    false,
                    "오늘 루프는 이미 닫혔어. 이제는 유지/정리 모드야."
            );
        }

        // 3) Risk 계산 (TODAY_DONE이면 SAFE로 잠금해서 충돌 제거)
        RiskLevel riskLevel = calculateRisk(state);
        if (nextAction != null && nextAction.type() == NextActionType.TODAY_DONE) {
            riskLevel = RiskLevel.SAFE;
        }

        // 4) v1.2: requiresForge=true면 템플릿 자동 준비 + 실패 시 fallback
        //    (TODAY_DONE은 requiresForge=false라 자동 스킵됨)
        if (nextAction != null
                && nextAction.type() != NextActionType.TODAY_DONE
                && nextAction.requiresForge()
                && nextAction.knowledgeId() != null) {

            ForgePrepareResult prep =
                    forgeTriggerService.prepare(nextAction.knowledgeId(), "summary-v1");

            forgeMode = prep.status().name();
            forgeError = prep.message();

            if (prep.status() == ForgeStatus.FAILED) {
                nextAction = NextActionDto.justOpenFallback();
            }
        }

        // 5) 해석/문구 생성 (항상 여기서 1번만)
        String interpretation = interpretationService.buildInterpretation(state, nextAction);
        Copy copy = applyV11CopyRules(state, nextAction, riskLevel);
        
        log.info("[COACH_END] userId={} todayDone={} nextAction={} kid={} forgeReq={} risk={} forgeMode={} forgeError={}",
                userId,
                (nextAction != null && nextAction.type() == NextActionType.TODAY_DONE),
                nextAction != null ? nextAction.type() : null,
                nextAction != null ? nextAction.knowledgeId() : null,
                nextAction != null ? nextAction.requiresForge() : null,
                riskLevel,
                forgeMode,
                forgeError
        );


        // 6) 응답 (return 1번만)
        return new StudyCoachResponse(
                state,
                interpretation,
                nextAction,
                forgeMode,
                forgeError,
                copy.suggestion(),
                copy.reason(),
                riskLevel
        );
    }

    private Copy applyV11CopyRules(StudyStateDto state, NextActionDto nextAction, RiskLevel riskLevel) {

        int quizSubmits = state.quizSubmits();
        int wrongReviews = state.wrongReviews();
        boolean studiedToday = state.studiedToday();
        int streakDays = state.streakDays();

        // ✅ (A) TODAY_DONE 최우선: 마감 톤이 Risk보다 먼저 나오게 잠금
        if (nextAction != null && nextAction.type() == NextActionType.TODAY_DONE) {
            return new Copy(
                    "오늘은 이미 한 번 닫았어.\n지금은 ‘유지’가 승리야.\n내일을 가볍게 준비하자.",
                    List.of(
                            "오늘 학습 이벤트가 있고 퀴즈도 제출했어",
                            "오답 리뷰도 0이라 루프가 깔끔하게 닫혔어",
                            "이제는 과열보다 유지가 더 중요해"
                    ),
                    "TODAY_DONE: 유지/정리 모드"
            );
        }

        // 기본값
        String suggestion = "지금 다음 행동을 실행해.";
        List<String> reason = List.of(
                "상태: 코치 상태를 확인했다",
                "왜: 다음 행동을 수행하면 루프가 진행된다",
                "효과: today/streak 집계가 즉시 반영된다"
        );
        String nextActionReason = "다음 행동 1개를 하면 루프가 열린다";

        // ✅ (B) Risk 우선 (TODAY_DONE 다음)
        if (riskLevel == RiskLevel.DANGER) {
            // streakDays=0인 케이스가 DANGER이므로 streak 언급 금지(정합성)
            suggestion = "지금은 시작 신호가 0이야.\n딱 1개만 하면 충분해.\n완벽 말고, 연결만 만들자.";
            reason = List.of(
                    "오늘 학습 이벤트가 0이라 흐름이 아직 시작되지 않았어",
                    "시작은 ‘양’이 아니라 ‘신호 1개’로 만들어진다",
                    "최소 행동 1개면 위험을 바로 해소할 수 있다"
            );
            nextActionReason = "첫 행동 1개가 있어야 추천 루프가 열린다";
            return new Copy(suggestion, reason, nextActionReason);
        }

        if (riskLevel == RiskLevel.WARNING) {
            // WARNING은 streakDays>0 케이스라 streak 언급 가능
            suggestion = "끊기기 직전이야.\n부담 없이 1개만 찍자.\n연결이 목표다.";
            reason = List.of(
                    "오늘 학습 이벤트가 없어서 streak가 끊길 위험이 있어",
                    "오늘 1개만 해도 연속성이 유지된다",
                    "지금은 양보다 연결이 중요하다"
            );
            nextActionReason = "오늘 1개만 해도 흐름이 유지된다";
            return new Copy(suggestion, reason, nextActionReason);
        }

        // (C) 템플릿: ‘정리/마감’ 톤 (타입 변경 없이 문장만)
        // NOTE: 이건 TODAY_DONE과 다름. (TODAY_DONE은 strict, 이건 soft)
        if (quizSubmits >= 1 && wrongReviews == 0) {
            suggestion = "오늘은 제출까지 했네. 이제 ‘정리’만 하면 끝.\n"
                    + "지금은 새로 늘리기보다, 오늘 만든 흐름을 고정해.\n"
                    + "남는 10분이면 ‘1개만’ 노트로 정리하고 종료하자.";
            reason = List.of(
                    "제출까지 했다는 건 오늘 루프가 이미 성립했다는 뜻",
                    "오답 복습이 0이면 부담 낮게 ‘정리’로 마감하기 좋다",
                    "오늘을 ‘완료 느낌’으로 찍어야 내일 루틴이 쉬워진다"
            );
            nextActionReason = "마무리 1개를 하면 오늘 학습이 더 단단해진다";
            return new Copy(suggestion, reason, nextActionReason);
        }

        // (D) MANY_WRONGS - 타입 변경 없이 문장만
        if (wrongReviews >= 3) {
            suggestion = "오답이 쌓였으니 새로 만들지 말고 ‘오답 1개만’ 처리하자.\n"
                    + "오늘은 REVIEW_WRONG_ONE 한 번만 성공시키면 충분해.\n"
                    + "지금은 넓히기보다, 약점 루프를 닫는 게 효율이 제일 크다.";
            reason = List.of(
                    "오답이 3개 이상이면 확장하면 더 복잡해진다",
                    "오답 1개를 닫는 순간 nextAction 품질이 바로 좋아진다",
                    "학습 체감은 ‘새로’가 아니라 ‘막힌 곳이 풀릴 때’ 온다"
            );
            nextActionReason = "오답 1개만 닫아도 약점 루프가 다시 돈다";
            return new Copy(suggestion, reason, nextActionReason);
        }

        // (E) REVIEW_WRONG_ONE (기존 규칙 유지)
        if (nextAction != null && nextAction.type() == NextActionType.REVIEW_WRONG_ONE) {
            boolean forge = nextAction.requiresForge();
            suggestion = forge
                    ? "Forge부터 열고 오답 1개만 복습해. (딱 1개면 충분해)"
                    : "오답 1개만 복습해. (딱 1개면 충분해)";

            reason = List.of(
                    "상태: 오늘 오답 복습이 남아 있다",
                    forge ? "왜: Forge 단계가 필요해서 먼저 준비하고 들어가야 한다"
                         : "왜: 오답 1개만 처리해도 루프가 이어진다",
                    "효과: 약점이 줄고 루프가 유지된다"
            );

            nextActionReason = forge
                    ? "Forge 준비 후 오답 1개만 처리하면 약점 루프가 다시 돈다"
                    : "오답 1개만 처리해도 약점이 줄고 루프가 이어진다";

            return new Copy(suggestion, reason, nextActionReason);
        }

        // (F) JUST_OPEN (기존 규칙 유지)
        if (nextAction != null && nextAction.type() == NextActionType.JUST_OPEN) {
            suggestion = "오늘은 ‘시작 신호’ 1개만 찍자. (JUST_OPEN)\n"
                    + "딱 1개면 충분해.";
            reason = List.of(
                    "상태: 아직 오늘 학습 기록이 없다",
                    "왜: 첫 행동 1개가 있어야 코치가 다음 행동을 더 정확히 추천한다",
                    "효과: today/streak 집계가 움직이고 루프가 열린다"
            );
            nextActionReason = "첫 행동 1개가 있어야 코치 추천 루프가 열린다";
            return new Copy(suggestion, reason, nextActionReason);
        }

        // (G) 기본 반환
        return new Copy(suggestion, reason, nextActionReason);
    }

    private RiskLevel calculateRisk(StudyStateDto state) {
        if (state == null) return RiskLevel.DANGER; // 상태 모르면 보수적으로

        // 1) 오늘 학습을 했으면 무조건 SAFE
        if (state.studiedToday() || state.eventsCount() >= 1) {
            return RiskLevel.SAFE;
        }

        // 2) 오늘 안 했는데 스트릭이 있으면 WARNING
        if (state.streakDays() > 0) {
            return RiskLevel.WARNING;
        }

        // 3) 오늘 안 했고 스트릭도 없으면 DANGER
        return RiskLevel.DANGER;
    }
}
