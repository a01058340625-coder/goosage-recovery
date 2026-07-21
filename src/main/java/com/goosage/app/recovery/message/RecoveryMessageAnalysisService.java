package com.goosage.app.recovery.message;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoverySnapshotService;

@Service
public class RecoveryMessageAnalysisService {

    private final RuleBasedRecoveryMessageAnalyzer analyzer;
    private final RecoverySnapshotService snapshotService;
    private final RecoverySnapshotProjector snapshotProjector;

    public RecoveryMessageAnalysisService(
            RuleBasedRecoveryMessageAnalyzer analyzer,
            RecoverySnapshotService snapshotService,
            RecoverySnapshotProjector snapshotProjector
    ) {
        this.analyzer = analyzer;
        this.snapshotService = snapshotService;
        this.snapshotProjector = snapshotProjector;
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
                    null
            );
        }

        RecoverySnapshot baseSnapshot =
                snapshotService.snapshot(userId, nowDate, nowDateTime);

        RecoverySnapshot projectedSnapshot =
                snapshotProjector.project(
                        baseSnapshot,
                        analysis.signal()
                );

        return new RecoveryMessageProjection(
                analysis,
                baseSnapshot,
                projectedSnapshot
        );
    }
}