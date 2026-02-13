package com.goosage.domain.study;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Domain Port: infra 타입 import 금지
 */
public interface StudyReadPort {

    Optional<TodayRow> findToday(long userId, LocalDate today);

    LocalDateTime lastEventAtAll(long userId);

    int calcStreakDays(long userId, LocalDate today);
}
