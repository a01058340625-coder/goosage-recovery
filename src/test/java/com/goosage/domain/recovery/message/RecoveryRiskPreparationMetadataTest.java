package com.goosage.domain.recovery.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RecoveryRiskPreparationMetadataTest {

    @Test
    void creates_none_metadata() {
        RecoveryRiskPreparationMetadata metadata =
                RecoveryRiskPreparationMetadata.none();

        assertThat(metadata.detected()).isFalse();
        assertThat(metadata.type()).isNull();
        assertThat(metadata.confidence()).isZero();
        assertThat(metadata.reason()).isNull();
    }

    @Test
    void creates_detected_metadata_for_each_allowed_type() {
        for (String type : new String[] {
                "FUNDING_COMPLETED_BET_NEGATED",
                "FUNDING_COMPLETED_FUTURE_INTENT_PRESENT",
                "FUNDING_STARTED_THEN_CANCELLED",
                "FUNDING_INTERRUPTED_BY_EXTERNAL_INTERVENTION_WITH_RETRY_INTENT",
                "PROTECTIVE_BLOCK_REVERSAL_POSSIBILITY_PRESENT",
                "PROTECTIVE_BLOCK_REVERSAL_PREPARATION_PRESENT"
        }) {
            RecoveryRiskPreparationMetadata metadata =
                    RecoveryRiskPreparationMetadata.detected(
                            type,
                            0.8,
                            "supported pre-bet funding boundary"
                    );

            assertThat(metadata.detected()).isTrue();
            assertThat(metadata.type()).isEqualTo(type);
            assertThat(metadata.confidence()).isEqualTo(0.8);
            assertThat(metadata.reason())
                    .isEqualTo(
                            "supported pre-bet funding boundary"
                    );
        }
    }

    @Test
    void rejects_fields_when_not_detected() {
        assertThatThrownBy(
                () -> new RecoveryRiskPreparationMetadata(
                        false,
                        "FUNDING_COMPLETED_BET_NEGATED",
                        0.0,
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "type must be null when not detected"
                );

        assertThatThrownBy(
                () -> new RecoveryRiskPreparationMetadata(
                        false,
                        null,
                        0.1,
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "confidence must be 0 when not detected"
                );

        assertThatThrownBy(
                () -> new RecoveryRiskPreparationMetadata(
                        false,
                        null,
                        0.0,
                        "reason"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "reason must be null when not detected"
                );
    }

    @Test
    void rejects_invalid_detected_metadata() {
        assertThatThrownBy(
                () -> RecoveryRiskPreparationMetadata.detected(
                        "UNKNOWN_TYPE",
                        0.8,
                        "reason"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "unsupported risk preparation type"
                );

        assertThatThrownBy(
                () -> RecoveryRiskPreparationMetadata.detected(
                        "FUNDING_COMPLETED_BET_NEGATED",
                        0.0,
                        "reason"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "confidence must be greater than 0 and at most 1"
                );

        assertThatThrownBy(
                () -> RecoveryRiskPreparationMetadata.detected(
                        "FUNDING_COMPLETED_BET_NEGATED",
                        1.1,
                        "reason"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "confidence must be greater than 0 and at most 1"
                );

        assertThatThrownBy(
                () -> RecoveryRiskPreparationMetadata.detected(
                        "FUNDING_COMPLETED_BET_NEGATED",
                        0.8,
                        " "
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "reason is required when detected"
                );
    }
}
