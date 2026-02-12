package com.goosage.infra.service.study.mapper;

import com.goosage.api.view.study.StudyStateView;
import com.goosage.infra.service.study.dto.StudyStateDto;

public class StudyStateViewMapper {

    private StudyStateViewMapper() {}

    public static StudyStateView toView(StudyStateDto d) {
        return new StudyStateView(
            d.getYmd(),
            d.isStudiedToday(),
            d.getStreakDays(),
            d.getEventsCount(),
            d.getQuizSubmits(),
            d.getWrongReviews(),
            d.getLastEventAt(),
            d.getRecentKnowledgeId()
        );
    }
}
