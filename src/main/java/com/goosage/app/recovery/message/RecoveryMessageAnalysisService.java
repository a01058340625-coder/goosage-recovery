package com.goosage.app.recovery.message;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.goosage.app.recovery.message.brain.BrainRecoveryDryRunClient;
import com.goosage.app.recovery.message.brain.BrainRecoveryDryRunResult;
import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoverySnapshotService;

@Service
public class RecoveryMessageAnalysisService {

    private final RuleBasedRecoveryMessageAnalyzer analyzer;
    private final RecoverySnapshotService snapshotService;
    private final RecoverySnapshotProjector snapshotProjector;
    private final BrainRecoveryDryRunClient brainDryRunClient;

    public RecoveryMessageAnalysisService(
            RuleBasedRecoveryMessageAnalyzer analyzer,
            RecoverySnapshotService snapshotService,
            RecoverySnapshotProjector snapshotProjector,
            BrainRecoveryDryRunClient brainDryRunClient
    ) {
        this.analyzer = analyzer;
        this.snapshotService = snapshotService;
        this.snapshotProjector = snapshotProjector;
        this.brainDryRunClient = brainDryRunClient;
    }

    public RecoveryMessageProjection analyze(
            long userId,
            String message,
            LocalDate nowDate,
            LocalDateTime nowDateTime
    ) {
        if (userId <= 0) {
            throw new IllegalArgumentException("valid userId is required");
        }

        if (nowDate == null) {
            throw new IllegalArgumentException("nowDate is required");
        }

        if (nowDateTime == null) {
            throw new IllegalArgumentException("nowDateTime is required");
        }

        RecoveryMessageAnalysis analysis = analyzer.analyze(message);

        if (!analysis.analyzable()) {
            return new RecoveryMessageProjection(
                    analysis,
                    null,
                    null,
                    BrainRecoveryDryRunResult.notRequested()
            );
        }

        RecoverySnapshot baseSnapshot =
                snapshotService.snapshot(userId, nowDate, nowDateTime);

        RecoverySnapshot projectedSnapshot =
                snapshotProjector.project(
                        baseSnapshot,
                        analysis.signal()
                );

        BrainRecoveryDryRunResult brainResult =
                brainDryRunClient.analyze(
                        userId,
                        projectedSnapshot
                );

        return new RecoveryMessageProjection(
                analysis,
                baseSnapshot,
                projectedSnapshot,
                brainResult
        );
    }
}