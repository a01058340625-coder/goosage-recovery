package com.goosage.service.study.interpret;

import java.util.List;

import org.springframework.stereotype.Service;

import com.goosage.service.study.action.NextActionDto;
import com.goosage.service.study.dto.StudyStateDto;

@Service
public class StudyInterpretationService {

    // v1.1은 나중에. 지금은 컴파일 안전/서버 안정화가 우선.
    public String buildInterpretation(StudyStateDto state, NextActionDto nextAction) {
        return "OK";
    }

    public String buildSuggestion(StudyStateDto state, NextActionDto nextAction) {
        return "지금 다음 행동을 실행해.";
    }

    public List<String> buildReason(StudyStateDto state, NextActionDto nextAction) {
        return List.of(
                "상태: 코치 상태를 확인했다",
                "왜: 다음 행동을 수행하면 루프가 진행된다",
                "효과: today/streak 집계가 즉시 반영된다"
        );
    }
    
    public StudyStateDto getState(Long userId) {
        // 임시: 컴파일/부팅 안정화용
        return new StudyStateDto(
                java.time.LocalDate.now(),
                false,
                0,
                0,
                0,
                0,
                null,
                null
        );
    }

}
