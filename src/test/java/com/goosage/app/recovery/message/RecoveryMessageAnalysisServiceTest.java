package com.goosage.app.recovery.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoverySnapshotService;
import com.goosage.domain.recovery.RecoveryState;

class RecoveryMessageAnalysisServiceTest {

    private RuleBasedRecoveryMessageAnalyzer analyzer;
    private RecoverySnapshotService snapshotService;
    private RecoverySnapshotProjector snapshotProjector;
    private RecoveryMessageAnalysisService service;

    @BeforeEach
    void setUp() {
        analyzer = new RuleBasedRecoveryMessageAnalyzer();
        snapshotService = Mockito.mock(RecoverySnapshotService.class);
        snapshotProjector = new RecoverySnapshotProjector();

        service = new RecoveryMessageAnalysisService(
                analyzer,
                snapshotService,
                snapshotProjector
        );
    }

    @Test
    void createsProjectedSnapshotWhenMessageIsAnalyzable() {
        long userId = 22L;
        LocalDate nowDate = LocalDate.of(2026, 7, 21);
        LocalDateTime nowDateTime =
                LocalDateTime.of(2026, 7, 21, 10, 10);

        RecoverySnapshot baseSnapshot = new RecoverySnapshot(
                nowDate,
                new RecoveryState(
                        1,
                        0,
                        0,
                        2,
                        0,
                        3
                ),
                true,
                4,
                LocalDateTime.of(2026, 7, 21, 8, 0),
                0,
                5,
                null
        );

        when(snapshotService.snapshot(
                userId,
                nowDate,
                nowDateTime
        )).thenReturn(baseSnapshot);

        RecoveryMessageProjection result = service.analyze(
                userId,
                "충동이 왔지만 사이트를 닫고 산책했어",
                nowDate,
                nowDateTime
        );

        assertThat(result.analysis().analyzable()).isTrue();
        assertThat(result.baseSnapshot()).isSameAs(baseSnapshot);
        assertThat(result.projected()).isTrue();
        assertThat(result.projectedSnapshot()).isNotNull();

        assertThat(result.projectedSnapshot().state().urgeLogs())
                .isEqualTo(2);
        assertThat(result.projectedSnapshot().state().betBlockedCount())
                .isEqualTo(1);
        assertThat(result.projectedSnapshot().state().recoveryActionCount())
                .isEqualTo(3);
        assertThat(result.projectedSnapshot().state().eventsCount())
                .isEqualTo(6);
        assertThat(result.projectedSnapshot().recentEventCount3d())
                .isEqualTo(8);

        verify(snapshotService).snapshot(
                userId,
                nowDate,
                nowDateTime
        );
    }

    @Test
    void doesNotLoadSnapshotWhenMessageIsHeld() {
        long userId = 22L;
        LocalDate nowDate = LocalDate.of(2026, 7, 21);
        LocalDateTime nowDateTime =
                LocalDateTime.of(2026, 7, 21, 10, 10);

        RecoveryMessageProjection result = service.analyze(
                userId,
                "친구가 다시 베팅했다고 하더라",
                nowDate,
                nowDateTime
        );

        assertThat(result.analysis().analyzable()).isFalse();
        assertThat(result.analysis().holdReason())
                .isEqualTo("THIRD_PARTY_CONTEXT");
        assertThat(result.baseSnapshot()).isNull();
        assertThat(result.projectedSnapshot()).isNull();
        assertThat(result.projected()).isFalse();

        verify(snapshotService, never()).snapshot(
                userId,
                nowDate,
                nowDateTime
        );
    }
}