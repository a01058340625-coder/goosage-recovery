package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

import com.goosage.app.recovery.message.protectiveshadow.ProtectiveVoluntaryExitSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveActionReversalSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveAccessViewExitSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveNextStepStopSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveSearchInputReversalSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveExternalThenSelfChoiceSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveUnknownRiskSelfExitSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveMotivatedNextStepStopSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveAppDiscoveryNoOpenSequenceShadow;
import com.goosage.app.recovery.message.signalshadow.ShadowSignalVector;

public class StructuredSignalSequenceShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequenceResolver =
                new StructuredEventSequenceShadow();

        StructuredSignalMapperShadow signalMapper =
                new StructuredSignalMapperShadow();

        ProtectiveVoluntaryExitSequenceShadow sequenceProtective =
                new ProtectiveVoluntaryExitSequenceShadow();

        ProtectiveActionReversalSequenceShadow actionReversal =
                new ProtectiveActionReversalSequenceShadow();

        ProtectiveAccessViewExitSequenceShadow accessViewExit =
                new ProtectiveAccessViewExitSequenceShadow();

        ProtectiveNextStepStopSequenceShadow nextStepStop =
                new ProtectiveNextStepStopSequenceShadow();

        ProtectiveSearchInputReversalSequenceShadow searchInputReversalSequence =
                new ProtectiveSearchInputReversalSequenceShadow();

        ProtectiveExternalThenSelfChoiceSequenceShadow externalThenSelfChoice =
                new ProtectiveExternalThenSelfChoiceSequenceShadow();

        ProtectiveUnknownRiskSelfExitSequenceShadow unknownRiskSelfExit =
                new ProtectiveUnknownRiskSelfExitSequenceShadow();

        ProtectiveMotivatedNextStepStopSequenceShadow motivatedNextStepStop =
                new ProtectiveMotivatedNextStepStopSequenceShadow();

        ProtectiveAppDiscoveryNoOpenSequenceShadow appDiscoveryNoOpen =
                new ProtectiveAppDiscoveryNoOpenSequenceShadow();

        List<String> sentences =
                Arrays.asList(args);

        List<StructuredEventShadow> events =
                sequenceResolver.resolve(sentences);

        int urge = 0;
        int attempt = 0;
        int blocked = 0;
        int recovery = 0;
        int relapse = 0;

        int index = 1;

        for (StructuredEventShadow structured : events) {

            ShadowSignalVector signal =
                    signalMapper.map(structured);

            System.out.println(
                    "event" + index
                    + "|role=" + structured.temporalRole()
                    + "|action=" + structured.event().actionType()
                    + "|stage=" + structured.event().actionStage()
                    + "|stop=" + structured.event().stopCause()
                    + "|protective=" + structured.protectiveOutcome()
                    + "|signal="
                    + signal.urge() + ","
                    + signal.attempt() + ","
                    + signal.blocked() + ","
                    + signal.recovery() + ","
                    + signal.relapse()
            );

            urge = Math.max(urge, signal.urge());
            attempt = Math.max(attempt, signal.attempt());
            blocked = Math.max(blocked, signal.blocked());
            recovery = Math.max(recovery, signal.recovery());
            relapse = Math.max(relapse, signal.relapse());

            index++;
        }

        RecentWagerCompletedAttemptShadow recentWagerCompletedAttempt =
                new RecentWagerCompletedAttemptShadow();

        if (recentWagerCompletedAttempt.resolve(events)) {
            attempt = 1;
        }

        SearchExecutionAttemptShadow searchExecutionAttempt =
                new SearchExecutionAttemptShadow();

        if (searchExecutionAttempt.resolve(events)) {
            attempt = 1;
        }

        ConcreteWagerExecutionAttemptShadow concreteWagerExecutionAttempt =
                new ConcreteWagerExecutionAttemptShadow();

        if (concreteWagerExecutionAttempt.resolve(events)) {
            attempt = 1;
        }

        ConcreteRiskInteractionAttemptShadow concreteRiskInteractionAttempt =
                new ConcreteRiskInteractionAttemptShadow();

        if (concreteRiskInteractionAttempt.resolve(events)) {
            attempt = 1;
        }

        CompressedGamblingContinuationAttemptShadow compressedGamblingContinuationAttempt =
                new CompressedGamblingContinuationAttemptShadow();

        if (compressedGamblingContinuationAttempt.resolve(events)) {
            attempt = 1;
        }

        PartialSearchInputAttemptShadow partialSearchInputAttempt =
                new PartialSearchInputAttemptShadow();

        if (partialSearchInputAttempt.resolve(events)) {
            attempt = 1;
        }

        WagerCompletionRelapseShadow wagerCompletionRelapse =
                new WagerCompletionRelapseShadow();

        if (wagerCompletionRelapse.resolve(events)) {
            relapse = 1;
        }

        CompressedGamblingRelapseShadow compressedGamblingRelapse =
                new CompressedGamblingRelapseShadow();

        if (compressedGamblingRelapse.resolve(events)) {
            relapse = 1;
        }

        RecoveryHistoryCarryShadow recoveryHistoryCarry =
                new RecoveryHistoryCarryShadow();

        if (recoveryHistoryCarry.resolve(events)) {
            recovery = 1;
        }

        FlexibleRecoveryGuardedShadow flexibleRecoveryGuarded =
                new FlexibleRecoveryGuardedShadow();

        if (flexibleRecoveryGuarded.resolve(events)) {
            recovery = 1;
        }

        HelpSeekingWriteRecoveryShadow helpSeekingWriteRecovery =
                new HelpSeekingWriteRecoveryShadow();

        if (helpSeekingWriteRecovery.resolve(events)) {
            recovery = 1;
        }

        StrongDesireTemporalGuardedUrgeShadow strongDesireTemporalGuardedUrge =
                new StrongDesireTemporalGuardedUrgeShadow();

        if (strongDesireTemporalGuardedUrge.resolve(events)) {
            urge = 1;
        }

        NarrowPersistentCognitivePullUrgeShadow narrowPersistentCognitivePullUrge =
                new NarrowPersistentCognitivePullUrgeShadow();

        if (narrowPersistentCognitivePullUrge.resolve(events)) {
            urge = 1;
        }

        ExplicitGamblingThoughtUrgeShadow explicitGamblingThoughtUrge =
                new ExplicitGamblingThoughtUrgeShadow();

        if (explicitGamblingThoughtUrge.resolve(events)) {
            urge = 1;
        }

        LaterCurrentUrgeNegationShadow laterCurrentUrgeNegation =
                new LaterCurrentUrgeNegationShadow();

        if (laterCurrentUrgeNegation.resolve(events)) {
            urge = 0;
        }

        PseudoProgressionAttemptSuppressionShadow pseudoProgressionAttemptSuppression =
                new PseudoProgressionAttemptSuppressionShadow();

        if (pseudoProgressionAttemptSuppression.resolve(events)) {
            attempt = 0;
        }

        CompressedCompletionAttemptSuppressionShadow compressedCompletionAttemptSuppression =
                new CompressedCompletionAttemptSuppressionShadow();

        if (compressedCompletionAttemptSuppression.resolve(events)) {
            attempt = 0;
        }

        HabitualNarrativeAttemptSuppressionShadow habitualNarrativeAttemptSuppression =
                new HabitualNarrativeAttemptSuppressionShadow();

        if (habitualNarrativeAttemptSuppression.resolve(events)) {
            attempt = 0;
        }

        PreActionNonExecutionAttemptSuppressionShadow preActionNonExecutionAttemptSuppression =
                new PreActionNonExecutionAttemptSuppressionShadow();

        if (preActionNonExecutionAttemptSuppression.resolve(events)) {
            attempt = 0;
        }

        MissingSupportedProgressionAttemptShadow missingSupportedProgressionAttempt =
                new MissingSupportedProgressionAttemptShadow();

        if (missingSupportedProgressionAttempt.resolve(events)) {
            attempt = 1;
        }

        ExternalInterruptionBlockedSuppressionShadow externalInterruptionBlockedSuppression =
                new ExternalInterruptionBlockedSuppressionShadow();

        if (externalInterruptionBlockedSuppression.resolve(events)) {
            blocked = 0;
        }

        PostCompletionNaturalStopBlockedSuppressionShadow postCompletionNaturalStopBlockedSuppression =
                new PostCompletionNaturalStopBlockedSuppressionShadow();

        if (postCompletionNaturalStopBlockedSuppression.resolve(events)) {
            blocked = 0;
        }

        NoExecutedRiskActionBlockedSuppressionShadow noExecutedRiskActionBlockedSuppression =
                new NoExecutedRiskActionBlockedSuppressionShadow();

        if (noExecutedRiskActionBlockedSuppression.resolve(events)) {
            blocked = 0;
        }

        ProtectiveDisengagementBlockedShadow protectiveDisengagementBlocked =
                new ProtectiveDisengagementBlockedShadow();

        if (protectiveDisengagementBlocked.resolve(events)) {
            blocked = 1;
        }

        RiskLinkedCognitivePullUrgeShadow riskLinkedCognitivePullUrge =
                new RiskLinkedCognitivePullUrgeShadow();

        if (riskLinkedCognitivePullUrge.resolve(events)) {
            urge = 1;
        }

        TriggeredCompulsivePullUrgeShadow triggeredCompulsivePullUrge =
                new TriggeredCompulsivePullUrgeShadow();

        if (triggeredCompulsivePullUrge.resolve(events)) {
            urge = 1;
        }

        CompletedProtectiveRecoveryActionShadow completedProtectiveRecoveryAction =
                new CompletedProtectiveRecoveryActionShadow();

        if (completedProtectiveRecoveryAction.resolve(events)) {
            recovery = 1;
        }

        OngoingGamblingBehaviorRelapseShadow ongoingGamblingBehaviorRelapse =
                new OngoingGamblingBehaviorRelapseShadow();

        if (ongoingGamblingBehaviorRelapse.resolve(events)) {
            relapse = 1;
        }

        HistoricalFalseCompletionRelapseSuppressionShadow historicalFalseCompletionRelapseSuppression =
                new HistoricalFalseCompletionRelapseSuppressionShadow();

        if (historicalFalseCompletionRelapseSuppression.resolve(events)) {
            relapse = 0;
        }

        NonGamblingDesireTargetUrgeSuppressionShadow nonGamblingDesireTargetUrgeSuppression =
                new NonGamblingDesireTargetUrgeSuppressionShadow();

        if (nonGamblingDesireTargetUrgeSuppression.resolve(events)) {
            urge = 0;
        }


        if (
                sequenceProtective.resolve(events)
                || actionReversal.resolve(events)
                || accessViewExit.resolve(events)
                || nextStepStop.resolve(events)
                || searchInputReversalSequence.resolve(events)
                || externalThenSelfChoice.resolve(events)
                || unknownRiskSelfExit.resolve(events)
                || motivatedNextStepStop.resolve(events)
                || appDiscoveryNoOpen.resolve(events)
        ) {
            blocked = 1;
        }

        BareSelfStopBlockedSuppressionShadow bareSelfStopBlockedSuppression =
                new BareSelfStopBlockedSuppressionShadow();

        if (bareSelfStopBlockedSuppression.resolve(events)) {
            blocked = 0;
        }

        LoginBalanceOnlyAttemptSuppressionShadow loginBalanceOnlyAttemptSuppression =
                new LoginBalanceOnlyAttemptSuppressionShadow();

        if (loginBalanceOnlyAttemptSuppression.resolve(events)) {
            attempt = 0;
        }

        System.out.println(
                "aggregate="
                + urge + ","
                + attempt + ","
                + blocked + ","
                + recovery + ","
                + relapse
        );
    }
}
