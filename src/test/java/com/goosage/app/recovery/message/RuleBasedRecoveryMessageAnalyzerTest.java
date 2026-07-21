package com.goosage.app.recovery.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RuleBasedRecoveryMessageAnalyzerTest {

    private final RuleBasedRecoveryMessageAnalyzer analyzer =
            new RuleBasedRecoveryMessageAnalyzer();

    @Test
    void extractsMixedRiskProtectiveAndRecoverySignals() {
        RecoveryMessageAnalysis result =
                analyzer.analyze("충동이 왔지만 사이트를 닫고 산책했어");

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();

        assertThat(result.signal().urgeLogDelta()).isEqualTo(1);
        assertThat(result.signal().betAttemptDelta()).isZero();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isEqualTo(1);
        assertThat(result.signal().relapseSignalDelta()).isZero();
        assertThat(result.signal().confidence()).isEqualTo(0.90);
    }

    @Test
    void doesNotTreatNegatedUrgeAsRiskSignal() {
        RecoveryMessageAnalysis result =
                analyzer.analyze("충동은 없었고 안정적이었어");

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason()).isEqualTo("NO_SUPPORTED_SIGNAL");
    }

    @Test
    void holdsThirdPartyContext() {
        RecoveryMessageAnalysis result =
                analyzer.analyze("친구가 다시 베팅했다고 하더라");

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason()).isEqualTo("THIRD_PARTY_CONTEXT");
    }

    @Test
    void extractsRelapseSignalFromDirectUserStatement() {
        RecoveryMessageAnalysis result =
                analyzer.analyze("다시 베팅했고 통제하지 못했어");

        assertThat(result.analyzable()).isTrue();
        assertThat(result.signal()).isNotNull();

        assertThat(result.signal().urgeLogDelta()).isZero();
        assertThat(result.signal().betAttemptDelta()).isZero();
        assertThat(result.signal().betBlockedDelta()).isZero();
        assertThat(result.signal().recoveryActionDelta()).isZero();
        assertThat(result.signal().relapseSignalDelta()).isEqualTo(1);
        assertThat(result.signal().confidence()).isEqualTo(0.70);
    }

    @Test
    void holdsEmptyAndTooShortMessages() {
        RecoveryMessageAnalysis empty = analyzer.analyze("   ");
        RecoveryMessageAnalysis shortMessage = analyzer.analyze("충동");

        assertThat(empty.analyzable()).isFalse();
        assertThat(empty.holdReason()).isEqualTo("EMPTY_MESSAGE");

        assertThat(shortMessage.analyzable()).isFalse();
        assertThat(shortMessage.holdReason()).isEqualTo("MESSAGE_TOO_SHORT");
    }

    @Test
    void holdsHypotheticalContext() {
        RecoveryMessageAnalysis result =
                analyzer.analyze("만약 다시 베팅한다면 어떻게 해야 할까");

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason()).isEqualTo("HYPOTHETICAL_CONTEXT");
    }
}