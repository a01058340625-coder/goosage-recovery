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
                                          "message": "충동이 왔지만 사이트를 닫고 산책했어"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));

        verify(analysisService, never()).analyze(
                anyLong(),
                any(),
                any(LocalDate.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    void returnsProjectedAnalysisForAuthenticatedUser() throws Exception {
        long userId = 22L;
        String message = "충동이 왔지만 사이트를 닫고 산책했어";

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

        RecoveryMessageProjection projection =
                new RecoveryMessageProjection(
                        analysis,
                        baseSnapshot,
                        projectedSnapshot
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
                                          "message": "충동이 왔지만 사이트를 닫고 산책했어"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.analyzable").value(true))
                .andExpect(jsonPath("$.data.holdReason").isEmpty())
                .andExpect(jsonPath("$.data.signal.urgeLogDelta").value(1))
                .andExpect(jsonPath("$.data.signal.betBlockedDelta").value(1))
                .andExpect(jsonPath("$.data.signal.recoveryActionDelta").value(1))
                .andExpect(jsonPath("$.data.baseSnapshot.eventsCount").value(3))
                .andExpect(jsonPath("$.data.projectedSnapshot.eventsCount").value(6))
                .andExpect(jsonPath("$.data.projectedSnapshot.recentEventCount3d").value(8));

        verify(analysisService).analyze(
                eq(userId),
                eq(message),
                any(LocalDate.class),
                any(LocalDateTime.class)
        );
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