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
                    false
            );
        }

        if (s != null && s.recentKnowledgeId() != null) {
            return new NextActionDto(
                    NextActionType.REVIEW_WRONG_ONE,
                    "오답 1개만 더 볼까?",
                    s.recentKnowledgeId(),
                    true
            );
        }

        return new NextActionDto(
                NextActionType.JUST_OPEN,
                "오늘은 여기서 가볍게 시작할까?",
                null,
                false
        );
    }
}
