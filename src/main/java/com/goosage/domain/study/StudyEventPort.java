package com.goosage.domain.study;

import com.goosage.domain.EventType;

public interface StudyEventPort {

    void recordEvent(long userId,
                     EventType type,
                     String targetType,
                     Long targetId,
                     String meta);
}