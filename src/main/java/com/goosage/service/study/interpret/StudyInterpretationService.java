package com.goosage.service.study.interpret;

import com.goosage.service.study.dto.StudyStateDto;
import org.springframework.stereotype.Service;

@Service
public class StudyInterpretationService {

    public String interpret(StudyStateDto s) {

        if (!s.studiedToday()) {
            if (s.streakDays() > 0) {
                return "오늘은 아직 시작하지 않았다. " + s.streakDays() + "일 연속 흐름이 유지 중이다.";
            }
            return "오늘은 아직 시작하지 않았다.";
        }

        if (s.quizSubmits() > 0 || s.wrongReviews() > 0) {
            return "오늘 학습이 기록되었다. "
                    + "퀴즈 제출 " + s.quizSubmits() + "회, 오답 복습 " + s.wrongReviews() + "회.";
        }

        return "오늘 학습이 기록되었다.";
    }
}
