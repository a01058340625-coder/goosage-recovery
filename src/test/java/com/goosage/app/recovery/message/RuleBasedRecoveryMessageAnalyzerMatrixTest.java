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

        assertSignal(
                "다시 베팅했지만 바로 차단하고 상담을 요청했어",
                0, 0, 1, 1, 1, 0.90
        );

        assertHold("차단하지 않았어", "NO_SUPPORTED_SIGNAL");
        assertHold("상담을 요청하지 않았어", "NO_SUPPORTED_SIGNAL");
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
    void separatesThirdPartyAndExplicitSelfSubjectContexts() {
        assertSignal(
                "친구가 다시 베팅했다는 얘기를 듣고 나는 충동이 왔지만 앱을 닫았어",
                1, 0, 1, 0, 0, 0.80
        );

        assertSignal(
                "지인이 재발했다는 말을 듣고 내가 산책했어",
                0, 0, 0, 1, 0, 0.70
        );

        assertHold(
                "나는 친구가 다시 베팅했다고 들었어",
                "THIRD_PARTY_CONTEXT"
        );
    }

    @Test
    void respectsExplicitNegationPatterns() {
        assertHold("베팅을 시도하지 않았어", "NO_SUPPORTED_SIGNAL");
        assertHold("베팅을 시도하려던 건 아니고 사이트만 확인했어", "NO_SUPPORTED_SIGNAL");
        assertHold("사이트에 들어가지 않았어", "NO_SUPPORTED_SIGNAL");
        assertHold("결제하지 않았어", "NO_SUPPORTED_SIGNAL");
        assertHold("재발하지 않았어", "NO_SUPPORTED_SIGNAL");
        assertHold("무너지지 않았어", "NO_SUPPORTED_SIGNAL");
    }

    @Test
    void distinguishesIndirectRelapseReentryFromNegatedAndNeutralReentry() {
        assertSignal(
                "오늘은 참으려고 했는데 결국 다시 들어갔고, 지금은 너무 후회돼.",
                0, 0, 0, 0, 1, 0.70
        );

        assertHold(
                "오늘은 참으려고 했고 사이트에 다시 들어가지 않았어",
                "NO_SUPPORTED_SIGNAL"
        );

        assertHold(
                "비가 와서 집에 다시 들어갔어",
                "NO_SUPPORTED_SIGNAL"
        );
    }
    @Test
    void auditsIndirectRelapseExpressionCoverageWithoutNeutralOvermatch() {
        // 명확한 간접 재발·재진입 표현
        assertSignal(
                "참으려고 했는데 결국 다시 들어갔고 너무 후회돼.",
                0, 0, 0, 0, 1, 0.70
        );

        assertSignal(
                "버티려고 했지만 또다시 들어갔고 완전히 무너졌어.",
                0, 0, 0, 0, 1, 0.70
        );

        assertSignal(
                "막으려고 했는데 다시 들어가버렸고 통제하지 못했어.",
                0, 0, 0, 0, 1, 0.70
        );

        assertSignal(
                "안 하려고 했는데 결국 또 들어갔고 지금 후회하고 있어.",
                0, 0, 0, 0, 1, 0.70
        );

        assertSignal(
                "끊으려고 했지만 결국 사이트에 다시 접속했고 후회돼.",
                0, 0, 0, 0, 1, 0.70
        );

        assertSignal(
                "버티다가 결국 그 화면으로 돌아갔고 또 해버렸어.",
                0, 0, 0, 0, 1, 0.70
        );

        // 부정 또는 일상적인 중립 재진입 표현
        assertHold(
                "오늘은 참으려고 했고 사이트에 다시 들어가지 않았어.",
                "NO_SUPPORTED_SIGNAL"
        );

        assertHold(
                "결국 다시 들어가지는 않았고 그냥 잠들었어.",
                "NO_SUPPORTED_SIGNAL"
        );

        assertHold(
                "비가 와서 집에 다시 들어갔어.",
                "NO_SUPPORTED_SIGNAL"
        );

        assertHold(
                "지갑을 두고 와서 카페에 다시 들어갔어.",
                "NO_SUPPORTED_SIGNAL"
        );

        assertHold(
                "회의가 있어서 방으로 다시 들어갔어.",
                "NO_SUPPORTED_SIGNAL"
        );

        assertHold(
                "사이트에 들어가려다가 멈추고 다시 나오기로 했어.",
                "NO_SUPPORTED_SIGNAL"
        );
    }

    @Test
    void capturesCompletedRelapseAfterReentryWithoutResistancePhrase() {
        assertSignal(
                "결국 다시 들어가서 또 돈을 걸었어.",
                0, 0, 0, 0, 1, 0.70
        );

        assertSignal(
                "오늘 결국 그 사이트로 돌아가서 또 해버렸어.",
                0, 0, 0, 0, 1, 0.70
        );

        assertSignal(
                "다시 들어가서 또 해버렸고 지금 도움을 요청하고 싶어.",
                0, 0, 0, 1, 1, 0.80
        );

        assertSignal(
                "친구가 다시 했다는 말을 들었고 나도 결국 들어가서 또 해버렸어.",
                0, 0, 0, 0, 1, 0.70
        );
    }


    @Test
    void capturesBypassRelapseAfterProtectiveBlock() {
        assertSignal(
                "다시는 안 하려고 계정까지 막았는데, 결국 다른 곳을 찾아서 또 돈을 넣었어.",
                0, 0, 1, 0, 1, 0.80
        );

        assertSignal(
                "계정을 막은 뒤 다른 곳을 찾아보기만 했고 돈은 넣지 않았어.",
                0, 0, 1, 0, 0, 0.70
        );

        assertSignal(
                "다시는 안 하려고 계정을 막았고, 다른 곳을 찾았지만 돈을 넣지는 않았어.",
                0, 0, 1, 0, 0, 0.70
        );
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
