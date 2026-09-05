package com.goosage.app.recovery.message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ProductionOwnerSwitchPreservationCli {

    public static void main(String[] args) throws Exception {

        RuleBasedRecoveryMessageAnalyzer legacy =
                new RuleBasedRecoveryMessageAnalyzer();

        ProductionRecoveryMessageAnalyzer production =
                new ProductionRecoveryMessageAnalyzer(
                        legacy,
                        "RULE_BASED"
                );

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                System.in,
                                StandardCharsets.UTF_8
                        )
                );

        String line;

        while ((line = reader.readLine()) != null) {

            if (line.isBlank()) {
                continue;
            }

            String message =
                    new String(
                            Base64.getDecoder().decode(line),
                            StandardCharsets.UTF_8
                    );

            RecoveryMessageAnalysis legacyResult =
                    legacy.analyze(message);

            RecoveryMessageAnalysis productionResult =
                    production.analyze(message);

            boolean exact =
                    legacyResult.equals(
                            productionResult
                    );

            System.out.println(
                    exact
                            ? "MATCH"
                            : "MISMATCH"
            );

            if (!exact) {

                System.out.println(
                        "LEGACY="
                        + legacyResult
                );

                System.out.println(
                        "PRODUCTION="
                        + productionResult
                );
            }
        }
    }
}