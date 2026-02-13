package com.goosage.infra.service.study.action;

import org.springframework.stereotype.Service;

import com.goosage.domain.NextActionType;
import com.goosage.domain.study.StudySnapshot;

@Service
public class NextActionService {

    public NextActionDto decide(StudySnapshot snap) {

        // 0) 안전장치: 상태 자체가 없으면 시작 유도
        if (snap == null) {
            return new NextActionDto(
                    NextActionType.JUST_OPEN,
                    "오늘은 가볍게 1개만 시작할까?",
                    null,
                    false,
                    "상태를 못 읽었어. 그래도 1분만 열면 루프가 다시 돈다."
            );
        }

        var s = snap.state();

        // 1) 오늘 이벤트 0 → JUST_OPEN
        if (!snap.studiedToday()) {
            return new NextActionDto(
                    NextActionType.JUST_OPEN,
                    "오늘은 가볍게 1개만 시작할까?",
                    null,
                    false,
                    "오늘의 첫 진입이야. 1분만 열어도 학습 루프가 시작돼."
            );
        }

        // 2) 오늘 오답복습이 존재 + 리뷰 대상 지식이 있음 → REVIEW
        if (s.wrongReviews() > 0 && snap.recentKnowledgeId() != null) {
            return new NextActionDto(
                    NextActionType.REVIEW_WRONG_ONE,
                    "오답 1개만 더 볼까?",
                    snap.recentKnowledgeId(),
                    true,
                    "오늘 남아있는 오답 1개만 잡아도 효율이 확 올라가."
            );
        }

        // 3) 오늘 퀴즈 제출이 있음 → TODAY_DONE
        if (s.quizSubmits() > 0) {
            return new NextActionDto(
                    NextActionType.TODAY_DONE,
                    "오늘은 이미 한 번 돌렸어. 내일을 위해 가볍게 정리할까?",
                    null,
                    false,
                    "오늘 루프는 완료! 내일은 오답/템플릿 중 하나만 이어가면 돼."
            );
        }

        // 4) 기본 fallback
        return new NextActionDto(
                NextActionType.JUST_OPEN,
                "오늘은 여기서 가볍게 시작할까?",
                null,
                false,
                "작은 행동 1개가 학습 흐름을 다시 연다."
        );
    }
}
