package com.goosage.domain.recovery;

import com.goosage.domain.EventType;

public interface RecoveryEventPort {

    void recordEvent(long userId,
                     EventType type,
                     String targetType,
                     Long targetId,
                     String meta);
}