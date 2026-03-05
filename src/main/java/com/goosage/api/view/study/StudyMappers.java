package com.goosage.api.view.study;

import com.goosage.domain.study.StudySnapshot;

public final class StudyMappers {

    private StudyMappers() {}

    public static StudyStateView toView(StudySnapshot s) {
        if (s == null) {
            return new StudyStateView(
                    null, false, 0,
                    0, 0, 0,
                    null, null
            );
        }

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
