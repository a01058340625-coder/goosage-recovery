package com.goosage.api.view.recovery.message;

import com.goosage.app.recovery.message.RecoveryMessageProjection;
import com.goosage.app.recovery.message.brain.BrainRecoveryDryRunResponse;
import com.goosage.app.recovery.message.brain.BrainRecoveryDryRunResult;
import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoveryState;
import com.goosage.domain.recovery.message.RecoveryMessageSignal;
import com.goosage.domain.recovery.message.RecoveryPostBlockStateMetadata;
import com.goosage.domain.recovery.message.RecoveryReentryPreparationMetadata;
import com.goosage.domain.recovery.message.RecoveryReentryStateMetadata;
import com.goosage.domain.recovery.message.RecoveryRiskPreparationMetadata;

public record RecoveryMessageAnalyzeResponse(
        boolean analyzable,
        String holdReason,
        String originalMessage,
        SignalResponse signal,
        RiskPreparationResponse riskPreparation,
        PostBlockStateResponse postBlockState,
        ReentryPreparationResponse reentryPreparation,
        ReentryStateResponse reentryState,
        SnapshotResponse baseSnapshot,
        SnapshotResponse projectedSnapshot,
        BrainResponse brain
) {

    public static RecoveryMessageAnalyzeResponse from(
            RecoveryMessageProjection projection
    ) {
        if (projection == null) {
            throw new IllegalArgumentException("projection is required");
        }

        RecoveryMessageSignal signal =
                projection.analysis() == null
                        ? null
                        : projection.analysis().signal();

        return new RecoveryMessageAnalyzeResponse(
                projection.analysis() != null
                        && projection.analysis().analyzable(),
                projection.analysis() == null
                        ? "ANALYSIS_MISSING"
                        : projection.analysis().holdReason(),
                projection.analysis() == null
                        ? null
                        : projection.analysis().originalMessage(),
                SignalResponse.from(signal),
                RiskPreparationResponse.from(
                        projection.analysis() == null
                                ? null
                                : projection.analysis()
                                        .riskPreparationMetadata()
                ),
                PostBlockStateResponse.from(
                        projection.analysis() == null
                                ? null
                                : projection.analysis()
                                        .postBlockStateMetadata()
                ),
                ReentryPreparationResponse.from(
                        projection.analysis() == null
                                ? null
                                : projection.analysis()
                                        .reentryPreparationMetadata()
                ),
                ReentryStateResponse.from(
                        projection.analysis() == null
                                ? null
                                : projection.analysis()
                                        .reentryStateMetadata()
                ),
                SnapshotResponse.from(projection.baseSnapshot()),
                SnapshotResponse.from(projection.projectedSnapshot()),
                BrainResponse.from(projection.brainResult())
        );
    }

    public record SignalResponse(
            int urgeLogDelta,
            int betAttemptDelta,
            int betBlockedDelta,
            int recoveryActionDelta,
            int relapseSignalDelta,
            double confidence,
            String reason
    ) {

        public static SignalResponse from(RecoveryMessageSignal signal) {
            if (signal == null) {
                return null;
            }

            return new SignalResponse(
                    signal.urgeLogDelta(),
                    signal.betAttemptDelta(),
                    signal.betBlockedDelta(),
                    signal.recoveryActionDelta(),
                    signal.relapseSignalDelta(),
                    signal.confidence(),
                    signal.reason()
            );
        }
    }

    public record RiskPreparationResponse(
            boolean detected,
            String type,
            double confidence,
            String reason
    ) {

        public static RiskPreparationResponse from(
                RecoveryRiskPreparationMetadata metadata
        ) {
            RecoveryRiskPreparationMetadata safeMetadata =
                    metadata == null
                            ? RecoveryRiskPreparationMetadata.none()
                            : metadata;

            return new RiskPreparationResponse(
                    safeMetadata.detected(),
                    safeMetadata.type(),
                    safeMetadata.confidence(),
                    safeMetadata.reason()
            );
        }
    }

    public record PostBlockStateResponse(
            boolean detected,
            String type,
            double confidence,
            String reason
    ) {

        public static PostBlockStateResponse from(
                RecoveryPostBlockStateMetadata metadata
        ) {
            RecoveryPostBlockStateMetadata safeMetadata =
                    metadata == null
                            ? RecoveryPostBlockStateMetadata.none()
                            : metadata;

            return new PostBlockStateResponse(
                    safeMetadata.detected(),
                    safeMetadata.type(),
                    safeMetadata.confidence(),
                    safeMetadata.reason()
            );
        }
    }
    public record ReentryPreparationResponse(
            boolean detected,
            String type,
            double confidence,
            String reason
    ) {

        public static ReentryPreparationResponse from(
                RecoveryReentryPreparationMetadata metadata
        ) {
            RecoveryReentryPreparationMetadata safeMetadata =
                    metadata == null
                            ? RecoveryReentryPreparationMetadata.none()
                            : metadata;

            return new ReentryPreparationResponse(
                    safeMetadata.detected(),
                    safeMetadata.type(),
                    safeMetadata.confidence(),
                    safeMetadata.reason()
            );
        }
    }

    public record ReentryStateResponse(
            boolean detected,
            String type,
            double confidence,
            String reason
    ) {

        public static ReentryStateResponse from(
                RecoveryReentryStateMetadata metadata
        ) {
            RecoveryReentryStateMetadata safeMetadata =
                    metadata == null
                            ? RecoveryReentryStateMetadata.none()
                            : metadata;

            return new ReentryStateResponse(
                    safeMetadata.detected(),
                    safeMetadata.type(),
                    safeMetadata.confidence(),
                    safeMetadata.reason()
            );
        }
    }

    public record SnapshotResponse(
            int urgeLogs,
            int betAttempts,
            int betBlockedCount,
            int recoveryActionCount,
            int relapseSignalCount,
            int eventsCount,
            int streakDays,
            int daysSinceLastEvent,
            int recentEventCount3d
    ) {

        public static SnapshotResponse from(RecoverySnapshot snapshot) {
            if (snapshot == null) {
                return null;
            }

            RecoveryState state = snapshot.state();

            return new SnapshotResponse(
                    state == null ? 0 : state.urgeLogs(),
                    state == null ? 0 : state.betAttempts(),
                    state == null ? 0 : state.betBlockedCount(),
                    state == null ? 0 : state.recoveryActionCount(),
                    state == null ? 0 : state.relapseSignalCount(),
                    state == null ? 0 : state.eventsCount(),
                    snapshot.streakDays(),
                    snapshot.daysSinceLastEvent(),
                    snapshot.recentEventCount3d()
            );
        }
    }

    public record BrainResponse(
            String status,
            String failureCode,
            String patternType,
            double score,
            String reason,
            double topScore,
            double secondScore,
            double gap,
            String gapClass,
            String nextActionType,
            String actionGuide,
            String actionIntensity,
            String actionTarget,
            String domainActionType,
            String domainActionGuide,
            String recommendedAction,
            double recommendationConfidence,
            String recommendationSource
    ) {

        public static BrainResponse from(
                BrainRecoveryDryRunResult result
        ) {
            if (result == null) {
                return new BrainResponse(
                        "NOT_REQUESTED",
                        null,
                        null,
                        0.0,
                        null,
                        0.0,
                        0.0,
                        0.0,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0.0,
                        null
                );
            }

            BrainRecoveryDryRunResponse response = result.response();

            if (!result.available() || response == null) {
                return new BrainResponse(
                        result.status().name(),
                        result.failureCode(),
                        null,
                        0.0,
                        null,
                        0.0,
                        0.0,
                        0.0,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0.0,
                        null
                );
            }

            return new BrainResponse(
                    result.status().name(),
                    null,
                    response.patternType(),
                    response.score(),
                    response.reason(),
                    response.topScore(),
                    response.secondScore(),
                    response.gap(),
                    response.gapClass(),
                    response.nextActionType(),
                    response.actionGuide(),
                    response.actionIntensity(),
                    response.actionTarget(),
                    response.domainActionType(),
                    response.domainActionGuide(),
                    response.recommendedAction(),
                    response.recommendationConfidence(),
                    response.recommendationSource()
            );
        }
    }
}
