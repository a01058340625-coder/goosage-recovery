package com.goosage.infra.service.study.mapper;

import com.goosage.domain.study.StudyState;
import com.goosage.infra.service.study.dto.StudyStateDto;

public class StudyStateMapper {
    private StudyStateMapper() {}

    public static StudyState toDomain(StudyStateDto dto) {
        return new StudyState(dto.wrongReviews(), dto.quizSubmits(), dto.eventsCount());
    }
}
