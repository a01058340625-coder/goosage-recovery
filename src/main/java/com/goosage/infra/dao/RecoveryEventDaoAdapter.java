package com.goosage.infra.dao;

import org.springframework.stereotype.Component;
import com.goosage.domain.EventType;
import com.goosage.domain.recovery.RecoveryEventPort;

@Component
public class RecoveryEventDaoAdapter implements RecoveryEventPort {

    private final RecoveryEventDao studyEventDao;

    public RecoveryEventDaoAdapter(RecoveryEventDao studyEventDao) {
        this.studyEventDao = studyEventDao;
    }

    @Override
    public void recordEvent(long userId, EventType eventType, String refType, Long refId, String payloadJson) {
        studyEventDao.recordEvent(userId, eventType, refType, refId, payloadJson);
    }
}