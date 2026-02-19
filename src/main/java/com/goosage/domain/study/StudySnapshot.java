package com.goosage.domain.study;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * StudySnapshot = SSOT(단일 진실)
 * - Coach / Prediction / NextAction이 참고하는 "읽기 전용" 스냅샷
 * - Rule/Engine/Controller는 DB를 직접 보지 않는다. (Snapshot만 본다)
 */
public record StudySnapshot(
        LocalDate ymd,                 // 오늘 날짜(기준일)
        StudyState state,              // 집계 상태(events/quiz/wrong 등)
        boolean studiedToday,          // 오늘 학습 여부(오늘 이벤트 존재)
        int streakDays,                // 연속 학습일(정책 확정 필요)
        LocalDateTime lastEventAt,      // 전체 마지막 이벤트 시각
        int daysSinceLastEvent,         // 마지막 이벤트로부터 경과 일수
        int recentEventCount3d,         // 최근 3일 이벤트 수(진짜 SQL 집계)
        Long recentKnowledgeId          // 최근 지식/노트 ID (추후 실제 조회)
) {}
