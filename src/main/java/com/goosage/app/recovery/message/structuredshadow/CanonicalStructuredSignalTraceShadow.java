package com.goosage.app.recovery.message.structuredshadow;

import com.goosage.app.recovery.message.protectiveshadow.ProtectiveAccessViewExitSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveActionReversalSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveAppDiscoveryNoOpenSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveExternalThenSelfChoiceSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveMotivatedNextStepStopSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveNextStepStopSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveSearchInputReversalSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveUnknownRiskSelfExitSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveVoluntaryExitSequenceShadow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CanonicalStructuredSignalTraceShadow {

    private final CanonicalStructuredSignalEngineShadow engine =
            new CanonicalStructuredSignalEngineShadow();


    public Map<String, Object> trace(
            List<StructuredEventShadow> events
    ) {

        List<Map<String, Object>> components =
                new ArrayList<>();


        // ====================================================
        // URGE_POSITIVE_EVIDENCE
        // ====================================================

        List<String> urgePositive =
                new ArrayList<>();

        hit(
                urgePositive,
                "StrongDesireTemporalGuardedUrgeShadow",
                new StrongDesireTemporalGuardedUrgeShadow()
                        .resolve(events)
        );

        hit(
                urgePositive,
                "NarrowPersistentCognitivePullUrgeShadow",
                new NarrowPersistentCognitivePullUrgeShadow()
                        .resolve(events)
        );

        hit(
                urgePositive,
                "ExplicitGamblingThoughtUrgeShadow",
                new ExplicitGamblingThoughtUrgeShadow()
                        .resolve(events)
        );

        hit(
                urgePositive,
                "RiskLinkedCognitivePullUrgeShadow",
                new RiskLinkedCognitivePullUrgeShadow()
                        .resolve(events)
        );

        hit(
                urgePositive,
                "TriggeredCompulsivePullUrgeShadow",
                new TriggeredCompulsivePullUrgeShadow()
                        .resolve(events)
        );

        components.add(
                component(
                        "URGE_POSITIVE_EVIDENCE",
                        urgePositive,
                        "POSITIVE"
                )
        );


        // ====================================================
        // URGE_INVALIDATOR
        // ====================================================

        List<String> urgeInvalidator =
                new ArrayList<>();

        hit(
                urgeInvalidator,
                "LaterCurrentUrgeNegationShadow",
                new LaterCurrentUrgeNegationShadow()
                        .resolve(events)
        );

        hit(
                urgeInvalidator,
                "NonGamblingDesireTargetUrgeSuppressionShadow",
                new NonGamblingDesireTargetUrgeSuppressionShadow()
                        .resolve(events)
        );

        components.add(
                component(
                        "URGE_INVALIDATOR",
                        urgeInvalidator,
                        "INVALIDATE"
                )
        );


        // ====================================================
        // ATTEMPT_SUPPORTED_PROGRESSION
        // ====================================================

        List<String> attemptPositive =
                new ArrayList<>();

        hit(
                attemptPositive,
                "RecentWagerCompletedAttemptShadow",
                new RecentWagerCompletedAttemptShadow()
                        .resolve(events)
        );

        hit(
                attemptPositive,
                "SearchExecutionAttemptShadow",
                new SearchExecutionAttemptShadow()
                        .resolve(events)
        );

        hit(
                attemptPositive,
                "ConcreteWagerExecutionAttemptShadow",
                new ConcreteWagerExecutionAttemptShadow()
                        .resolve(events)
        );

        hit(
                attemptPositive,
                "ConcreteRiskInteractionAttemptShadow",
                new ConcreteRiskInteractionAttemptShadow()
                        .resolve(events)
        );

        hit(
                attemptPositive,
                "CompressedGamblingContinuationAttemptShadow",
                new CompressedGamblingContinuationAttemptShadow()
                        .resolve(events)
        );

        hit(
                attemptPositive,
                "PartialSearchInputAttemptShadow",
                new PartialSearchInputAttemptShadow()
                        .resolve(events)
        );

        hit(
                attemptPositive,
                "MissingSupportedProgressionAttemptShadow",
                new MissingSupportedProgressionAttemptShadow()
                        .resolve(events)
        );

        hit(
                attemptPositive,
                "FailedWagerSubmitAttemptShadow",
                new FailedWagerSubmitAttemptShadow()
                        .resolve(events)
        );

        components.add(
                component(
                        "ATTEMPT_SUPPORTED_PROGRESSION",
                        attemptPositive,
                        "POSITIVE"
                )
        );


        // ====================================================
        // ATTEMPT_PROGRESSION_INVALIDATOR
        // ====================================================

        List<String> attemptInvalidator =
                new ArrayList<>();

        hit(
                attemptInvalidator,
                "PseudoProgressionAttemptSuppressionShadow",
                new PseudoProgressionAttemptSuppressionShadow()
                        .resolve(events)
        );

        hit(
                attemptInvalidator,
                "CompressedCompletionAttemptSuppressionShadow",
                new CompressedCompletionAttemptSuppressionShadow()
                        .resolve(events)
        );

        hit(
                attemptInvalidator,
                "HabitualNarrativeAttemptSuppressionShadow",
                new HabitualNarrativeAttemptSuppressionShadow()
                        .resolve(events)
        );

        hit(
                attemptInvalidator,
                "PreActionNonExecutionAttemptSuppressionShadow",
                new PreActionNonExecutionAttemptSuppressionShadow()
                        .resolve(events)
        );

        hit(
                attemptInvalidator,
                "LoginBalanceOnlyAttemptSuppressionShadow",
                new LoginBalanceOnlyAttemptSuppressionShadow()
                        .resolve(events)
        );

        hit(
                attemptInvalidator,
                "HistoricalCompletedWagerAttemptSuppressionShadow",
                new HistoricalCompletedWagerAttemptSuppressionShadow()
                        .resolve(events)
        );

        components.add(
                component(
                        "ATTEMPT_PROGRESSION_INVALIDATOR",
                        attemptInvalidator,
                        "INVALIDATE"
                )
        );


        // ====================================================
        // BLOCKED_PROTECTIVE_STOP
        // ====================================================

        List<String> blockedPositive =
                new ArrayList<>();

        hit(
                blockedPositive,
                "ProtectiveDisengagementBlockedShadow",
                new ProtectiveDisengagementBlockedShadow()
                        .resolve(events)
        );

        hit(
                blockedPositive,
                "ProtectiveAccessViewExitSequenceShadow",
                new ProtectiveAccessViewExitSequenceShadow()
                        .resolve(events)
        );

        hit(
                blockedPositive,
                "ProtectiveActionReversalSequenceShadow",
                new ProtectiveActionReversalSequenceShadow()
                        .resolve(events)
        );

        hit(
                blockedPositive,
                "ProtectiveAppDiscoveryNoOpenSequenceShadow",
                new ProtectiveAppDiscoveryNoOpenSequenceShadow()
                        .resolve(events)
        );

        hit(
                blockedPositive,
                "ProtectiveExternalThenSelfChoiceSequenceShadow",
                new ProtectiveExternalThenSelfChoiceSequenceShadow()
                        .resolve(events)
        );

        hit(
                blockedPositive,
                "ProtectiveMotivatedNextStepStopSequenceShadow",
                new ProtectiveMotivatedNextStepStopSequenceShadow()
                        .resolve(events)
        );

        hit(
                blockedPositive,
                "ProtectiveNextStepStopSequenceShadow",
                new ProtectiveNextStepStopSequenceShadow()
                        .resolve(events)
        );

        hit(
                blockedPositive,
                "ProtectiveSearchInputReversalSequenceShadow",
                new ProtectiveSearchInputReversalSequenceShadow()
                        .resolve(events)
        );

        hit(
                blockedPositive,
                "ProtectiveUnknownRiskSelfExitSequenceShadow",
                new ProtectiveUnknownRiskSelfExitSequenceShadow()
                        .resolve(events)
        );

        hit(
                blockedPositive,
                "ProtectiveVoluntaryExitSequenceShadow",
                new ProtectiveVoluntaryExitSequenceShadow()
                        .resolve(events)
        );

        components.add(
                component(
                        "BLOCKED_PROTECTIVE_STOP",
                        blockedPositive,
                        "POSITIVE"
                )
        );


        // ====================================================
        // BLOCKED_INVALIDATOR
        // ====================================================

        List<String> blockedInvalidator =
                new ArrayList<>();

        hit(
                blockedInvalidator,
                "ExternalInterruptionBlockedSuppressionShadow",
                new ExternalInterruptionBlockedSuppressionShadow()
                        .resolve(events)
        );

        hit(
                blockedInvalidator,
                "PostCompletionNaturalStopBlockedSuppressionShadow",
                new PostCompletionNaturalStopBlockedSuppressionShadow()
                        .resolve(events)
        );

        hit(
                blockedInvalidator,
                "NoExecutedRiskActionBlockedSuppressionShadow",
                new NoExecutedRiskActionBlockedSuppressionShadow()
                        .resolve(events)
        );

        hit(
                blockedInvalidator,
                "BareSelfStopBlockedSuppressionShadow",
                new BareSelfStopBlockedSuppressionShadow()
                        .resolve(events)
        );


        hit(
                blockedInvalidator,
                "RecoveryActionBlockedSuppressionShadow",
                new RecoveryActionBlockedSuppressionShadow()
                        .resolve(events)
        );

        hit(
                blockedInvalidator,
                "NonMotivatedWagerCancelBlockedSuppressionShadow",
                new NonMotivatedWagerCancelBlockedSuppressionShadow()
                        .resolve(events)
        );

        hit(
                blockedInvalidator,
                "NegatedAccountBlockBlockedSuppressionShadow",
                new NegatedAccountBlockBlockedSuppressionShadow()
                        .resolve(events)
        );

        hit(
                blockedInvalidator,
                "AccountBlockRecoveryAdministrationBlockedSuppressionShadow",
                new AccountBlockRecoveryAdministrationBlockedSuppressionShadow()
                        .resolve(events)
        );

        hit(
                blockedInvalidator,
                "AccountUnblockReversalBlockedSuppressionShadow",
                new AccountUnblockReversalBlockedSuppressionShadow()
                        .resolve(events)
        );
        components.add(
                component(
                        "BLOCKED_INVALIDATOR",
                        blockedInvalidator,
                        "INVALIDATE"
                )
        );


        // ====================================================
        // RECOVERY_COMPLETED_PROTECTIVE_ACTION
        // ====================================================

        List<String> recoveryPositive =
                new ArrayList<>();

        hit(
                recoveryPositive,
                "RecoveryHistoryCarryShadow",
                new RecoveryHistoryCarryShadow()
                        .resolve(events)
        );

        hit(
                recoveryPositive,
                "FlexibleRecoveryGuardedShadow",
                new FlexibleRecoveryGuardedShadow()
                        .resolve(events)
        );

        hit(
                recoveryPositive,
                "HelpSeekingWriteRecoveryShadow",
                new HelpSeekingWriteRecoveryShadow()
                        .resolve(events)
        );

        hit(
                recoveryPositive,
                "CompletedProtectiveRecoveryActionShadow",
                new CompletedProtectiveRecoveryActionShadow()
                        .resolve(events)
        );

        hit(
                recoveryPositive,
                "CompletedHelpSeekingRecoveryShadow",
                new CompletedHelpSeekingRecoveryShadow()
                        .resolve(events)
        );

        hit(
                recoveryPositive,
                "CompletedAccountBlockRecoveryShadow",
                new CompletedAccountBlockRecoveryShadow()
                        .resolve(events)
        );

        components.add(
                component(
                        "RECOVERY_COMPLETED_PROTECTIVE_ACTION",
                        recoveryPositive,
                        "POSITIVE"
                )
        );


        // ====================================================
        // RELAPSE_POSITIVE_EVIDENCE
        // ====================================================

        List<String> relapsePositive =
                new ArrayList<>();

        hit(
                relapsePositive,
                "WagerCompletionRelapseShadow",
                new WagerCompletionRelapseShadow()
                        .resolve(events)
        );

        hit(
                relapsePositive,
                "CompressedGamblingRelapseShadow",
                new CompressedGamblingRelapseShadow()
                        .resolve(events)
        );

        hit(
                relapsePositive,
                "OngoingGamblingBehaviorRelapseShadow",
                new OngoingGamblingBehaviorRelapseShadow()
                        .resolve(events)
        );

        hit(
                relapsePositive,
                "CompletedGamblingRelapseCarryShadow",
                new CompletedGamblingRelapseCarryShadow()
                        .resolve(events)
        );

        components.add(
                component(
                        "RELAPSE_POSITIVE_EVIDENCE",
                        relapsePositive,
                        "POSITIVE"
                )
        );


        // ====================================================
        // RELAPSE_INVALIDATOR
        // ====================================================

        List<String> relapseInvalidator =
                new ArrayList<>();

        hit(
                relapseInvalidator,
                "HistoricalFalseCompletionRelapseSuppressionShadow",
                new HistoricalFalseCompletionRelapseSuppressionShadow()
                        .resolve(events)
        );

        components.add(
                component(
                        "RELAPSE_INVALIDATOR",
                        relapseInvalidator,
                        "INVALIDATE"
                )
        );


        // ====================================================
        // FINAL VECTOR
        // ====================================================

        int[] finalVector =
                engine.resolve(events);

        Map<String, Integer> signal =
                new LinkedHashMap<>();

        signal.put("urge", finalVector[0]);
        signal.put("attempt", finalVector[1]);
        signal.put("blocked", finalVector[2]);
        signal.put("recovery", finalVector[3]);
        signal.put("relapse", finalVector[4]);


        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "engine",
                "CanonicalStructuredSignalEngineShadow"
        );

        result.put(
                "mode",
                "SHADOW_OBSERVABILITY"
        );

        result.put(
                "eventCount",
                events.size()
        );

        result.put(
                "components",
                components
        );

        result.put(
                "finalSignalVector",
                signal
        );

        result.put(
                "eventIndexTrace",
                "UNAVAILABLE_AT_CURRENT_RULE_API"
        );

        result.put(
                "decisionOwner",
                "CURRENT_STRUCTURED"
        );

        result.put(
                "canonicalRole",
                "SHADOW_ONLY"
        );

        return result;
    }


    private static void hit(
            List<String> rules,
            String name,
            boolean matched
    ) {

        if (matched) {
            rules.add(name);
        }
    }


    private static Map<String, Object> component(
            String name,
            List<String> rules,
            String effect
    ) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "component",
                name
        );

        result.put(
                "hit",
                !rules.isEmpty()
        );

        result.put(
                "matchedRules",
                rules
        );

        result.put(
                "eventIndexes",
                List.of()
        );

        result.put(
                "eventIndexStatus",
                "UNAVAILABLE_AT_CURRENT_RULE_API"
        );

        result.put(
                "finalAxisEffect",
                rules.isEmpty()
                        ? "NONE"
                        : effect
        );

        return result;
    }
}
