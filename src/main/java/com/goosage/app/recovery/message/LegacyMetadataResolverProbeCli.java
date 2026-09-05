package com.goosage.app.recovery.message;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.goosage.domain.recovery.message.RecoveryPostBlockStateMetadata;
import com.goosage.domain.recovery.message.RecoveryReentryPreparationMetadata;
import com.goosage.domain.recovery.message.RecoveryReentryStateMetadata;
import com.goosage.domain.recovery.message.RecoveryRiskPreparationMetadata;

public class LegacyMetadataResolverProbeCli {

    public static void main(String[] args) throws Exception {

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

        RuleBasedRecoveryMessageAnalyzer analyzer =
                new RuleBasedRecoveryMessageAnalyzer();


        Method normalize =
                RuleBasedRecoveryMessageAnalyzer.class
                        .getDeclaredMethod(
                                "normalize",
                                String.class
                        );

        Method riskMethod =
                RuleBasedRecoveryMessageAnalyzer.class
                        .getDeclaredMethod(
                                "resolveRiskPreparationMetadata",
                                String.class
                        );

        Method postBlockMethod =
                RuleBasedRecoveryMessageAnalyzer.class
                        .getDeclaredMethod(
                                "resolvePostBlockStateMetadata",
                                String.class
                        );

        Method reentryPreparationMethod =
                RuleBasedRecoveryMessageAnalyzer.class
                        .getDeclaredMethod(
                                "resolveReentryPreparationMetadata",
                                String.class
                        );

        Method reentryStateMethod =
                RuleBasedRecoveryMessageAnalyzer.class
                        .getDeclaredMethod(
                                "resolveReentryStateMetadata",
                                String.class
                        );


        normalize.setAccessible(true);
        riskMethod.setAccessible(true);
        postBlockMethod.setAccessible(true);
        reentryPreparationMethod.setAccessible(true);
        reentryStateMethod.setAccessible(true);


        String normalized =
                (String) normalize.invoke(
                        analyzer,
                        message
                );


        RecoveryMessageAnalysis legacy =
                analyzer.analyze(message);


        RecoveryRiskPreparationMetadata risk =
                (RecoveryRiskPreparationMetadata)
                        riskMethod.invoke(
                                analyzer,
                                normalized
                        );


        RecoveryPostBlockStateMetadata postBlock =
                (RecoveryPostBlockStateMetadata)
                        postBlockMethod.invoke(
                                analyzer,
                                normalized
                        );


        RecoveryReentryPreparationMetadata reentryPreparation =
                (RecoveryReentryPreparationMetadata)
                        reentryPreparationMethod.invoke(
                                analyzer,
                                normalized
                        );


        RecoveryReentryStateMetadata reentryState =
                (RecoveryReentryStateMetadata)
                        reentryStateMethod.invoke(
                                analyzer,
                                normalized
                        );


        System.out.println(
                "legacyAnalyzable="
                + legacy.analyzable()
        );

        System.out.println(
                "legacyHold="
                + safe(legacy.holdReason())
        );


        printMetadata(
                "risk",
                risk.detected(),
                risk.type()
        );

        printMetadata(
                "postBlock",
                postBlock.detected(),
                postBlock.type()
        );

        printMetadata(
                "reentryPreparation",
                reentryPreparation.detected(),
                reentryPreparation.type()
        );

        printMetadata(
                "reentryState",
                reentryState.detected(),
                reentryState.type()
        );
    }


    private static void printMetadata(
            String name,
            boolean detected,
            String type
    ) {

        System.out.println(
                name + "Detected="
                + detected
        );

        System.out.println(
                name + "Type="
                + safe(type)
        );
    }


    private static String safe(
            String value
    ) {

        return value == null
                ? "null"
                : value;
    }
}