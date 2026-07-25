package com.goosage.app.recovery.message.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationCommand;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationItem;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationQuery;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationPort;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationResult;

class RecoveryMessageValidationServiceTest {

    private RecoveryMessageValidationPort validationPort;
    private RecoveryMessageValidationService service;

    @BeforeEach
    void setUp() {
        validationPort = Mockito.mock(
                RecoveryMessageValidationPort.class
        );

        service = new RecoveryMessageValidationService(
                validationPort
        );
    }

    @Test
    void savesNormalizedMismatchValidation() {
        RecoveryMessageValidationCommand command =
                command(
                        " mismatch ",
                        " signal "
                );

        RecoveryMessageValidationResult saved =
                new RecoveryMessageValidationResult(
                        10L,
                        22L,
                        "MISMATCH",
                        true,
                        false,
                        LocalDateTime.of(
                                2026,
                                7,
                                25,
                                9,
                                0
                        )
                );

        when(validationPort.save(any()))
                .thenReturn(saved);

        RecoveryMessageValidationResult result =
                service.save(command);

        ArgumentCaptor<RecoveryMessageValidationCommand> captor =
                ArgumentCaptor.forClass(
                        RecoveryMessageValidationCommand.class
                );

        verify(validationPort).save(captor.capture());

        RecoveryMessageValidationCommand normalized =
                captor.getValue();

        assertThat(normalized.validationResult())
                .isEqualTo("MISMATCH");
        assertThat(normalized.mismatchType())
                .isEqualTo("SIGNAL");
        assertThat(normalized.originalMessage())
                .isEqualTo("충동이 왔지만 앱을 닫았어");
        assertThat(normalized.reviewMemo())
                .isEqualTo("검토 필요");

        assertThat(result).isEqualTo(saved);
    }

    @Test
    void rejectsMatchWithMismatchType() {
        RecoveryMessageValidationCommand command =
                command(
                        "MATCH",
                        "SIGNAL"
                );

        assertThatThrownBy(
                () -> service.save(command)
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "MATCH must not have mismatchType"
                );
    }

    @Test
    void rejectsMismatchWithoutMismatchType() {
        RecoveryMessageValidationCommand command =
                command(
                        "MISMATCH",
                        null
                );

        assertThatThrownBy(
                () -> service.save(command)
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "MISMATCH requires mismatchType"
                );
    }

    @Test
    void acceptsNeedsReviewWithoutMismatchType() {
        RecoveryMessageValidationCommand command =
                command(
                        "needs_review",
                        null
                );

        RecoveryMessageValidationResult saved =
                new RecoveryMessageValidationResult(
                        11L,
                        22L,
                        "NEEDS_REVIEW",
                        false,
                        true,
                        LocalDateTime.of(
                                2026,
                                7,
                                25,
                                9,
                                5
                        )
                );

        when(validationPort.save(any()))
                .thenReturn(saved);

        RecoveryMessageValidationResult result =
                service.save(command);

        assertThat(result.validationResult())
                .isEqualTo("NEEDS_REVIEW");

        verify(validationPort).save(any());
    }

    @Test
    void rejectsUnsupportedValidationResult() {
        RecoveryMessageValidationCommand command =
                command(
                        "UNKNOWN",
                        null
                );

        assertThatThrownBy(
                () -> service.save(command)
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "unsupported validationResult"
                );
    }

    @Test
    void rejectsUnsupportedMismatchType() {
        RecoveryMessageValidationCommand command =
                command(
                        "MISMATCH",
                        "ENGINE"
                );

        assertThatThrownBy(
                () -> service.save(command)
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "unsupported mismatchType"
                );
    }

    @Test
    void findsRecentValidationsWithNormalizedQuery() {
        RecoveryMessageValidationQuery query =
                new RecoveryMessageValidationQuery(
                        22L,
                        " mismatch ",
                        true,
                        false,
                        20
                );

        List<RecoveryMessageValidationItem> saved =
                List.of();

        when(validationPort.findRecent(any()))
                .thenReturn(saved);

        List<RecoveryMessageValidationItem> result =
                service.findRecent(query);

        ArgumentCaptor<RecoveryMessageValidationQuery> captor =
                ArgumentCaptor.forClass(
                        RecoveryMessageValidationQuery.class
                );

        verify(validationPort).findRecent(
                captor.capture()
        );

        RecoveryMessageValidationQuery normalized =
                captor.getValue();

        assertThat(normalized.userId())
                .isEqualTo(22L);
        assertThat(normalized.validationResult())
                .isEqualTo("MISMATCH");
        assertThat(normalized.realScenarioCandidate())
                .isTrue();
        assertThat(normalized.virtualUserCandidate())
                .isFalse();
        assertThat(normalized.limit())
                .isEqualTo(20);
        assertThat(result)
                .isSameAs(saved);
    }

    @Test
    void rejectsUnsupportedValidationResultForQuery() {
        RecoveryMessageValidationQuery query =
                new RecoveryMessageValidationQuery(
                        22L,
                        "UNKNOWN",
                        null,
                        null,
                        20
                );

        assertThatThrownBy(
                () -> service.findRecent(query)
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "unsupported validationResult"
                );
    }

    @Test
    void rejectsQueryLimitOutsideAllowedRange() {
        RecoveryMessageValidationQuery query =
                new RecoveryMessageValidationQuery(
                        22L,
                        null,
                        null,
                        null,
                        101
                );

        assertThatThrownBy(
                () -> service.findRecent(query)
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "limit must be between 1 and 100"
                );
    }

    private RecoveryMessageValidationCommand command(
            String validationResult,
            String mismatchType
    ) {
        return new RecoveryMessageValidationCommand(
                22L,
                " 충동이 왔지만 앱을 닫았어 ",

                true,
                null,
                "{\"urgeLogDelta\":1}",
                "RECOVERY_DRIVEN",
                "REINFORCE_RECOVERY",

                true,
                null,
                "{\"urgeLogDelta\":1}",
                "RECOVERY_DRIVEN",
                "REINFORCE_RECOVERY",
                "RECOVERY_DO_RECOVERY_ACTION",

                validationResult,
                mismatchType,
                " 검토 필요 ",

                true,
                false
        );
    }
}