package com.goosage.app.recovery.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RuleBasedRecoveryMessageAnalyzerMatrixTest {

    private final RuleBasedRecoveryMessageAnalyzer analyzer =
            new RuleBasedRecoveryMessageAnalyzer();

    @Test
    void extractsSingleSupportedSignals() {
        assertSignal("충동이 생겼어", 1, 0, 0, 0, 0, 0.70);
        assertSignal("베팅을 시도했어", 0, 1, 0, 0, 0, 0.70);
        assertSignal("사이트를 닫았어", 0, 0, 1, 0, 0, 0.70);
        assertSignal("산책했어", 0, 0, 0, 1, 0, 0.70);
        assertSignal("재발했어", 0, 0, 0, 0, 1, 0.70);
    }

    @Test
    void extractsCombinedSignalsAndConfidence() {
        assertSignal(
                "충동이 왔지만 앱을 닫았어",
                1, 0, 1, 0, 0, 0.80
        );

        assertSignal(
                "충동이 왔지만 사이트를 닫고 산책했어",
                1, 0, 1, 1, 0, 0.90
        );

        assertSignal(
                "베팅 화면을 열었지만 결제를 취소하고 도움을 요청했어",
                0, 1, 1, 1, 0, 0.90
        );
    }

    @Test
    void holdsUnsupportedOrExcludedContexts() {
        assertHold("   ", "EMPTY_MESSAGE");
        assertHold("충동", "MESSAGE_TOO_SHORT");
        assertHold("오늘은 평범한 하루였어", "NO_SUPPORTED_SIGNAL");
        assertHold("충동은 없었고 안정적이었어", "NO_SUPPORTED_SIGNAL");
        assertHold("친구가 다시 베팅했다고 하더라", "THIRD_PARTY_CONTEXT");
        assertHold("만약 다시 베팅한다면 어떻게 해야 할까", "HYPOTHETICAL_CONTEXT");
    }

    @Test
    void respectsExplicitNegationPatterns() {
        assertHold("베팅을 시도하지 않았어", "NO_SUPPORTED_SIGNAL");
        assertHold("사이트에 들어가지 않았어", "NO_SUPPORTED_SIGNAL");
        assertHold("결제하지 않았어", "NO_SUPPORTED_SIGNAL");
        assertHold("재발하지 않았어", "NO_SUPPORTED_SIGNAL");
        assertHold("무너지지 않았어", "NO_SUPPORTED_SIGNAL");
    }

    private void assertSignal(
            String message,
            int urge,
            int attempt,
            int blocked,
            int recovery,
            int relapse,
            double confidence
    ) {
        RecoveryMessageAnalysis result = analyzer.analyze(message);

        assertThat(result.analyzable())
                .as(message)
                .isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();

        assertThat(result.signal().urgeLogDelta()).isEqualTo(urge);
        assertThat(result.signal().betAttemptDelta()).isEqualTo(attempt);
        assertThat(result.signal().betBlockedDelta()).isEqualTo(blocked);
        assertThat(result.signal().recoveryActionDelta()).isEqualTo(recovery);
        assertThat(result.signal().relapseSignalDelta()).isEqualTo(relapse);
        assertThat(result.signal().confidence()).isEqualTo(confidence);
    }

    private void assertHold(String message, String expectedReason) {
        RecoveryMessageAnalysis result = analyzer.analyze(message);

        assertThat(result.analyzable())
                .as(message)
                .isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason()).isEqualTo(expectedReason);
    }
}
