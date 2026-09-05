package com.goosage.app.recovery.message.structuredshadow;

import com.goosage.app.recovery.message.signalshadow.ShadowSignalVector;

import com.goosage.app.recovery.message.protectiveshadow.ProtectiveAccessViewExitSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveActionReversalSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveAppDiscoveryNoOpenSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveExternalThenSelfChoiceSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveMotivatedNextStepStopSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveNextStepStopSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveSearchInputReversalSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveUnknownRiskSelfExitSequenceShadow;
import com.goosage.app.recovery.message.protectiveshadow.ProtectiveVoluntaryExitSequenceShadow;

import java.util.List;

public class CanonicalStructuredSignalEngineShadow {

    private final StructuredSignalMapperShadow signalMapper =
            new StructuredSignalMapperShadow();


    // ========================================================
    // URGE
    // ========================================================

    private final StrongDesireTemporalGuardedUrgeShadow
            strongDesireTemporalGuardedUrge =
            new StrongDesireTemporalGuardedUrgeShadow();

    private final NarrowPersistentCognitivePullUrgeShadow
            narrowPersistentCognitivePullUrge =
            new NarrowPersistentCognitivePullUrgeShadow();

    private final ExplicitGamblingThoughtUrgeShadow
            explicitGamblingThoughtUrge =
            new ExplicitGamblingThoughtUrgeShadow();

    private final RiskLinkedCognitivePullUrgeShadow
            riskLinkedCognitivePullUrge =
            new RiskLinkedCognitivePullUrgeShadow();

    private final TriggeredCompulsivePullUrgeShadow
            triggeredCompulsivePullUrge =
            new TriggeredCompulsivePullUrgeShadow();

    private final LaterCurrentUrgeNegationShadow
            laterCurrentUrgeNegation =
            new LaterCurrentUrgeNegationShadow();

    private final NonGamblingDesireTargetUrgeSuppressionShadow
            nonGamblingDesireTargetUrgeSuppression =
            new NonGamblingDesireTargetUrgeSuppressionShadow();


    // ========================================================
    // ATTEMPT
    // ========================================================

    private final RecentWagerCompletedAttemptShadow
            recentWagerCompletedAttempt =
            new RecentWagerCompletedAttemptShadow();

    private final SearchExecutionAttemptShadow
            searchExecutionAttempt =
            new SearchExecutionAttemptShadow();

    private final ConcreteWagerExecutionAttemptShadow
            concreteWagerExecutionAttempt =
            new ConcreteWagerExecutionAttemptShadow();

    private final ConcreteRiskInteractionAttemptShadow
            concreteRiskInteractionAttempt =
            new ConcreteRiskInteractionAttemptShadow();

    private final CompressedGamblingContinuationAttemptShadow
            compressedGamblingContinuationAttempt =
            new CompressedGamblingContinuationAttemptShadow();

    private final PartialSearchInputAttemptShadow
            partialSearchInputAttempt =
            new PartialSearchInputAttemptShadow();

    private final MissingSupportedProgressionAttemptShadow
            missingSupportedProgressionAttempt =
            new MissingSupportedProgressionAttemptShadow();

    private final FailedWagerSubmitAttemptShadow
            failedWagerSubmitAttempt =
            new FailedWagerSubmitAttemptShadow();

    private final PseudoProgressionAttemptSuppressionShadow
            pseudoProgressionAttemptSuppression =
            new PseudoProgressionAttemptSuppressionShadow();

    private final CompressedCompletionAttemptSuppressionShadow
            compressedCompletionAttemptSuppression =
            new CompressedCompletionAttemptSuppressionShadow();

    private final HabitualNarrativeAttemptSuppressionShadow
            habitualNarrativeAttemptSuppression =
            new HabitualNarrativeAttemptSuppressionShadow();

    private final PreActionNonExecutionAttemptSuppressionShadow
            preActionNonExecutionAttemptSuppression =
            new PreActionNonExecutionAttemptSuppressionShadow();

    private final LoginBalanceOnlyAttemptSuppressionShadow
            loginBalanceOnlyAttemptSuppression =
            new LoginBalanceOnlyAttemptSuppressionShadow();

    private final HistoricalCompletedWagerAttemptSuppressionShadow
            historicalCompletedWagerAttemptSuppression =
            new HistoricalCompletedWagerAttemptSuppressionShadow();


    // ========================================================
    // BLOCKED
    // ========================================================

    private final ProtectiveDisengagementBlockedShadow
            protectiveDisengagementBlocked =
            new ProtectiveDisengagementBlockedShadow();

    private final ProtectiveAccessViewExitSequenceShadow
            protectiveAccessViewExit =
            new ProtectiveAccessViewExitSequenceShadow();

    private final ProtectiveActionReversalSequenceShadow
            protectiveActionReversal =
            new ProtectiveActionReversalSequenceShadow();

