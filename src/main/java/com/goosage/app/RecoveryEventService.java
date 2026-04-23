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
    public void record(
            Long userId,
            EventType type,
            Long knowledgeId,
            boolean isBrainAction,
            String brainActionType,
            String brainPatternType
    ) {
        String refType = (knowledgeId == null) ? null : "KNOWLEDGE";
        String payload = buildPayloadJson(type, isBrainAction, brainActionType, brainPatternType);

        recoveryEventPort.recordEvent(userId, type, refType, knowledgeId, payload);

        EventLogWriter.write(userId, type.name());
    }

    private String buildPayloadJson(
            EventType type,
            boolean isBrainAction,
            String brainActionType,
            String brainPatternType
    ) {
        if (!isBrainAction) {
            return "{\"source\":\"user\"}";
        }

        String actionValue = (brainActionType == null || brainActionType.isBlank())
                ? type.name()
                : brainActionType;

        String patternValue = (brainPatternType == null || brainPatternType.isBlank())
                ? ""
                : ",\"brainPatternType\":\"" + brainPatternType + "\"";

        return "{\"source\":\"brain\",\"brainActionType\":\"" + actionValue + "\"" + patternValue + "}";
    }
}