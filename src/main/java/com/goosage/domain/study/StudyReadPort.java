package com.goosage.domain.study;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface StudyReadPort {
    Optional<TodayRow> findToday(long userId, LocalDate nowDate);
    Optional<LocalDateTime> lastEventAtAll(long userId);
    int calcStreakDays(long userId, LocalDate today);
    int recentEventCount3d(long userId, LocalDate today);
    int todayEventCountFromEvents(long userId, LocalDate today);
    int recentWrong3d(long userId, LocalDate today);
    int recentWrongDone3d(long userId, LocalDate today);
    int todayWrongFromEvents(long userId, LocalDate today);
    int todayWrongDoneFromEvents(long userId, LocalDate today);
    int todayQuizFromEvents(long userId, LocalDate today);
}