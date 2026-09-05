package com.goosage.app.recovery.message;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import com.goosage.app.recovery.message.structuredshadow.CanonicalStructuredSignalEngineShadow;
import com.goosage.app.recovery.message.structuredshadow.StructuredEventSequenceShadow;
import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;
import com.goosage.app.recovery.message.structuredshadow.StructuredSentenceBoundaryAdapter;
import com.goosage.domain.recovery.message.RecoveryMessageSignal;

public class CanonicalProductionAdapterDiagnosticCli {

    public static void main(String[] args) {

        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "base64 message argument required"
            );
        }

        String message =
                new String(
                        Base64.getDecoder().decode(args[0]),
                        StandardCharsets.UTF_8
                );

        RuleBasedRecoveryMessageAnalyzer legacyAnalyzer =
                new RuleBasedRecoveryMessageAnalyzer();

        ProductionRecoveryMessageAnalyzer canonicalAdapter =
                new ProductionRecoveryMessageAnalyzer(
                        legacyAnalyzer,
                        "CANONICAL"
                );

        StructuredSentenceBoundaryAdapter boundary =
                new StructuredSentenceBoundaryAdapter();

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        CanonicalStructuredSignalEngineShadow canonical =
                new CanonicalStructuredSignalEngineShadow();


        RecoveryMessageAnalysis legacy =
                legacyAnalyzer.analyze(message);

        RecoveryMessageAnalysis adapted =
                canonicalAdapter.analyze(message);


        List<String> sentences =
                boundary.split(message);

        List<StructuredEventShadow> events =
                sequence.resolve(sentences);

        int[] vector =
                canonical.resolve(events);


        System.out.println(
                "canonical="
                + vector[0] + ","
                + vector[1] + ","
                + vector[2] + ","
                + vector[3] + ","
                + vector[4]
        );

        System.out.println(
                "legacyAnalyzable="
                + legacy.analyzable()
        );

        System.out.println(
                "legacyHold="
                + safe(legacy.holdReason())
        );

        System.out.println(
                "legacyRisk="
                + legacy.riskPreparationMetadata().detected()
        );

        System.out.println(
                "legacyPostBlock="
                + legacy.postBlockStateMetadata().detected()
        );

        System.out.println(
                "legacyReentryPreparation="
                + legacy.reentryPreparationMetadata().detected()
        );

        System.out.println(
                "legacyReentryState="
                + legacy.reentryStateMetadata().detected()
        );


        System.out.println(
                "adaptedAnalyzable="
                + adapted.analyzable()
        );

        System.out.println(
                "adaptedHold="
                + safe(adapted.holdReason())
        );


        RecoveryMessageSignal signal =
                adapted.signal();

        if (signal == null) {

            System.out.println(
                    "adaptedSignal=null"
            );

        } else {

            System.out.println(
                    "adaptedSignal="
                    + signal.urgeLogDelta() + ","
                    + signal.betAttemptDelta() + ","
                    + signal.betBlockedDelta() + ","
                    + signal.recoveryActionDelta() + ","
                    + signal.relapseSignalDelta()
            );
        }


        System.out.println(
                "adaptedRisk="
                + adapted.riskPreparationMetadata().detected()
        );

        System.out.println(
                "adaptedPostBlock="
                + adapted.postBlockStateMetadata().detected()
        );

        System.out.println(
                "adaptedReentryPreparation="
                + adapted.reentryPreparationMetadata().detected()
        );

        System.out.println(
                "adaptedReentryState="
                + adapted.reentryStateMetadata().detected()
        );
    }


    private static String safe(String value) {
        return value == null
                ? "null"
                : value;
    }
}