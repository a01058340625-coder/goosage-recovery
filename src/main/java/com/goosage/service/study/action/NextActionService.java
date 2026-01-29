package com.goosage.service.study.action;

import org.springframework.stereotype.Service;
import com.goosage.service.study.dto.StudyStateDto;

@Service
public class NextActionService {

    public NextActionDto decide(StudyStateDto s) {
        
        if (s != null && !s.studiedToday()) {
            return new NextActionDto(
                    NextActionType.JUST_OPEN,
                    "오늘은 가볍게 1개만 시작할까?",
                    null,
                    false,
                    "오늘의 첫 진입이야. 1분만 열어도 학습 루프가 시작돼."
            );
        }

        // 2) 최근 지식이 있고 → 오답 복습 유도
        if (s != null && s.recentKnowledgeId() != null) {
            return new NextActionDto(
                    NextActionType.REVIEW_WRONG_ONE,
                    "오답 1개만 더 볼까?",
                    s.recentKnowledgeId(),
                    true,
                    "최근 오답 1개를 바로 잡는 게 가장 효율적인 복습이야."
            );
        }

        // 3) 기본 fallback
        return new NextActionDto(
                NextActionType.JUST_OPEN,
                "오늘은 여기서 가볍게 시작할까?",
                null,
                false,
                "작은 행동 1개가 학습 흐름을 다시 연다."
        );
    }
}
