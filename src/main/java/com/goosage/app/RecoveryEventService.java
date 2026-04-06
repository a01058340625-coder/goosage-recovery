package com.goosage.app;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goosage.domain.EventType;
import com.goosage.domain.recovery.RecoveryEventPort;
import com.goosage.infra.log.EventLogWriter;

@Service
public class RecoveryEventService {

    private final RecoveryEventPort recoveryEventPort;

    public RecoveryEventService(RecoveryEventPort recoveryEventPort) {
        this.recoveryEventPort = recoveryEventPort;
    }

    @Transactional
    public void record(Long userId, EventType type, Long knowledgeId) {
        String refType = (knowledgeId == null) ? null : "KNOWLEDGE";

        recoveryEventPort.recordEvent(userId, type, refType, knowledgeId, null);

        EventLogWriter.write(userId, type.name());
    }
}