package com.goosage.infra.service.study.mapper;

import com.goosage.api.view.study.StudyStateView;
import com.goosage.domain.study.StudySnapshot;

public class StudyStateViewMapper2 {
    private StudyStateViewMapper2() {}

    public static StudyStateView toView(StudySnapshot s) {
        return new StudyStateView(
                s.ymd(),
                s.studiedToday(),
                s.streakDays(),
                s.state().eventsCount(),
                s.state().quizSubmits(),
                s.state().wrongReviews(),
                s.lastEventAt(),
                s.recentKnowledgeId()
        );
    }
}
