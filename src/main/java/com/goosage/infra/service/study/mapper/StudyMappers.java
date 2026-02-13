package com.goosage.infra.service.study.mapper;

import com.goosage.api.view.study.StudyStateView;
import com.goosage.domain.study.StudySnapshot;
import com.goosage.domain.study.StudyState;
import com.goosage.infra.service.study.dto.StudyStateDto;

/**
 * Study 영역의 "표준 변환"을 한 곳에 모은다.
 * - DTO ↔ Domain
 * - Snapshot/DTO → View
 *
 * 원칙:
 * - controller/view 변환은 여기서만
 * - infra dto 변환도 여기서만
 */
public final class StudyMappers {

    private StudyMappers() {}

    /** DTO -> Domain */
    public static StudyState toDomain(StudyStateDto dto) {
        if (dto == null) {
            return new StudyState(0, 0, 0);
        }
        return new StudyState(dto.wrongReviews(), dto.quizSubmits(), dto.eventsCount());
    }

    /** Snapshot -> View (권장: Snapshot 단일화 경로) */
    public static StudyStateView toView(StudySnapshot s) {
        if (s == null) {
            // null을 허용할지/예외를 던질지는 취향인데, 지금은 안전값 리턴
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

    /** DTO -> View (과도기 지원: DTO 남아있는 동안만 사용) */
    public static StudyStateView toView(StudyStateDto d) {
        if (d == null) {
            return new StudyStateView(
                    null, false, 0,
                    0, 0, 0,
                    null, null
            );
        }

        // 기존 코드의 getter 스타일 + record 스타일이 섞여있어서,
        // 네 StudyStateDto 구현에 맞춰 아래 둘 중 하나만 남기면 된다.

        // ✅ record 스타일(현재 네 코드가 섞여있지만 이 쪽이 더 "정공법")
        return new StudyStateView(
                d.ymd(),
                d.studiedToday(),
                d.streakDays(),
                d.eventsCount(),
                d.quizSubmits(),
                d.wrongReviews(),
                d.lastEventAt(),
                d.recentKnowledgeId()
        );

        /*
        // ✅ getter 스타일(만약 StudyStateDto가 record가 아니라면 이걸 사용)
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
        */
    }
}
