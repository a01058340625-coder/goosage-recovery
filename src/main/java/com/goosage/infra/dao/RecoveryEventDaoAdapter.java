package com.goosage.infra.dao;

import org.springframework.stereotype.Component;

import com.goosage.domain.EventType;
import com.goosage.domain.recovery.RecoveryEventPort;

@Component
public class RecoveryEventDaoAdapter implements RecoveryEventPort {

    private final RecoveryEventDao recoveryEventDao;

    public RecoveryEventDaoAdapter(RecoveryEventDao recoveryEventDao) {
        this.recoveryEventDao = recoveryEventDao;
    }

    @Override
    public void recordEvent(long userId,
                            EventType eventType,
                            String refType,
                            Long refId,
                            String payloadJson,
                            String source) {
        recoveryEventDao.recordEvent(userId, eventType, refType, refId, payloadJson, source);
    }
}