package com.goosage.app.recovery.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoveryState;
import com.goosage.domain.recovery.message.RecoveryMessageSignal;

class RecoverySnapshotProjectorTest {

    private final RecoverySnapshotProjector projector =
            new RecoverySnapshotProjector();

    @Test
    void projectsMessageSignalWithoutChangingBaseSnapshot() {
        RecoveryState baseState = new RecoveryState(
                2,
                1,
                1,
                3,
                0,
                7
        );

        RecoverySnapshot base = new RecoverySnapshot(
                LocalDate.of(2026, 7, 21),
                baseState,
                true,
                4,
                LocalDateTime.of(2026, 7, 21, 8, 30),
                0,
                10,
                null
        );

        RecoveryMessageSignal signal = new RecoveryMessageSignal(
                1,
                0,
                1,
                1,
                0,
                0.90,
                "위험 신호와 보호·회복 행동이 함께 존재"
        );

        RecoverySnapshot projected = projector.project(base, signal);

        assertThat(projected.state().urgeLogs()).isEqualTo(3);
        assertThat(projected.state().betAttempts()).isEqualTo(1);
        assertThat(projected.state().betBlockedCount()).isEqualTo(2);
        assertThat(projected.state().recoveryActionCount()).isEqualTo(4);
        assertThat(projected.state().relapseSignalCount()).isZero();
        assertThat(projected.state().eventsCount()).isEqualTo(10);

        assertThat(projected.recentEventCount3d()).isEqualTo(13);
        assertThat(projected.streakDays()).isEqualTo(4);
        assertThat(projected.daysSinceLastEvent()).isZero();

        assertThat(base.state().urgeLogs()).isEqualTo(2);
        assertThat(base.state().betBlockedCount()).isEqualTo(1);
        assertThat(base.state().recoveryActionCount()).isEqualTo(3);
        assertThat(base.state().eventsCount()).isEqualTo(7);
        assertThat(base.recentEventCount3d()).isEqualTo(10);
    }

    @Test
    void treatsNegativeDeltasAsZero() {
        RecoveryState baseState = new RecoveryState(
                1,
                1,
                1,
                1,
                1,
                5
        );

        RecoverySnapshot base = new RecoverySnapshot(
                LocalDate.of(2026, 7, 21),
                baseState,
                true,
                2,
                LocalDateTime.of(2026, 7, 20, 22, 0),
                0,
                5,
                null
        );

        RecoveryMessageSignal signal = new RecoveryMessageSignal(
                -1,
                -2,
                -3,
                -4,
                -5,
                0.20,
                "무효 음수 입력"
        );

        RecoverySnapshot projected = projector.project(base, signal);

        assertThat(projected.state().urgeLogs()).isEqualTo(1);
        assertThat(projected.state().betAttempts()).isEqualTo(1);
        assertThat(projected.state().betBlockedCount()).isEqualTo(1);
        assertThat(projected.state().recoveryActionCount()).isEqualTo(1);
        assertThat(projected.state().relapseSignalCount()).isEqualTo(1);
        assertThat(projected.state().eventsCount()).isEqualTo(5);
        assertThat(projected.recentEventCount3d()).isEqualTo(5);
    }
}