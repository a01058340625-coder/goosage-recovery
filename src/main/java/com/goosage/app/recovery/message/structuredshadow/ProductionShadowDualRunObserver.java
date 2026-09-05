package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.goosage.app.recovery.message.RecoveryMessageAnalysis;
import com.goosage.domain.recovery.message.RecoveryMessageSignal;

@Component
public class ProductionShadowDualRunObserver {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ProductionShadowDualRunObserver.class
            );

    private final StructuredSentenceBoundaryAdapter
            sentenceBoundary =
            new StructuredSentenceBoundaryAdapter();

    private final CurrentStructuredSignalEngineShadow
            currentEngine =
            new CurrentStructuredSignalEngineShadow();

    private final StructuredEventSequenceShadow
            eventSequence =
            new StructuredEventSequenceShadow();

    private final CanonicalStructuredSignalEngineShadow
            canonicalEngine =
            new CanonicalStructuredSignalEngineShadow();

    private final CanonicalStructuredSignalTraceShadow
            traceEngine =
            new CanonicalStructuredSignalTraceShadow();


    public void observe(
            String message,
            RecoveryMessageAnalysis analysis
    ) {

        try {

            observeInternal(
                    message,
                    analysis
            );

        } catch (Exception error) {

            log.warn(
                    "CANONICAL_PRODUCTION_SHADOW_FAILED "
                    + "errorType={} message={}",
                    error.getClass().getSimpleName(),
                    error.getMessage()
            );
        }
    }


    private void observeInternal(
            String message,
            RecoveryMessageAnalysis analysis
    ) {

        List<String> sentences =
                sentenceBoundary.split(
                        message
                );

        int[] current =
                currentEngine.resolve(
                        sentences
                );

        List<StructuredEventShadow> events =
                eventSequence.resolve(
                        sentences
                );

        int[] canonical =
                canonicalEngine.resolve(
                        events
                );

        Map<String, Object> trace =
                traceEngine.trace(
                        events
                );


        int[] production =
                productionVector(
                        analysis
                );


        boolean currentCanonicalMatch =
                Arrays.equals(
                        current,
                        canonical
                );

        boolean productionCurrentMatch =
                production != null
                && Arrays.equals(
                        production,
                        current
                );

        boolean productionCanonicalMatch =
                production != null
                && Arrays.equals(
                        production,
                        canonical
                );


        log.info(
                "CANONICAL_PRODUCTION_SHADOW "
                + "decisionOwner=PRODUCTION "
                + "currentCanonicalMatch={} "
                + "productionCurrentMatch={} "
                + "productionCanonicalMatch={} "
                + "production={} "
                + "current={} "
                + "canonical={} "
                + "trace={}",
                currentCanonicalMatch,
                productionCurrentMatch,
                productionCanonicalMatch,
                vectorText(production),
                vectorText(current),
                vectorText(canonical),
                trace
        );
    }


    private int[] productionVector(
            RecoveryMessageAnalysis analysis
    ) {

        if (
                analysis == null
                || analysis.signal() == null
        ) {
            return null;
        }

        RecoveryMessageSignal signal =
                analysis.signal();

        return new int[] {
                signal.urgeLogDelta(),
                signal.betAttemptDelta(),
                signal.betBlockedDelta(),
                signal.recoveryActionDelta(),
                signal.relapseSignalDelta()
        };
    }


    private String vectorText(
            int[] vector
    ) {

        if (vector == null) {
            return "null";
        }

        return Arrays.toString(
                vector
        );
    }
}
