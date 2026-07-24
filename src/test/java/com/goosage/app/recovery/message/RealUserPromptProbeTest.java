package com.goosage.app.recovery.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goosage.domain.recovery.message.RecoveryMessageSignal;

class RealUserPromptProbeTest {

    private final RuleBasedRecoveryMessageAnalyzer analyzer =
            new RuleBasedRecoveryMessageAnalyzer();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Test
    void probesRealUserPromptSetWithoutFailingBuild() throws Exception {
        List<PromptCase> cases = loadCases();

        int matched = 0;
        int mismatched = 0;

        System.out.println();
        System.out.println(
                "REAL USER PROMPT PROBE V1"
        );
        System.out.println(
                "=".repeat(88)
        );

        for (PromptCase promptCase : cases) {
            RecoveryMessageAnalysis actual =
                    analyzer.analyze(
                            promptCase.message()
                    );

            Comparison comparison =
                    compare(
                            promptCase,
                            actual
                    );

            if (comparison.matched()) {
                matched++;
            } else {
                mismatched++;
            }

            printResult(
                    promptCase,
                    actual,
                    comparison
            );
        }

        System.out.println();
        System.out.println(
                "total=" + cases.size()
        );
        System.out.println(
                "matched=" + matched
        );
        System.out.println(
                "mismatched=" + mismatched
        );
        System.out.println(
                "matchRate="
                        + round(
                                matched
                                / (double) cases.size()
                        )
        );

        assertThat(cases).hasSize(20);
        assertThat(mismatched)
                .as("real-user prompt mismatches")
                .isZero();
        assertThat(matched)
                .as("real-user prompt matches")
                .isEqualTo(cases.size());
    }

    private List<PromptCase> loadCases() throws Exception {
        try (
                InputStream inputStream =
                        getClass()
                                .getClassLoader()
                                .getResourceAsStream(
                                        "recovery-message/"
                                        + "real_user_prompt_set_v1.json"
                                )
        ) {
            if (inputStream == null) {
                throw new IllegalStateException(
                        "Prompt resource not found"
                );
            }

            return objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<PromptCase>>() {
                    }
            );
        }
    }

    private Comparison compare(
            PromptCase expected,
            RecoveryMessageAnalysis actual
    ) {
        boolean analyzableMatched =
                actual.analyzable()
                == expected.expectedAnalyzable();

        boolean holdReasonMatched =
                expected.expectedHoldReason() == null
                        ? actual.holdReason() == null
                        : expected.expectedHoldReason()
                                .equals(
                                        actual.holdReason()
                                );

        boolean signalMatched =
                compareSignal(
                        expected.expectedSignal(),
                        actual.signal()
                );

        return new Comparison(
                analyzableMatched
                        && holdReasonMatched
                        && signalMatched,
                analyzableMatched,
                holdReasonMatched,
                signalMatched
        );
    }

    private boolean compareSignal(
            ExpectedSignal expected,
            RecoveryMessageSignal actual
    ) {
        if (expected == null) {
            return actual == null;
        }

        if (actual == null) {
            return false;
        }

        return actual.urgeLogDelta()
                        == expected.urgeLogDelta()
                && actual.betAttemptDelta()
                        == expected.betAttemptDelta()
                && actual.betBlockedDelta()
                        == expected.betBlockedDelta()
                && actual.recoveryActionDelta()
                        == expected.recoveryActionDelta()
                && actual.relapseSignalDelta()
                        == expected.relapseSignalDelta();
    }

    private void printResult(
            PromptCase promptCase,
            RecoveryMessageAnalysis actual,
            Comparison comparison
    ) {
        System.out.println();
        System.out.println(
                promptCase.id()
                        + " | "
                        + promptCase.category()
        );
        System.out.println(
                "message="
                        + promptCase.message()
        );
        System.out.println(
                "matched="
                        + comparison.matched()
        );
        System.out.println(
                "expectedAnalyzable="
                        + promptCase.expectedAnalyzable()
                        + ", actualAnalyzable="
                        + actual.analyzable()
        );
        System.out.println(
                "expectedHoldReason="
                        + promptCase.expectedHoldReason()
                        + ", actualHoldReason="
                        + actual.holdReason()
        );
        System.out.println(
                "expectedSignal="
                        + signalToMap(
                                promptCase.expectedSignal()
                        )
        );
        System.out.println(
                "actualSignal="
                        + signalToMap(
                                actual.signal()
                        )
        );
    }

    private Map<String, Integer> signalToMap(
            ExpectedSignal signal
    ) {
        if (signal == null) {
            return null;
        }

        return Map.of(
                "urge",
                signal.urgeLogDelta(),
                "attempt",
                signal.betAttemptDelta(),
                "blocked",
                signal.betBlockedDelta(),
                "recovery",
                signal.recoveryActionDelta(),
                "relapse",
                signal.relapseSignalDelta()
        );
    }

    private Map<String, Integer> signalToMap(
            RecoveryMessageSignal signal
    ) {
        if (signal == null) {
            return null;
        }

        return Map.of(
                "urge",
                signal.urgeLogDelta(),
                "attempt",
                signal.betAttemptDelta(),
                "blocked",
                signal.betBlockedDelta(),
                "recovery",
                signal.recoveryActionDelta(),
                "relapse",
                signal.relapseSignalDelta()
        );
    }

    private double round(double value) {
        return Math.round(value * 10000.0)
                / 10000.0;
    }

    private record PromptCase(
            String id,
            String category,
            String message,
            boolean expectedAnalyzable,
            String expectedHoldReason,
            ExpectedSignal expectedSignal
    ) {
    }

    private record ExpectedSignal(
            int urgeLogDelta,
            int betAttemptDelta,
            int betBlockedDelta,
            int recoveryActionDelta,
            int relapseSignalDelta
    ) {
    }

    private record Comparison(
            boolean matched,
            boolean analyzableMatched,
            boolean holdReasonMatched,
            boolean signalMatched
    ) {
    }
}
