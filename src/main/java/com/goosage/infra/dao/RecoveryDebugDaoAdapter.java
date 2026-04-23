package com.goosage.infra.dao;

import org.springframework.stereotype.Component;

import com.goosage.domain.EventType;
import com.goosage.domain.recovery.RecoveryDebugPort;

@Component
public class RecoveryDebugDaoAdapter implements RecoveryDebugPort {

    private final RecoveryEventDao recoveryEventDao;

    public RecoveryDebugDaoAdapter(RecoveryEventDao recoveryEventDao) {
        this.recoveryEventDao = recoveryEventDao;
    }

    @Override
    public void recordPing(Long userId) {
    	recoveryEventDao.recordEvent(userId, EventType.URGE_LOG, null, null, null, "user");
    }
}