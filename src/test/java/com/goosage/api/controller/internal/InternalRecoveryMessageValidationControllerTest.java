package com.goosage.api.controller.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goosage.api.view.recovery.message.validation.RecoveryMessageValidationListItemResponse;
import com.goosage.app.recovery.message.validation.RecoveryMessageValidationService;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationItem;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationQuery;
import com.goosage.support.web.ApiResponse;
import com.goosage.support.web.UnauthorizedException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class InternalRecoveryMessageValidationControllerTest {

    private RecoveryMessageValidationService validationService;
    private InternalRecoveryMessageValidationController controller;

    @BeforeEach
    void setUp() {
        validationService = Mockito.mock(
                RecoveryMessageValidationService.class
        );

        controller =
                new InternalRecoveryMessageValidationController(
                        validationService,
                        new ObjectMapper()
                );
    }

    @Test
    void findsValidationsWithValidInternalKey() {
        RecoveryMessageValidationItem item =
                new RecoveryMessageValidationItem(
                        4L,
                        1L,
                        "오늘 충동이 있었지만 앱을 끄고 가족에게 연락했어",

                        true,
                        null,
                        "{\"urgeLogDelta\":1}",
                        null,
                        null,

                        true,
                        "NO_SUPPORTED_SIGNAL",
                        "{\"urgeLogDelta\":1}",
                        null,
                        null,
                        null,

                        "MISMATCH",
                        "SIGNAL",
                        "MISMATCH 저장 검증",

                        false,
                        false,

                        LocalDateTime.of(
                                2026,
                                7,
                                25,
                                17,
                                32,
                                4
                        )
                );

        when(validationService.findRecent(any()))
                .thenReturn(List.of(item));

        ApiResponse<
                List<RecoveryMessageValidationListItemResponse>
        > response =
                controller.findRecent(
                        "goosage-dev",
                        1L,
                        " mismatch ",
                        null,
                        null,
                        100
                );

        ArgumentCaptor<RecoveryMessageValidationQuery> captor =
                ArgumentCaptor.forClass(
                        RecoveryMessageValidationQuery.class
                );

        verify(validationService).findRecent(
                captor.capture()
        );

        RecoveryMessageValidationQuery query =
                captor.getValue();

        assertThat(query.userId())
                .isEqualTo(1L);
        assertThat(query.validationResult())
                .isEqualTo(" mismatch ");
        assertThat(query.limit())
                .isEqualTo(100);

        assertThat(response.isSuccess())
                .isTrue();
        assertThat(response.getData())
                .hasSize(1);

        RecoveryMessageValidationListItemResponse data =
                response.getData().get(0);

        assertThat(data.id())
                .isEqualTo(4L);
        assertThat(data.validationResult())
                .isEqualTo("MISMATCH");
        assertThat(data.actualSignal())
                .isInstanceOf(Map.class);
        assertThat(
                ((Map<?, ?>) data.actualSignal())
                        .get("urgeLogDelta")
        )
                .isEqualTo(1);
    }

    @Test
    void rejectsInvalidInternalKey() {
        assertThatThrownBy(
                () -> controller.findRecent(
                        "wrong-key",
                        1L,
                        null,
                        null,
                        null,
                        100
                )
        )
                .isInstanceOf(
                        UnauthorizedException.class
                )
                .hasMessage("bad internal key");
    }

    @Test
    void rejectsMissingInternalKey() {
        assertThatThrownBy(
                () -> controller.findRecent(
                        null,
                        1L,
                        null,
                        null,
                        null,
                        100
                )
        )
                .isInstanceOf(
                        UnauthorizedException.class
                )
                .hasMessage("bad internal key");
    }
}