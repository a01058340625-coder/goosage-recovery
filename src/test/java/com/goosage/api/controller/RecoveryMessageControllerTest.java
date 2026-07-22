package com.goosage.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.goosage.app.recovery.message.RecoveryMessageAnalysis;
import com.goosage.app.recovery.message.RecoveryMessageAnalysisService;
import com.goosage.app.recovery.message.RecoveryMessageProjection;
import com.goosage.app.recovery.message.brain.BrainRecoveryDryRunResponse;
import com.goosage.app.recovery.message.brain.BrainRecoveryDryRunResult;
import com.goosage.auth.SessionConst;
import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoveryState;
import com.goosage.domain.recovery.message.RecoveryMessageSignal;

@ExtendWith(MockitoExtension.class)
class RecoveryMessageControllerTest {

    @Mock
    private RecoveryMessageAnalysisService analysisService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RecoveryMessageController controller =
                new RecoveryMessageController(analysisService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void rejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(
                        post("/recovery/message/analyze")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "message": "\ucda9\ub3d9\uc774 \uc654\uc9c0\ub9cc \uc0ac\uc774\ud2b8\ub97c \ub2eb\uace0 \uc0b0\ucc45\ud588\uc5b4"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("\ub85c\uadf8\uc778\uc774 \ud544\uc694\ud569\ub2c8\ub2e4"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(analysisService, never()).analyze(
                anyLong(),
                any(),
                any(LocalDate.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    void returnsAvailableBrainContractForAuthenticatedUser()
            throws Exception {

        long userId = 22L;
        String message = "\ucda9\ub3d9\uc774 \uc654\uc9c0\ub9cc \uc0ac\uc774\ud2b8\ub97c \ub2eb\uace0 \uc0b0\ucc45\ud588\uc5b4";

        RecoveryMessageSignal signal = new RecoveryMessageSignal(
                1,
                0,
                1,
                1,
                0,
                0.90,
                "urge=1, attempt=0, blocked=1, recovery=1, relapse=0"
        );

        RecoveryMessageAnalysis analysis = new RecoveryMessageAnalysis(
                message,
                true,
                signal,
                null
        );

        RecoverySnapshot baseSnapshot = snapshot(
                1, 0, 0, 2, 0, 3, 5
        );

        RecoverySnapshot projectedSnapshot = snapshot(
                2, 0, 1, 3, 0, 6, 8
        );

        BrainRecoveryDryRunResponse brainResponse =
                new BrainRecoveryDryRunResponse(
                        "RECOVERY_DRIVEN",
                        0.8,
                        "recovery signal",
                        0.8,
                        0.2,
                        0.6,
                        "CLEAR",
                        "REINFORCE_RECOVERY",
                        "guide",
                        "MID",
                        "RECOVERY",
                        "RECOVERY_DO_RECOVERY_ACTION",
                        "do recovery action",
                        "RECOVERY_DO_RECOVERY_ACTION",
                        1.0,
                        "FIXED_RULE"
                );

        RecoveryMessageProjection projection =
                new RecoveryMessageProjection(
                        analysis,
                        baseSnapshot,
                        projectedSnapshot,
                        BrainRecoveryDryRunResult.available(
                                brainResponse
                        )
                );

        when(analysisService.analyze(
                eq(userId),
                eq(message),
                any(LocalDate.class),
                any(LocalDateTime.class)
        )).thenReturn(projection);

        mockMvc.perform(
                        post("/recovery/message/analyze")
                                .sessionAttr(
                                        SessionConst.LOGIN_USER_ID,
                                        userId
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "message": "\ucda9\ub3d9\uc774 \uc654\uc9c0\ub9cc \uc0ac\uc774\ud2b8\ub97c \ub2eb\uace0 \uc0b0\ucc45\ud588\uc5b4"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.analyzable").value(true))
                .andExpect(jsonPath("$.data.holdReason").isEmpty())
                .andExpect(jsonPath("$.data.originalMessage")
                        .value(message))
                .andExpect(jsonPath("$.data.signal.urgeLogDelta")
                        .value(1))
                .andExpect(jsonPath("$.data.signal.betAttemptDelta")
                        .value(0))
                .andExpect(jsonPath("$.data.signal.betBlockedDelta")
                        .value(1))
                .andExpect(jsonPath("$.data.signal.recoveryActionDelta")
                        .value(1))
                .andExpect(jsonPath("$.data.signal.relapseSignalDelta")
                        .value(0))
                .andExpect(jsonPath("$.data.baseSnapshot.eventsCount")
                        .value(3))
                .andExpect(jsonPath(
                        "$.data.projectedSnapshot.eventsCount"
                ).value(6))
                .andExpect(jsonPath(
                        "$.data.projectedSnapshot.recentEventCount3d"
                ).value(8))
                .andExpect(jsonPath("$.data.brain.status")
                        .value("AVAILABLE"))
                .andExpect(jsonPath("$.data.brain.failureCode")
                        .isEmpty())
                .andExpect(jsonPath("$.data.brain.patternType")
                        .value("RECOVERY_DRIVEN"))
                .andExpect(jsonPath("$.data.brain.gapClass")
                        .value("CLEAR"))
                .andExpect(jsonPath("$.data.brain.nextActionType")
                        .value("REINFORCE_RECOVERY"))
                .andExpect(jsonPath("$.data.brain.actionIntensity")
                        .value("MID"))
                .andExpect(jsonPath("$.data.brain.actionTarget")
                        .value("RECOVERY"))
                .andExpect(jsonPath("$.data.brain.domainActionType")
                        .value("RECOVERY_DO_RECOVERY_ACTION"))
                .andExpect(jsonPath("$.data.brain.recommendedAction")
                        .value("RECOVERY_DO_RECOVERY_ACTION"))
                .andExpect(jsonPath(
                        "$.data.brain.recommendationConfidence"
                ).value(1.0))
                .andExpect(jsonPath("$.data.brain.recommendationSource")
                        .value("FIXED_RULE"));

        verify(analysisService).analyze(
                eq(userId),
                eq(message),
                any(LocalDate.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    void returnsNotRequestedBrainContractForHeldMessage()
            throws Exception {

        long userId = 22L;
        String message = "\ucda9\ub3d9\uc740 \uc5c6\uc5c8\uace0 \uc548\uc815\uc801\uc774\uc5c8\uc5b4";

        RecoveryMessageAnalysis analysis =
                new RecoveryMessageAnalysis(
                        message,
                        false,
                        null,
                        "NO_SUPPORTED_SIGNAL"
                );

        RecoveryMessageProjection projection =
                new RecoveryMessageProjection(
                        analysis,
                        null,
                        null
                );

        when(analysisService.analyze(
                eq(userId),
                eq(message),
                any(LocalDate.class),
                any(LocalDateTime.class)
        )).thenReturn(projection);

        mockMvc.perform(
                        post("/recovery/message/analyze")
                                .sessionAttr(
                                        SessionConst.LOGIN_USER_ID,
                                        userId
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "message": "\ucda9\ub3d9\uc740 \uc5c6\uc5c8\uace0 \uc548\uc815\uc801\uc774\uc5c8\uc5b4"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.analyzable")
                        .value(false))
                .andExpect(jsonPath("$.data.holdReason")
                        .value("NO_SUPPORTED_SIGNAL"))
                .andExpect(jsonPath("$.data.originalMessage")
                        .value(message))
                .andExpect(jsonPath("$.data.signal")
                        .doesNotExist())
                .andExpect(jsonPath("$.data.baseSnapshot")
                        .doesNotExist())
                .andExpect(jsonPath("$.data.projectedSnapshot")
                        .doesNotExist())
                .andExpect(jsonPath("$.data.brain.status")
                        .value("NOT_REQUESTED"))
                .andExpect(jsonPath("$.data.brain.failureCode")
                        .isEmpty())
                .andExpect(jsonPath("$.data.brain.patternType")
                        .isEmpty())
                .andExpect(jsonPath("$.data.brain.recommendedAction")
                        .isEmpty());
    }

    @Test
    void returnsUnavailableBrainContractWithoutFailingRecovery()
            throws Exception {

        long userId = 22L;
        String message = "\ucda9\ub3d9\uc774 \uc654\uc9c0\ub9cc \uc0ac\uc774\ud2b8\ub97c \ub2eb\uace0 \uc0b0\ucc45\ud588\uc5b4";

        RecoveryMessageAnalysis analysis =
                new RecoveryMessageAnalysis(
                        message,
                        true,
                        new RecoveryMessageSignal(
                                1,
                                0,
                                1,
                                1,
                                0,
                                0.9,
                                "recovery signal"
                        ),
                        null
                );

        RecoveryMessageProjection projection =
                new RecoveryMessageProjection(
                        analysis,
                        snapshot(0, 0, 0, 0, 0, 0, 0),
                        snapshot(1, 0, 1, 1, 0, 3, 3),
                        BrainRecoveryDryRunResult.unavailable(
                                "BRAIN_UNAVAILABLE"
                        )
                );

        when(analysisService.analyze(
                eq(userId),
                eq(message),
                any(LocalDate.class),
                any(LocalDateTime.class)
        )).thenReturn(projection);

        mockMvc.perform(
                        post("/recovery/message/analyze")
                                .sessionAttr(
                                        SessionConst.LOGIN_USER_ID,
                                        userId
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "message": "\ucda9\ub3d9\uc774 \uc654\uc9c0\ub9cc \uc0ac\uc774\ud2b8\ub97c \ub2eb\uace0 \uc0b0\ucc45\ud588\uc5b4"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.analyzable")
                        .value(true))
                .andExpect(jsonPath("$.data.baseSnapshot")
                        .exists())
                .andExpect(jsonPath("$.data.projectedSnapshot")
                        .exists())
                .andExpect(jsonPath("$.data.brain.status")
                        .value("UNAVAILABLE"))
                .andExpect(jsonPath("$.data.brain.failureCode")
                        .value("BRAIN_UNAVAILABLE"))
                .andExpect(jsonPath("$.data.brain.patternType")
                        .isEmpty())
                .andExpect(jsonPath("$.data.brain.recommendedAction")
                        .isEmpty());
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
