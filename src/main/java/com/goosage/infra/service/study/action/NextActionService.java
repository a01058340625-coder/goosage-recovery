package com.goosage.infra.service.study.action;

import org.springframework.stereotype.Service;
import com.goosage.domain.study.StudyState;

import com.goosage.domain.NextActionType;
import com.goosage.infra.service.study.dto.StudyStateDto;

@Service
public class NextActionService {

    public NextActionDto decide(StudyStateDto s) {

        // 0) 안전장치: 상태 자체가 없으면 시작 유도
        if (s == null) {
            return new NextActionDto(
                    NextActionType.JUST_OPEN,
                    "오늘은 가볍게 1개만 시작할까?",
                    null,
                    false,
                    "상태를 못 읽었어. 그래도 1분만 열면 루프가 다시 돈다."
            );
        }

        // 1) 오늘 이벤트 0 → JUST_OPEN
        if (!s.studiedToday()) {
            return new NextActionDto(
                    NextActionType.JUST_OPEN,
                    "오늘은 가볍게 1개만 시작할까?",
                    null,
                    false,
                    "오늘의 첫 진입이야. 1분만 열어도 학습 루프가 시작돼."
            );
        }

        // 2) 오늘 오답복습이 존재 + 리뷰 대상 지식이 있음 → REVIEW
        if (s.wrongReviews() > 0 && s.recentKnowledgeId() != null) {
            return new NextActionDto(
                    NextActionType.REVIEW_WRONG_ONE,
                    "오답 1개만 더 볼까?",
                    s.recentKnowledgeId(),
                    true,
                    "오늘 남아있는 오답 1개만 잡아도 효율이 확 올라가."
            );
        }

        // 3) 오늘 퀴즈 제출이 있음 → TODAY_DONE (enum에 있으면)
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
    
    public NextActionDto decide(StudyState s) {

        // 0) 안전장치
        if (s == null) {
            return new NextActionDto(
                    NextActionType.JUST_OPEN,
                    "오늘은 가볍게 1개만 시작할까?",
                    null,
                    false,
                    "상태를 못 읽었어. 그래도 1분만 열면 루프가 다시 돈다."
            );
        }

        // ✅ 기존 로직이 dto 기반이라면, '판단에 필요한 최소 필드'만 옮겨서 여기서 판단해.
        // (네 현재 state는 wrongReviews/quizSubmits/eventsCount 3개니까 이 3개로 분기만 구성)

        int wrongReviews = s.wrongReviews();
        int quizSubmits  = s.quizSubmits();
        int eventsCount  = s.eventsCount();

        // 아래는 예시. 네 기존 decide(dto) 로직의 핵심 분기만 그대로 옮겨.
        if (eventsCount <= 0) {
            return new NextActionDto(
                    NextActionType.JUST_OPEN,
                    "오늘은 가볍게 1개만 시작할까?",
                    null,
                    false,
                    "이벤트가 아직 없어. 1개만 열면 루프가 시작돼."
            );
        }

        if (wrongReviews > 0) {
            return new NextActionDto(
                NextActionType.TODAY_DONE,
                "오답이 남아있어. 1개만 복습하고 가자.",
                null,
                true,
                "오답이 남아있어. 1개만 줄이면 상태가 확 좋아져."
            );
        }

        if (quizSubmits <= 0) {
            return new NextActionDto(
                NextActionType.TODAY_DONE,
                "퀴즈 1개만 제출해서 루프를 완성하자.",
                null,
                false,
                "퀴즈 제출이 없으면 루프가 끊겨. 1개만 하자."
            );
        }


        return new NextActionDto(
                NextActionType.TODAY_DONE,
                "오늘은 이미 한 번 돌렸어. 내일을 위해 가볍게 정리할까?",
                null,
                false,
                "오늘 루프는 완료! 내일은 오답/템플릿 중 하나만 이어가면 돼."
        );
    }
}