    private final ProtectiveAppDiscoveryNoOpenSequenceShadow
            protectiveAppDiscoveryNoOpen =
            new ProtectiveAppDiscoveryNoOpenSequenceShadow();

    private final ProtectiveExternalThenSelfChoiceSequenceShadow
            protectiveExternalThenSelfChoice =
            new ProtectiveExternalThenSelfChoiceSequenceShadow();

    private final ProtectiveMotivatedNextStepStopSequenceShadow
            protectiveMotivatedNextStepStop =
            new ProtectiveMotivatedNextStepStopSequenceShadow();

    private final ProtectiveNextStepStopSequenceShadow
            protectiveNextStepStop =
            new ProtectiveNextStepStopSequenceShadow();

    private final ProtectiveSearchInputReversalSequenceShadow
            protectiveSearchInputReversal =
            new ProtectiveSearchInputReversalSequenceShadow();

    private final ProtectiveUnknownRiskSelfExitSequenceShadow
            protectiveUnknownRiskSelfExit =
            new ProtectiveUnknownRiskSelfExitSequenceShadow();

    private final ProtectiveVoluntaryExitSequenceShadow
            protectiveVoluntaryExit =
            new ProtectiveVoluntaryExitSequenceShadow();

    private final ExternalInterruptionBlockedSuppressionShadow
            externalInterruptionBlockedSuppression =
            new ExternalInterruptionBlockedSuppressionShadow();

    private final PostCompletionNaturalStopBlockedSuppressionShadow
            postCompletionNaturalStopBlockedSuppression =
            new PostCompletionNaturalStopBlockedSuppressionShadow();

    private final NoExecutedRiskActionBlockedSuppressionShadow
            noExecutedRiskActionBlockedSuppression =
            new NoExecutedRiskActionBlockedSuppressionShadow();

    private final BareSelfStopBlockedSuppressionShadow
            bareSelfStopBlockedSuppression =
            new BareSelfStopBlockedSuppressionShadow();


    private final RecoveryActionBlockedSuppressionShadow
            recoveryActionBlockedSuppression =
            new RecoveryActionBlockedSuppressionShadow();

    private final NonMotivatedWagerCancelBlockedSuppressionShadow
            nonMotivatedWagerCancelBlockedSuppression =
            new NonMotivatedWagerCancelBlockedSuppressionShadow();

    private final NegatedAccountBlockBlockedSuppressionShadow
            negatedAccountBlockBlockedSuppression =
            new NegatedAccountBlockBlockedSuppressionShadow();


    private final AccountBlockRecoveryAdministrationBlockedSuppressionShadow
            accountBlockRecoveryAdministrationBlockedSuppression =
            new AccountBlockRecoveryAdministrationBlockedSuppressionShadow();


    private final AccountUnblockReversalBlockedSuppressionShadow
            accountUnblockReversalBlockedSuppression =
            new AccountUnblockReversalBlockedSuppressionShadow();


    // ========================================================
    // RECOVERY
    // ========================================================

    private final RecoveryHistoryCarryShadow
            recoveryHistoryCarry =
            new RecoveryHistoryCarryShadow();

    private final FlexibleRecoveryGuardedShadow
            flexibleRecoveryGuarded =
            new FlexibleRecoveryGuardedShadow();

    private final HelpSeekingWriteRecoveryShadow
            helpSeekingWriteRecovery =
            new HelpSeekingWriteRecoveryShadow();

    private final CompletedProtectiveRecoveryActionShadow
            completedProtectiveRecoveryAction =
            new CompletedProtectiveRecoveryActionShadow();

    private final CompletedHelpSeekingRecoveryShadow
            completedHelpSeekingRecovery =
            new CompletedHelpSeekingRecoveryShadow();


    private final CompletedAccountBlockRecoveryShadow
            completedAccountBlockRecovery =
            new CompletedAccountBlockRecoveryShadow();


    // ========================================================
    // RELAPSE
    // ========================================================

    private final WagerCompletionRelapseShadow
            wagerCompletionRelapse =
            new WagerCompletionRelapseShadow();

    private final CompressedGamblingRelapseShadow
            compressedGamblingRelapse =
            new CompressedGamblingRelapseShadow();

    private final OngoingGamblingBehaviorRelapseShadow
            ongoingGamblingBehaviorRelapse =
            new OngoingGamblingBehaviorRelapseShadow();

    private final CompletedGamblingRelapseCarryShadow
            completedGamblingRelapseCarry =
            new CompletedGamblingRelapseCarryShadow();

    private final HistoricalFalseCompletionRelapseSuppressionShadow
            historicalFalseCompletionRelapseSuppression =
            new HistoricalFalseCompletionRelapseSuppressionShadow();


