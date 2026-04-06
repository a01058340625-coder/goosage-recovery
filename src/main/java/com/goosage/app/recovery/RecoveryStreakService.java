package com.goosage.app.recovery;

import org.springframework.stereotype.Service;

import com.goosage.domain.recovery.RecoveryStreakPort;

@Service
public class RecoveryStreakService {

    private final RecoveryStreakPort recoveryStreakPort;

    public RecoveryStreakService(RecoveryStreakPort recoveryStreakPort) {
        this.recoveryStreakPort = recoveryStreakPort;
    }

    public int getStreak(long userId) {
        return recoveryStreakPort.countStreak(userId);
    }
}