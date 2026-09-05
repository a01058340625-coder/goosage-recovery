package com.goosage.app.recovery.message;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.goosage.app.recovery.message.structuredshadow.CanonicalStructuredSignalEngineShadow;
import com.goosage.app.recovery.message.structuredshadow.StructuredEventSequenceShadow;
import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;
import com.goosage.app.recovery.message.structuredshadow.StructuredSentenceBoundaryAdapter;
import com.goosage.domain.recovery.message.RecoveryMessageSignal;

@Component
public class ProductionRecoveryMessageAnalyzer {

    public enum OwnerMode {
        RULE_BASED,
        CANONICAL
    }

    private final RuleBasedRecoveryMessageAnalyzer legacyAnalyzer;

    private final StructuredSentenceBoundaryAdapter sentenceBoundary =
            new StructuredSentenceBoundaryAdapter();

    private final StructuredEventSequenceShadow eventSequence =
            new StructuredEventSequenceShadow();

    private final CanonicalStructuredSignalEngineShadow canonicalEngine =
            new CanonicalStructuredSignalEngineShadow();

    private final OwnerMode ownerMode;


    public ProductionRecoveryMessageAnalyzer(
            RuleBasedRecoveryMessageAnalyzer legacyAnalyzer,
            @Value("${goosage.recovery.message.owner:RULE_BASED}")
            String ownerMode
    ) {
        this.legacyAnalyzer = legacyAnalyzer;
        this.ownerMode = resolveOwnerMode(ownerMode);
    }


    public RecoveryMessageAnalysis analyze(String message) {

        if (ownerMode == OwnerMode.RULE_BASED) {
            return legacyAnalyzer.analyze(message);
        }

        return analyzeCanonical(message);
    }


    public OwnerMode ownerMode() {
        return ownerMode;
    }


    private RecoveryMessageAnalysis analyzeCanonical(
            String message
    ) {

        RecoveryMessageAnalysis legacy =
                legacyAnalyzer.analyze(message);


        if (mustPreserveLegacyEnvelopeHold(legacy)) {
            return legacy;
        }


        List<String> sentences =
                sentenceBoundary.split(message);

        List<StructuredEventShadow> events =
                eventSequence.resolve(sentences);

        int[] vector =
                canonicalEngine.resolve(events);


        int totalSignals =
                vector[0]
                + vector[1]
                + vector[2]
                + vector[3]
                + vector[4];


        boolean metadataKeepsAnalysisActive =
                legacy.postBlockStateMetadata().detected()
                || legacy.reentryPreparationMetadata().detected()
                || legacy.reentryStateMetadata().detected();


        if (
                totalSignals == 0
                && !metadataKeepsAnalysisActive
        ) {

            return new RecoveryMessageAnalysis(
                    message,
                    false,
                    null,
                    resolveCanonicalZeroVectorHoldReason(legacy),
                    legacy.riskPreparationMetadata(),
                    legacy.postBlockStateMetadata(),
                    legacy.reentryPreparationMetadata(),
                    legacy.reentryStateMetadata()
            );
        }


        RecoveryMessageSignal signal =
                new RecoveryMessageSignal(
                        vector[0],
                        vector[1],
                        vector[2],
                        vector[3],
                        vector[4],
                        resolveConfidence(totalSignals),
                        buildReason(vector)
                );


        return new RecoveryMessageAnalysis(
                message,
                true,
                signal,
                null,
                legacy.riskPreparationMetadata(),
                legacy.postBlockStateMetadata(),
                legacy.reentryPreparationMetadata(),
                legacy.reentryStateMetadata()
        );
    }


    private boolean mustPreserveLegacyEnvelopeHold(
            RecoveryMessageAnalysis legacy
    ) {

        if (legacy == null) {
            return false;
        }

        if (legacy.analyzable()) {
            return false;
        }

        String reason =
                legacy.holdReason();

        return
                "EMPTY_MESSAGE".equals(reason)
                || "MESSAGE_TOO_SHORT".equals(reason)
                || "HYPOTHETICAL_CONTEXT".equals(reason);
    }


    private String resolveCanonicalZeroVectorHoldReason(
            RecoveryMessageAnalysis legacy
    ) {

        if (legacy == null) {
            return "NO_SUPPORTED_SIGNAL";
        }

        String reason =
                legacy.holdReason();

        if (
                "NO_CURRENT_SUPPORTED_SIGNAL".equals(reason)
                || "NO_SUPPORTED_SELF_SIGNAL".equals(reason)
                || "NO_SUPPORTED_SIGNAL".equals(reason)
        ) {
            return reason;
        }

        return "NO_SUPPORTED_SIGNAL";
    }


    private double resolveConfidence(
            int totalSignals
    ) {

        if (totalSignals >= 3) {
            return 0.90;
        }

        if (totalSignals == 2) {
            return 0.85;
        }

        if (totalSignals == 1) {
            return 0.80;
        }

        return 0.0;
    }


    private String buildReason(
            int[] vector
    ) {

        return (
                "urge=%d, attempt=%d, blocked=%d, recovery=%d, relapse=%d"
        ).formatted(
                vector[0],
                vector[1],
                vector[2],
                vector[3],
                vector[4]
        );
    }


    private OwnerMode resolveOwnerMode(
            String raw
    ) {

        if (raw == null) {
            return OwnerMode.RULE_BASED;
        }

        try {

            return OwnerMode.valueOf(
                    raw.trim().toUpperCase()
            );

        } catch (IllegalArgumentException error) {

            return OwnerMode.RULE_BASED;
        }
    }
}