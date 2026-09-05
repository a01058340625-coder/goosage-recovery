package com.goosage.app.recovery.message;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goosage.app.recovery.message.brain.BrainRecoveryDryRunClient;
import com.goosage.app.recovery.message.brain.BrainRecoveryDryRunResult;
import com.goosage.app.recovery.message.structuredshadow.ProductionShadowDualRunObserver;
import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoverySnapshotService;

@Service
public class RecoveryMessageAnalysisService {

    private final ProductionRecoveryMessageAnalyzer analyzer;
    private final RecoverySnapshotService snapshotService;
    private final RecoverySnapshotProjector snapshotProjector;
    private final BrainRecoveryDryRunClient brainDryRunClient;
    private final ProductionShadowDualRunObserver productionShadowDualRunObserver;

    public RecoveryMessageAnalysisService(
            RuleBasedRecoveryMessageAnalyzer analyzer,
            RecoverySnapshotService snapshotService,
            RecoverySnapshotProjector snapshotProjector,
            BrainRecoveryDryRunClient brainDryRunClient
    ) {
        this(
                new ProductionRecoveryMessageAnalyzer(
                        analyzer,
                        "RULE_BASED"
                ),
                snapshotService,
                snapshotProjector,
                brainDryRunClient,
                null
        );
    }


    @Autowired
    public RecoveryMessageAnalysisService(
            ProductionRecoveryMessageAnalyzer analyzer,
            RecoverySnapshotService snapshotService,
            RecoverySnapshotProjector snapshotProjector,
            BrainRecoveryDryRunClient brainDryRunClient,
            ProductionShadowDualRunObserver productionShadowDualRunObserver
    ) {
        this.analyzer = analyzer;
        this.snapshotService = snapshotService;
        this.snapshotProjector = snapshotProjector;
        this.brainDryRunClient = brainDryRunClient;
        this.productionShadowDualRunObserver = productionShadowDualRunObserver;
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

        if (productionShadowDualRunObserver != null) {
            productionShadowDualRunObserver.observe(
                    message,
                    analysis
            );
        }

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