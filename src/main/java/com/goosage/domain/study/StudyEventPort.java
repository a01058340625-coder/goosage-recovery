package com.goosage.domain.study;

public interface StudyEventPort {

    void recordEvent(long userId,
                     String type,
                     String targetType,
                     Long targetId,
                     String meta);
}
