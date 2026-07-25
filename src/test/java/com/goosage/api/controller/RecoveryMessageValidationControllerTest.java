package com.goosage.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goosage.api.view.recovery.message.validation.RecoveryMessageValidationSaveRequest;
import com.goosage.api.view.recovery.message.validation.RecoveryMessageValidationSaveResponse;
import com.goosage.app.recovery.message.validation.RecoveryMessageValidationService;
import com.goosage.auth.SessionConst;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationCommand;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationResult;
import com.goosage.support.web.ApiResponse;

import org.springframework.mock.web.MockHttpSession;

class RecoveryMessageValidationControllerTest {

    private RecoveryMessageValidationService validationService;
    private RecoveryMessageValidationController controller;

    @BeforeEach
    void setUp() {
        validationService = Mockito.mock(
                RecoveryMessageValidationService.class
        );

        controller =
                new RecoveryMessageValidationController(
                        validationService,
                        new ObjectMapper()
                );
    }

    @Test
    void savesValidationWithSessionUserAndSerializedSignals() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                SessionConst.LOGIN_USER_ID,
                10035L
        );

        RecoveryMessageValidationSaveRequest request =
                new RecoveryMessageValidationSaveRequest(
                        "충동이 왔지만 앱을 닫고 산책했어",

                        true,
                        null,
                        Map.of(
                                "urgeLogDelta", 1,
                                "betBlockedDelta", 1,
                                "recoveryActionDelta", 1
                        ),
                        "RECOVERY_DRIVEN",
                        "REINFORCE_RECOVERY",

                        true,
                        null,
                        Map.of(
                                "urgeLogDelta", 1,
                                "betBlockedDelta", 1,
                                "recoveryActionDelta", 1
                        ),
                        "RECOVERY_DRIVEN",
                        "REINFORCE_RECOVERY",
                        "RECOVERY_DO_RECOVERY_ACTION",

                        "MATCH",
                        null,
                        "실제 UI 결과와 일치",

                        true,
                        false
                );

        RecoveryMessageValidationResult saved =
                new RecoveryMessageValidationResult(
                        77L,
                        10035L,
                        "MATCH",
                        true,
                        false,
                        LocalDateTime.of(
                                2026,
                                7,
                                25,
                                9,
                                10
                        )
                );

        when(validationService.save(any()))
                .thenReturn(saved);

        ApiResponse<RecoveryMessageValidationSaveResponse> response =
                controller.save(
                        request,
                        session
                );

        ArgumentCaptor<RecoveryMessageValidationCommand> captor =
                ArgumentCaptor.forClass(
                        RecoveryMessageValidationCommand.class
                );

        verify(validationService).save(
                captor.capture()
        );

        RecoveryMessageValidationCommand command =
                captor.getValue();

        assertThat(command.userId())
                .isEqualTo(10035L);
        assertThat(command.originalMessage())
                .isEqualTo(
                        "충동이 왔지만 앱을 닫고 산책했어"
                );
        assertThat(command.expectedSignalJson())
                .contains(
                        "\"urgeLogDelta\":1"
                );
        assertThat(command.actualSignalJson())
                .contains(
                        "\"recoveryActionDelta\":1"
                );
        assertThat(command.validationResult())
                .isEqualTo("MATCH");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().id())
                .isEqualTo(77L);
        assertThat(response.getData().userId())
                .isEqualTo(10035L);
    }

    @Test
    void returnsFailureWhenRequestBodyIsMissing() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                SessionConst.LOGIN_USER_ID,
                10035L
        );

        ApiResponse<RecoveryMessageValidationSaveResponse> response =
                controller.save(
                        null,
                        session
                );

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage())
                .isEqualTo(
                        "VALIDATION_REQUEST_REQUIRED"
                );
    }

    @Test
    void rejectsUnauthenticatedSave() {
        MockHttpSession session = new MockHttpSession();

        RecoveryMessageValidationSaveRequest request =
                new RecoveryMessageValidationSaveRequest(
                        "충동이 왔어",

                        true,
                        null,
                        null,
                        null,
                        null,

                        true,
                        null,
                        null,
                        null,
                        null,
                        null,

                        "NEEDS_REVIEW",
                        null,
                        null,

                        false,
                        false
                );

        assertThatThrownBy(
                () -> controller.save(
                        request,
                        session
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage("UNAUTHORIZED");
    }
}