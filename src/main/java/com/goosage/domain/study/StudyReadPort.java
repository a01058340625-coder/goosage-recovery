package com.goosage.domain.study;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Domain Port: infra 타입 import 금지
 */
public interface StudyReadPort {
    Optional<TodayRow> findToday(long userId, LocalDate nowDate);
    Optional<LocalDateTime> lastEventAtAll(long userId);
    int calcStreakDays(long userId, LocalDate today);
    int recentEventCount3d(long userId, LocalDate today);
    int todayEventCountFromEvents(long userId, LocalDate today);
    int recentWrong3d(long userId, LocalDate today);
    int recentWrongDone3d(long userId, LocalDate today);
}
