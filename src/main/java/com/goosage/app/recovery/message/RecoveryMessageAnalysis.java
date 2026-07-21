package com.goosage.app.recovery.message;

import com.goosage.domain.recovery.message.RecoveryMessageSignal;

public record RecoveryMessageAnalysis(
        String originalMessage,
        boolean analyzable,
        RecoveryMessageSignal signal,
        String holdReason
) {
}