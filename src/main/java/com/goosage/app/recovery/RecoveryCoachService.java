package com.goosage.app.recovery;

import org.springframework.stereotype.Service;

import com.goosage.domain.recovery.RecoveryCoachPort;
import com.goosage.domain.recovery.RecoveryCoachResult;

@Service
public class RecoveryCoachService {

    private final RecoveryCoachPort recoveryCoachPort;

    public RecoveryCoachService(RecoveryCoachPort recoveryCoachPort) {
        this.recoveryCoachPort = recoveryCoachPort;
    }

    public RecoveryCoachResult coach(long userId) {
        return recoveryCoachPort.execute(userId);
    }
}