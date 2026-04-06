package com.goosage.app;

import org.springframework.stereotype.Service;

import com.goosage.domain.recovery.RecoveryDebugPort;

@Service
public class RecoveryDebugService {

    private final RecoveryDebugPort recoveryDebugPort;

    public RecoveryDebugService(RecoveryDebugPort recoveryDebugPort) {
        this.recoveryDebugPort = recoveryDebugPort;
    }

    public void recordPing(Long userId) {
        recoveryDebugPort.recordPing(userId);
    }
}