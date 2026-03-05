package com.goosage.app.study.action;

import org.springframework.stereotype.Service;

import com.goosage.api.view.study.CoachPredictionReasonCode;
import com.goosage.api.view.study.NextActionDto;
import com.goosage.domain.NextActionType;
import com.goosage.domain.study.StudySnapshot;

@Service
public class NextActionService {

    public NextActionDto decide(StudySnapshot snap, CoachPredictionReasonCode reasonCode) {

        // 0) 안전장치
        if (snap == null) {
            return new NextActionDto(
                    NextActionType.JUST_OPEN,
                    "오늘은 가볍게 1개만 시작할까?",
                    null,
                    false,
                    "상태를 못 읽었어. 그래도 1분만 열면 루프가 다시 돈다."
            );
        }

        // ✅ Prediction 우선 정책: 충돌 방지 핵심
        if (reasonCode == CoachPredictionReasonCode.TODAY_DONE) {
            return new NextActionDto(
                    NextActionType.TODAY_DONE,
                    "오늘은 이미 한 번 돌렸어. 내일을 위해 가볍게 정리할까?",
                    null,
                    false,
                    "오늘 루프는 완료! 내일은 오답/템플릿 중 하나만 이어가면 돼."
            );
        }

        // ✅ 오늘 첫 진입 (이유를 분리해서 남기기)
        if (!snap.studiedToday()) {
            return new NextActionDto(
                    NextActionType.JUST_OPEN,
                    "오늘은 가볍게 1개만 시작할까?",
                    null,
                    false,
                    "오늘의 첫 진입이야. 이벤트 1개만 생기면 루프가 시작돼."
            );
        }

        var s = snap.state();
        if (s == null) {
            return new NextActionDto(
                    NextActionType.JUST_OPEN,
                    "오늘은 가볍게 1개만 시작할까?",
                    null,
                    false,
                    "상태(state)가 비어있어. 이벤트 1개만 넣어서 다시 스냅샷을 만들자."
            );
        }

        // 2) 오답복습
        if (s.wrongReviews() > 0 && snap.recentKnowledgeId() != null) {
            return new NextActionDto(
                    NextActionType.REVIEW_WRONG_ONE,
                    "오답 1개만 더 볼까?",
                    snap.recentKnowledgeId(),
                    true,
                    "오늘 남아있는 오답 1개만 잡아도 효율이 확 올라가."
            );
        }

        // 4) fallback
        return new NextActionDto(
                NextActionType.JUST_OPEN,
                "오늘은 여기서 가볍게 시작할까?",
                null,
                false,
                "작은 행동 1개가 학습 흐름을 다시 연다."
        );
    }
}