package com.goosage.domain.recovery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface RecoveryReadPort {
    Optional<TodayRow> findToday(long userId, LocalDate nowDate);
    Optional<LocalDateTime> lastEventAtAll(long userId);
    int calcStreakDays(long userId, LocalDate today);
    int recentEventCount3d(long userId, LocalDate today);
    int todayEventCountFromEvents(long userId, LocalDate today);

    int recentRiskSignal3d(long userId, LocalDate today);
    int recentRecoveryAction3d(long userId, LocalDate today);

    int todayRiskSignalFromEvents(long userId, LocalDate today);
    int todayRecoveryActionFromEvents(long userId, LocalDate today);
    int todayActionFromEvents(long userId, LocalDate today);
}