package com.goosage.service.study.action;

import com.goosage.service.study.dto.StudyStateDto;
import org.springframework.stereotype.Service;

@Service
public class NextActionService {

    public NextActionDto decide(StudyStateDto s) {

        if (!s.studiedToday()) {
            return new NextActionDto(
                    NextActionType.JUST_OPEN,
                    "오늘은 가볍게 1개만 시작할까?",
                    null,
                    false
            );
        }

        // ✅ recentKnowledgeId 있으면 그걸로 오답 복습 추천
        if (s.recentKnowledgeId() != null) {
            return new NextActionDto(
                    NextActionType.REVIEW_WRONG_ONE,
                    "오답 1개만 더 볼까?",
                    s.recentKnowledgeId(),
                    true
            );
        }

        // ✅ 없으면 fallback (퀴즈를 아직 한 적이 없는 유저)
        return new NextActionDto(
                NextActionType.JUST_OPEN,
                "퀴즈 기록이 없어서, 오늘은 가볍게 1개만 시작할까?",
                null,
                false
        );
    }
}