    public int[] resolve(
            List<StructuredEventShadow> events
    ) {

        int urge = 0;
        int attempt = 0;
        int blocked = 0;
        int recovery = 0;
        int relapse = 0;


        // ====================================================
        // PHASE 1
        // BASE SIGNAL MAPPER
        // ====================================================

        for (StructuredEventShadow structured : events) {

            ShadowSignalVector signal =
                    signalMapper.map(structured);

            urge = Math.max(
                    urge,
                    signal.urge()
            );

            attempt = Math.max(
                    attempt,
                    signal.attempt()
            );

            blocked = Math.max(
                    blocked,
                    signal.blocked()
            );

            recovery = Math.max(
                    recovery,
                    signal.recovery()
            );

            relapse = Math.max(
                    relapse,
                    signal.relapse()
            );
        }


        // ====================================================
        // PHASE 2
        // POSITIVE COMPONENT EVIDENCE
        // ====================================================


        // URGE_POSITIVE_EVIDENCE

        if (
                strongDesireTemporalGuardedUrge.resolve(events)
                || narrowPersistentCognitivePullUrge.resolve(events)
                || explicitGamblingThoughtUrge.resolve(events)
                || riskLinkedCognitivePullUrge.resolve(events)
                || triggeredCompulsivePullUrge.resolve(events)
        ) {
            urge = 1;
        }


        // ATTEMPT_SUPPORTED_PROGRESSION

        if (
                recentWagerCompletedAttempt.resolve(events)
                || searchExecutionAttempt.resolve(events)
                || concreteWagerExecutionAttempt.resolve(events)
                || concreteRiskInteractionAttempt.resolve(events)
                || compressedGamblingContinuationAttempt.resolve(events)
                || partialSearchInputAttempt.resolve(events)
                || missingSupportedProgressionAttempt.resolve(events)
                || failedWagerSubmitAttempt.resolve(events)
        ) {
            attempt = 1;
        }


        // BLOCKED_PROTECTIVE_STOP

        if (
                protectiveDisengagementBlocked.resolve(events)
                || protectiveAccessViewExit.resolve(events)
                || protectiveActionReversal.resolve(events)
                || protectiveAppDiscoveryNoOpen.resolve(events)
                || protectiveExternalThenSelfChoice.resolve(events)
                || protectiveMotivatedNextStepStop.resolve(events)
                || protectiveNextStepStop.resolve(events)
                || protectiveSearchInputReversal.resolve(events)
                || protectiveUnknownRiskSelfExit.resolve(events)
                || protectiveVoluntaryExit.resolve(events)
        ) {
            blocked = 1;
        }


        // RECOVERY_COMPLETED_PROTECTIVE_ACTION

        if (
                recoveryHistoryCarry.resolve(events)
                || flexibleRecoveryGuarded.resolve(events)
                || helpSeekingWriteRecovery.resolve(events)
                || completedProtectiveRecoveryAction.resolve(events)
                || completedHelpSeekingRecovery.resolve(events)

                || completedAccountBlockRecovery.resolve(events)
        ) {
            recovery = 1;
        }


        // RELAPSE_POSITIVE_EVIDENCE

        if (
                wagerCompletionRelapse.resolve(events)
                || compressedGamblingRelapse.resolve(events)
                || ongoingGamblingBehaviorRelapse.resolve(events)
                || completedGamblingRelapseCarry.resolve(events)
        ) {
            relapse = 1;
        }


        // ====================================================
        // PHASE 3
        // FINAL INVALIDATORS
        // ====================================================


        // URGE_INVALIDATOR

        if (
                laterCurrentUrgeNegation.resolve(events)
                || nonGamblingDesireTargetUrgeSuppression.resolve(events)
        ) {
            urge = 0;
        }


        // ATTEMPT_PROGRESSION_INVALIDATOR

        if (
                pseudoProgressionAttemptSuppression.resolve(events)
                || compressedCompletionAttemptSuppression.resolve(events)
                || habitualNarrativeAttemptSuppression.resolve(events)
                || preActionNonExecutionAttemptSuppression.resolve(events)
                || loginBalanceOnlyAttemptSuppression.resolve(events)
                || historicalCompletedWagerAttemptSuppression.resolve(events)
        ) {
            attempt = 0;
        }


        // BLOCKED_INVALIDATOR

        if (
                externalInterruptionBlockedSuppression.resolve(events)
                || postCompletionNaturalStopBlockedSuppression.resolve(events)
                || noExecutedRiskActionBlockedSuppression.resolve(events)
                || bareSelfStopBlockedSuppression.resolve(events)
                || recoveryActionBlockedSuppression.resolve(events)
                || nonMotivatedWagerCancelBlockedSuppression.resolve(events)
                || negatedAccountBlockBlockedSuppression.resolve(events)
                || accountBlockRecoveryAdministrationBlockedSuppression.resolve(events)

                || accountUnblockReversalBlockedSuppression.resolve(events)
        ) {
            blocked = 0;
        }


        // RELAPSE_INVALIDATOR

        if (
                historicalFalseCompletionRelapseSuppression.resolve(events)
        ) {
            relapse = 0;
        }


        return new int[] {
                urge,
                attempt,
                blocked,
                recovery,
                relapse
        };
    }
}
