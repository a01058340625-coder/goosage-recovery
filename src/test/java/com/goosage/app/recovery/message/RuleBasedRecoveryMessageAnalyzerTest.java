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

@Test
    void detectsFundingCompletedWithBetNegationAsShadowMetadata() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\ubca0\ud305\uc740 \ud558\uc9c0 \uc54a\uc558\uc9c0\ub9cc "
                        + "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \ucda9\uc804\ud574 \ub450\uc5c8\uc5b4. "
                        + "\uc544\uc9c1 \uc0ac\uc6a9\ud558\uc9c0 \uc54a\uc558\uc73c\ub2c8 "
                        + "\uc7ac\ubc1c\uc740 \uc544\ub2c8\ub77c\uace0 \uc0dd\uac01\ud574."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");

        assertThat(result.riskPreparationMetadata().detected())
                .isTrue();
        assertThat(result.riskPreparationMetadata().type())
                .isEqualTo(
                        "FUNDING_COMPLETED_BET_NEGATED"
                );
    }

    @Test
    void detectsFundingCompletedWithFutureIntentAsShadowMetadata() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\ubca0\ud305\uc740 \uc544\uc9c1 \ud558\uc9c0 \uc54a\uc558\uc9c0\ub9cc "
                        + "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \ub123\uc5b4\ub450\uace0 "
                        + "\uc624\ub298 \ubc24\uc5d0 \uc0ac\uc6a9\ud560 \uc0dd\uac01\uc774\uc57c."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");

        assertThat(result.riskPreparationMetadata().detected())
                .isTrue();
        assertThat(result.riskPreparationMetadata().type())
                .isEqualTo(
                        "FUNDING_COMPLETED_FUTURE_INTENT_PRESENT"
                );
    }

    @Test
    void detectsConditionalFutureReentryDecisionAfterCompletedFunding() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uacc4\uc88c\uc5d0 \ub3c8\uc744 "
                        + "\uc62e\uaca8\ub450\uae34 \ud588\uace0, "
                        + "\ub0b4\uc77c \uc0c1\ud669\uc744 \ubcf4\uace0 "
                        + "\ub2e4\uc2dc \ub4e4\uc5b4\uac08\uc9c0 "
                        + "\uacb0\uc815\ud560 \uac70\uc57c."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
        assertThat(result.signal()).isNull();

        assertThat(result.riskPreparationMetadata().detected())
                .isTrue();
        assertThat(result.riskPreparationMetadata().type())
                .isEqualTo(
                        "FUNDING_COMPLETED_FUTURE_INTENT_PRESENT"
                );
    }

    @Test
    void doesNotDetectRiskPreparationForUndecidedFundingUse() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uc0c1\ub2f4\uc740 \ubc1b\uace0 \uc654\uc9c0\ub9cc, "
                        + "\uacc4\uc88c\uc5d0 \ub3c8\uc744 "
                        + "\uc62e\uaca8\ub450\uace0\ub3c4 "
                        + "\uc544\uc9c1 \uc0ac\uc6a9\ud560\uc9c0\ub294 "
                        + "\uacb0\uc815\ud558\uc9c0 \ubabb\ud588\uc5b4."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.signal().recoveryActionDelta())
                .isEqualTo(1);

        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
        assertThat(result.riskPreparationMetadata().type())
                .isNull();
    }

    @Test
    void doesNotDetectRiskPreparationWhenReentryIntentIsNegated() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uacc4\uc88c\uc5d0 \ub3c8\uc744 "
                        + "\uc62e\uaca8\ub450\uae34 \ud588\uc9c0\ub9cc, "
                        + "\ub2e4\uc2dc \ub4e4\uc5b4\uac08 "
                        + "\uc0dd\uac01\uc740 \uc5c6\uc5b4."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
        assertThat(result.signal()).isNull();

        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
        assertThat(result.riskPreparationMetadata().type())
                .isNull();
    }

    @Test
    void detectsFundingFutureIntentAfterCompletedRecoveryAction() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uc624\ub298 \uc0c1\ub2f4\uc740 \ubc1b\uace0 "
                        + "\uc654\uc9c0\ub9cc, \ubc24\uc5d0 \ub2e4\uc2dc "
                        + "\ub4e4\uc5b4\uac00\ub824\uace0 \uacc4\uc88c\uc5d0 "
                        + "\ub3c8\uc744 \uc62e\uaca8\ub480\uc5b4."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().urgeLogDelta()).isZero();
        assertThat(result.signal().betAttemptDelta()).isZero();
        assertThat(result.signal().betBlockedDelta()).isZero();
        assertThat(result.signal().recoveryActionDelta()).isEqualTo(1);
        assertThat(result.signal().relapseSignalDelta()).isZero();

        assertThat(result.riskPreparationMetadata().detected())
                .isTrue();
        assertThat(result.riskPreparationMetadata().type())
                .isEqualTo(
                        "FUNDING_COMPLETED_FUTURE_INTENT_PRESENT"
                );
    }


    @Test
    void keepsRiskPreparationNoneForCompletedRecoveryWithoutFundingIntent() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uc624\ub298 \uc0c1\ub2f4\uc740 \ubc1b\uace0 "
                        + "\uc654\uace0 \uacc4\uc815\ub3c4 \ub9c9\uc558\uc5b4."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isEqualTo(1);
        assertThat(result.signal().relapseSignalDelta()).isZero();

        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
        assertThat(result.riskPreparationMetadata().type())
                .isNull();
    }


    @Test
    void detectsFundingStartedThenCancelledAsShadowMetadata() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \ub123\uc73c\ub824\ub2e4\uac00 "
                        + "\uc911\uac04\uc5d0 \uba48\ucd94\uace0 "
                        + "\ucda9\uc804\uc744 \ucde8\uc18c\ud588\uc5b4."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");

        assertThat(result.riskPreparationMetadata().detected())
                .isTrue();
        assertThat(result.riskPreparationMetadata().type())
                .isEqualTo(
                        "FUNDING_STARTED_THEN_CANCELLED"
                );
    }

    @Test
    void detectsCurrentCounselingContinuationAsRecoveryAction() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uc9c0\ub09c\uc8fc\uc5d0\ub294 \ub2e4\uc2dc "
                        + "\ubca0\ud305\ud588\uc9c0\ub9cc \uc9c0\uae08\uc740 "
                        + "\uc571\uc744 \uc9c0\uc6b0\uace0 "
                        + "\uc0c1\ub2f4\uc744 \uacc4\uc18d \ubc1b\uace0 \uc788\uc5b4."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().urgeLogDelta()).isZero();
        assertThat(result.signal().betAttemptDelta()).isZero();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isEqualTo(1);
        assertThat(result.signal().relapseSignalDelta()).isEqualTo(1);
    }

    @Test
    void doesNotTreatNegatedCounselingContinuationAsRecoveryAction() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uc9c0\ub09c\uc8fc\uc5d0\ub294 \ub2e4\uc2dc "
                        + "\ubca0\ud305\ud588\uc9c0\ub9cc \uc9c0\uae08\uc740 "
                        + "\uc0c1\ub2f4\uc744 \uacc4\uc18d \ubc1b\uc9c0 \uc54a\uace0 \uc788\uc5b4."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().recoveryActionDelta()).isZero();
        assertThat(result.signal().relapseSignalDelta()).isEqualTo(1);
    }

    @Test
    void detectsSelfRelapseAfterThirdPartyContextSwitch() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uce5c\uad6c\uac00 \ub2e4\uc2dc "
                        + "\ubca0\ud305\ud588\ub2e4\ub294 \uc598\uae30\ub97c "
                        + "\ub4e4\uc5c8\ub294\ub370, \uc0ac\uc2e4 \ub098\ub3c4 "
                        + "\uc5b4\uc82f\ubc24\uc5d0 \ub2e4\uc2dc "
                        + "\ub3c8\uc744 \uac78\uc5c8\uc5b4."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().urgeLogDelta()).isZero();
        assertThat(result.signal().betAttemptDelta()).isZero();
        assertThat(result.signal().betBlockedDelta()).isZero();
        assertThat(result.signal().recoveryActionDelta()).isZero();
        assertThat(result.signal().relapseSignalDelta()).isEqualTo(1);
    }

    @Test
    void doesNotTreatNegatedSelfBetAfterThirdPartyContextAsRelapse() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uce5c\uad6c\uac00 \ub2e4\uc2dc "
                        + "\ubca0\ud305\ud588\ub2e4\ub294 \uc598\uae30\ub97c "
                        + "\ub4e4\uc5c8\uc9c0\ub9cc, \uc0ac\uc2e4 \ub098\ub294 "
                        + "\uc5b4\uc82f\ubc24\uc5d0 \ub3c8\uc744 "
                        + "\uac78\uc9c0 \uc54a\uc558\uc5b4."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SELF_SIGNAL");
    }


    @Test
    void doesNotTreatQuotedSelfSubjectInThirdPartySpeechAsUserRelapse() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uce5c\uad6c\uac00 \u2018\ub098\ub3c4 "
                        + "\ub2e4\uc2dc \ub3c8\uc744 "
                        + "\uac78\uc5c8\uc5b4\u2019\ub77c\uace0 "
                        + "\ub9d0\ud588\uc5b4."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("THIRD_PARTY_CONTEXT");
    }


    @Test
    void detectsCounselingAndBlockingAfterQuotedThirdPartyRelapse() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uce5c\uad6c\uac00 \u2018\ub098\ub3c4 "
                        + "\uc5b4\uc81c \ub2e4\uc2dc \ub3c8\uc744 "
                        + "\uac78\uc5c8\uc5b4\u2019\ub77c\uace0 "
                        + "\ub9d0\ud588\uc9c0\ub9cc, \ub098\ub294 \uc624\ub298 "
                        + "\uc0c1\ub2f4\uc744 \ubc1b\uace0 "
                        + "\uacc4\uc815\uc744 \ub9c9\uc558\uc5b4."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().urgeLogDelta()).isZero();
        assertThat(result.signal().betAttemptDelta()).isZero();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isEqualTo(1);
        assertThat(result.signal().relapseSignalDelta()).isZero();
    }

    @Test
    void doesNotTreatNegatedCounselingAsRecoveryAction() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\ub098\ub294 \uc624\ub298 \uc0c1\ub2f4\uc744 "
                        + "\ubc1b\uace0 \uc788\uc9c0 \uc54a\uace0 "
                        + "\uacc4\uc815\ub9cc \ub9c9\uc558\uc5b4."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isZero();
        assertThat(result.signal().relapseSignalDelta()).isZero();
    }

    @Test
    void detectsCompletedCounselingParticleVariantWithNegatedBlocking() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uc0c1\ub2f4\uc740 \ubc1b\uace0 \uc654\uc9c0\ub9cc "
                        + "\uacc4\uc815\uc740 \uc544\uc9c1 \ub9c9\uc9c0 \uc54a\uc558\uace0, "
                        + "\uc624\ub298 \ubc24 \ub2e4\uc2dc \ub4e4\uc5b4\uac08\uae4c "
                        + "\uacc4\uc18d \uace0\ubbfc \uc911\uc774\uc57c."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().betBlockedDelta()).isZero();
        assertThat(result.signal().recoveryActionDelta()).isEqualTo(1);
        assertThat(result.signal().relapseSignalDelta()).isZero();
    }


    @Test
    void doesNotTreatNegatedCounselingReturnAsRecoveryAction() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uc0c1\ub2f4\uc740 \ubc1b\uace0 \uc624\uc9c0 "
                        + "\uc54a\uc558\uace0 \uacc4\uc815\ub3c4 "
                        + "\ub9c9\uc9c0 \uc54a\uc558\uc5b4."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
    }

    @Test
    void doesNotTreatNegatedCounselingParticleVariantAsRecoveryAction() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uc0c1\ub2f4\uc740 \ubc1b\uace0 \uc788\uc9c0 "
                        + "\uc54a\uc544."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
    }


    @Test
    void doesNotTreatQuotedThirdPartyCounselingAsUserRecovery() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uce5c\uad6c\uac00 \u2018\ub098\ub294 "
                        + "\uc0c1\ub2f4\uc744 \ubc1b\uace0 "
                        + "\uacc4\uc815\uc744 \ub9c9\uc558\uc5b4\u2019\ub77c\uace0 "
                        + "\ub9d0\ud588\uc5b4."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("THIRD_PARTY_CONTEXT");
    }


    @Test
    void detectsFundingWithdrawalAsBlockAndRecoveryAfterRiskPreparation() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\ubc24\uc5d0 \ub2e4\uc2dc "
                        + "\ub4e4\uc5b4\uac00\ub824\uace0 "
                        + "\uacc4\uc88c\uc5d0 \ub3c8\uc744 "
                        + "\uc62e\uaca8\ub480\uc9c0\ub9cc, "
                        + "\uc0dd\uac01\uc744 \ubc14\uafb8\uace0 "
                        + "\ubc14\ub85c \ub2e4\uc2dc "
                        + "\ube7c\ub0c8\uc5b4."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().urgeLogDelta()).isZero();
        assertThat(result.signal().betAttemptDelta()).isZero();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isEqualTo(1);
        assertThat(result.signal().relapseSignalDelta()).isZero();

        assertThat(result.riskPreparationMetadata().detected())
                .isTrue();
        assertThat(result.riskPreparationMetadata().type())
                .isEqualTo(
                        "FUNDING_COMPLETED_FUTURE_INTENT_PRESENT"
                );
    }

    @Test
    void doesNotTreatUncompletedFundingWithdrawalAsBlockOrRecovery() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uacc4\uc88c\uc758 \ub3c8\uc744 "
                        + "\ub2e4\uc2dc \ube7c\ub0b4\ub824\uace0 "
                        + "\ud588\uc9c0\ub9cc \ubabb\ud588\uc5b4."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
    }

    @Test
    void doesNotTreatNegatedFundingWithdrawalAsBlockOrRecovery() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uacc4\uc88c\uc5d0 \ub3c8\uc744 "
                        + "\uc62e\uaca8\ub450\uace0 "
                        + "\ub2e4\uc2dc \ube7c\ub0b4\uc9c0 "
                        + "\uc54a\uc558\uc5b4."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
    }

    @Test
    void doesNotTreatQuotedThirdPartyFundingWithdrawalAsUserRecovery() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uce5c\uad6c\uac00 "
                        + "\u2018\uacc4\uc88c\uc5d0 \ub123\uc5b4\ub454 "
                        + "\ub3c8\uc744 \ubc14\ub85c \ub2e4\uc2dc "
                        + "\ube7c\ub0c8\uc5b4\u2019\ub77c\uace0 "
                        + "\ub9d0\ud588\uc5b4."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("THIRD_PARTY_CONTEXT");
    }


    @Test
    void doesNotTreatOrdinaryWithdrawalAsRecoverySignal() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uc0dd\ud65c\ube44\uac00 "
                        + "\ud544\uc694\ud574\uc11c "
                        + "\uacc4\uc88c\uc5d0\uc11c \ub3c8\uc744 "
                        + "\ubc14\ub85c \ub2e4\uc2dc "
                        + "\ube7c\ub0c8\uc5b4."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
    }

    @Test
    void preservesRelapseWhenFundingIsWithdrawnAfterCompletedBet() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "\uacc4\uc88c\uc5d0 \ub3c8\uc744 "
                        + "\uc62e\uaca8\ub480\uace0 "
                        + "\uc2e4\uc81c\ub85c \ub2e4\uc2dc "
                        + "\ubca0\ud305\ud55c \ub4a4 "
                        + "\ub0a8\uc740 \ub3c8\uc744 "
                        + "\ubc14\ub85c \ub2e4\uc2dc "
                        + "\ube7c\ub0c8\uc5b4."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isEqualTo(1);
        assertThat(result.signal().relapseSignalDelta()).isEqualTo(1);
    }


    @Test
    void detectsAbortedFundingAsBlockAndRecovery() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "계좌에 돈을 옮기려다가 "
                        + "마음을 바꿔서 이체하지 않았고, "
                        + "내일 다시 생각해볼 거야."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().urgeLogDelta()).isZero();
        assertThat(result.signal().betAttemptDelta()).isZero();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isEqualTo(1);
        assertThat(result.signal().relapseSignalDelta()).isZero();
        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
    }

    @Test
    void doesNotTreatExternalTransferFailureAsSelfBlock() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "계좌에 돈을 옮기려 했지만 "
                        + "오류가 나서 이체하지 못했어."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
    }

    @Test
    void doesNotTreatFundingConsiderationAsSelfBlock() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "계좌에 돈을 옮길까 생각했지만 "
                        + "아직 아무것도 하지 않았어."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
    }

    @Test
    void doesNotTreatExternalReasonAsSelfReversal() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "계좌에 돈을 옮기려다가 "
                        + "시간이 없어서 이체하지 않았어."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
    }

    @Test
    void doesNotTreatThirdPartyAbortedFundingAsUserRecovery() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "친구가 계좌에 돈을 옮기려다가 "
                        + "마음을 바꿔서 이체하지 않았다고 말했어."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("THIRD_PARTY_CONTEXT");
    }



    @Test
    void detectsAnxietyStoppedFundingAsBlockOnly() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "계좌에 돈을 옮기려다가 불안해져서 "
                        + "이체를 멈췄지만, 내일은 다시 "
                        + "옮길 수도 있을 것 같아."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().urgeLogDelta()).isZero();
        assertThat(result.signal().betAttemptDelta()).isZero();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isZero();
        assertThat(result.signal().relapseSignalDelta()).isZero();

        assertThat(result.riskPreparationMetadata().detected())
                .isTrue();
        assertThat(result.riskPreparationMetadata().type())
                .isEqualTo("FUNDING_STARTED_THEN_CANCELLED");
    }

    @Test
    void doesNotTreatTransferErrorAsFundingCancellation() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "계좌에 돈을 옮기려 했지만 "
                        + "오류가 나서 이체가 중단됐어."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
    }

    @Test
    void doesNotTreatTimeLimitAsFundingCancellation() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "계좌에 돈을 옮기려다가 "
                        + "시간이 없어서 이체를 멈췄어."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
    }

    @Test
    void doesNotPromoteAnxietyStopToRecoveryAction() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "이체하려다가 불안해서 "
                        + "이체를 중단했어."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isZero();
        assertThat(result.signal().relapseSignalDelta()).isZero();
    }

    @Test
    void doesNotTreatThirdPartyAnxietyStopAsUserSignal() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "친구가 계좌에 돈을 옮기려다가 "
                        + "불안해서 이체를 멈췄다고 말했어."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("THIRD_PARTY_CONTEXT");
        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
    }



    @Test
    void detectsExternalInterventionRetryIntentRiskPreparation() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "계좌에 돈을 옮기려 했는데 "
                        + "가족이 휴대폰을 가져가서 "
                        + "이체하지 못했고, 내일 다시 "
                        + "시도할 생각이야."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");

        assertThat(result.riskPreparationMetadata().detected())
                .isTrue();
        assertThat(result.riskPreparationMetadata().type())
                .isEqualTo(
                        "FUNDING_INTERRUPTED_BY_EXTERNAL_"
                        + "INTERVENTION_WITH_RETRY_INTENT"
                );
    }

    @Test
    void doesNotDetectExternalInterventionWithoutRetryIntent() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "가족이 휴대폰을 가져가서 "
                        + "이체하지 못했어."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
    }

    @Test
    void doesNotDetectExternalInterventionAfterAbandonment() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "가족이 휴대폰을 가져가서 "
                        + "이체하지 못했고, 이제 다시 "
                        + "시도하지 않을 거야."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
    }

    @Test
    void preservesSelfCancelledFundingSubtype() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "계좌에 돈을 옮기려다가 "
                        + "불안해져서 이체를 멈췄어."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isZero();

        assertThat(result.riskPreparationMetadata().detected())
                .isTrue();
        assertThat(result.riskPreparationMetadata().type())
                .isEqualTo("FUNDING_STARTED_THEN_CANCELLED");
    }

    @Test
    void doesNotTreatThirdPartyExternalInterventionAsUserRisk() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "친구가 가족에게 휴대폰을 빼앗겨 "
                        + "이체하지 못했고 내일 다시 "
                        + "시도할 생각이라고 말했어."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("THIRD_PARTY_CONTEXT");
        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
    }



    @Test
    void detectsProtectiveBlockReversalPossibility() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "계좌에 돈을 옮기려 했는데 가족이 "
                        + "휴대폰을 가져가서 이체하지 못했고, "
                        + "이후 내가 계정을 막았지만 내일 "
                        + "다시 풀 수도 있을 것 같아."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.holdReason()).isNull();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().urgeLogDelta()).isZero();
        assertThat(result.signal().betAttemptDelta()).isZero();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.signal().recoveryActionDelta()).isZero();
        assertThat(result.signal().relapseSignalDelta()).isZero();

        assertThat(result.riskPreparationMetadata().detected())
                .isTrue();
        assertThat(result.riskPreparationMetadata().type())
                .isEqualTo(
                        "PROTECTIVE_BLOCK_REVERSAL_"
                        + "POSSIBILITY_PRESENT"
                );
    }

    @Test
    void doesNotDetectBlockReversalWithoutPossibility() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "이후 내가 계정을 막고 "
                        + "다시 접속하지 못하게 했어."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
    }

    @Test
    void doesNotDetectExplicitlyNegatedBlockReversal() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "계정을 막았고 다시 풀 생각은 없어."
                );

        assertThat(result.analyzable()).isTrue();
        assertThat(result.signal()).isNotNull();
        assertThat(result.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
    }

    @Test
    void doesNotDetectBlockReversalWithoutCompletedBlock() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "내일 계정을 막을지 다시 풀지 "
                        + "생각해볼 거야."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("NO_SUPPORTED_SIGNAL");
        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
    }

    @Test
    void doesNotTreatThirdPartyBlockReversalAsUserRisk() {
        RecoveryMessageAnalysis result =
                analyzer.analyze(
                        "친구가 계정을 막았지만 내일 "
                        + "다시 풀 수도 있다고 말했어."
                );

        assertThat(result.analyzable()).isFalse();
        assertThat(result.signal()).isNull();
        assertThat(result.holdReason())
                .isEqualTo("THIRD_PARTY_CONTEXT");
        assertThat(result.riskPreparationMetadata().detected())
                .isFalse();
    }


}
