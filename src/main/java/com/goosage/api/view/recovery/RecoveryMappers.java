package com.goosage.api.view.recovery;

import com.goosage.domain.recovery.RecoverySnapshot;

public final class RecoveryMappers {

    private RecoveryMappers() {}

    public static RecoveryStateView toView(RecoverySnapshot s) {
        if (s == null) {
            return new RecoveryStateView(
                    null,
                    false,
                    0,
                    0,
                    0,
                    0,
                    0,
                    null,
                    null
            );
        }

        return new RecoveryStateView(
                s.ymd(),
                s.studiedToday(),
                s.streakDays(),
                s.state().eventsCount(),
                s.state().quizSubmits(),
                s.state().wrongReviews(),
                s.state().wrongReviewDoneCount(),
                s.lastEventAt(),
                s.recentKnowledgeId()
        );
    }
}