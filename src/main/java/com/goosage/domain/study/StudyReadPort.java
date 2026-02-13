package com.goosage.domain.study;

import java.util.Optional;

public interface StudyReadPort {
    Optional<StudyTodayRow> findToday(long userId);
}
