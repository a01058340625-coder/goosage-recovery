package com.goosage.api.view.recovery.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.goosage.app.recovery.message.RecoveryMessageAnalysis;
import com.goosage.app.recovery.message.RecoveryMessageProjection;
import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoveryState;
import com.goosage.domain.recovery.message.RecoveryMessageSignal;

class RecoveryMessageAnalyzeResponseTest {

    @Test
    void mapsAnalyzableProjectionToApiResponse() {
        RecoveryMessageSignal signal = new RecoveryMessageSignal(
                1,
                0,
                1,
                1,
                0,
                0.90,
                "mixed signal"
        );

        RecoveryMessageAnalysis analysis = new RecoveryMessageAnalysis(
                "충동이 왔지만 사이트를 닫고 산책했어",
                true,
                signal,
                null
        );

        RecoverySnapshot base = snapshot(
                1, 0, 0, 2, 0, 3, 5
        );

        RecoverySnapshot projected = snapshot(
                2, 0, 1, 3, 0, 6, 8
        );

        RecoveryMessageProjection projection =
                new RecoveryMessageProjection(
                        analysis,
                        base,
                        projected
                );

        RecoveryMessageAnalyzeResponse response =
                RecoveryMessageAnalyzeResponse.from(projection);

        assertThat(response.analyzable()).isTrue();
        assertThat(response.holdReason()).isNull();
        assertThat(response.originalMessage())
                .isEqualTo("충동이 왔지만 사이트를 닫고 산책했어");

        assertThat(response.signal()).isNotNull();
        assertThat(response.signal().urgeLogDelta()).isEqualTo(1);
        assertThat(response.signal().betBlockedDelta()).isEqualTo(1);
        assertThat(response.signal().recoveryActionDelta()).isEqualTo(1);

        assertThat(response.baseSnapshot()).isNotNull();
        assertThat(response.baseSnapshot().eventsCount()).isEqualTo(3);
        assertThat(response.baseSnapshot().recentEventCount3d())
                .isEqualTo(5);

        assertThat(response.projectedSnapshot()).isNotNull();
        assertThat(response.projectedSnapshot().eventsCount())
                .isEqualTo(6);
        assertThat(response.projectedSnapshot().recentEventCount3d())
                .isEqualTo(8);
    }

    @Test
    void mapsHeldProjectionWithoutSnapshots() {
        RecoveryMessageAnalysis analysis = new RecoveryMessageAnalysis(
                "친구가 다시 베팅했다고 하더라",
                false,
                null,
                "THIRD_PARTY_CONTEXT"
        );

        RecoveryMessageProjection projection =
                new RecoveryMessageProjection(
                        analysis,
                        null,
                        null
                );

        RecoveryMessageAnalyzeResponse response =
                RecoveryMessageAnalyzeResponse.from(projection);

        assertThat(response.analyzable()).isFalse();
        assertThat(response.holdReason())
                .isEqualTo("THIRD_PARTY_CONTEXT");
        assertThat(response.signal()).isNull();
        assertThat(response.baseSnapshot()).isNull();
        assertThat(response.projectedSnapshot()).isNull();
    }

    private RecoverySnapshot snapshot(
            int urgeLogs,
            int betAttempts,
            int betBlockedCount,
            int recoveryActionCount,
            int relapseSignalCount,
            int eventsCount,
            int recentEventCount3d
    ) {
        return new RecoverySnapshot(
                LocalDate.of(2026, 7, 21),
                new RecoveryState(
                        urgeLogs,
                        betAttempts,
                        betBlockedCount,
                        recoveryActionCount,
                        relapseSignalCount,
                        eventsCount
                ),
                eventsCount > 0,
                4,
                LocalDateTime.of(2026, 7, 21, 8, 0),
                0,
                recentEventCount3d,
                null
        );
    }
}