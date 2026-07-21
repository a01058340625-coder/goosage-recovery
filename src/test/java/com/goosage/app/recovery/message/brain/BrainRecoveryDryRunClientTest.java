package com.goosage.app.recovery.message.brain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoveryState;

class BrainRecoveryDryRunClientTest {

    private RestTemplate restTemplate;
    private BrainRecoveryDryRunClient client;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        client = new BrainRecoveryDryRunClient(restTemplate);

        ReflectionTestUtils.setField(
                client,
                "brainBaseUrl",
                "http://localhost:8086"
        );
    }

    @Test
    void returnsAvailableWhenBrainRespondsSuccessfully() {
        RecoverySnapshot snapshot = snapshot();

        BrainRecoveryDryRunResponse response =
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

        when(restTemplate.postForEntity(
                "http://localhost:8086/brain/recovery/dry-run",
                BrainRecoveryDryRunRequest.from(22L, snapshot),
                BrainRecoveryDryRunResponse.class
        )).thenReturn(ResponseEntity.ok(response));

        BrainRecoveryDryRunResult result =
                client.analyze(22L, snapshot);

        assertThat(result.status())
                .isEqualTo(BrainRecoveryDryRunStatus.AVAILABLE);
        assertThat(result.available()).isTrue();
        assertThat(result.response()).isSameAs(response);
        assertThat(result.failureCode()).isNull();
    }

    @Test
    void returnsUnavailableWhenBrainBodyIsEmpty() {
        RecoverySnapshot snapshot = snapshot();

        when(restTemplate.postForEntity(
                "http://localhost:8086/brain/recovery/dry-run",
                BrainRecoveryDryRunRequest.from(22L, snapshot),
                BrainRecoveryDryRunResponse.class
        )).thenReturn(ResponseEntity.ok(null));

        BrainRecoveryDryRunResult result =
                client.analyze(22L, snapshot);

        assertThat(result.status())
                .isEqualTo(BrainRecoveryDryRunStatus.UNAVAILABLE);
        assertThat(result.available()).isFalse();
        assertThat(result.failureCode())
                .isEqualTo("BRAIN_EMPTY_RESPONSE");
    }

    @Test
    void returnsUnavailableWhenBrainCallFails() {
        RecoverySnapshot snapshot = snapshot();

        when(restTemplate.postForEntity(
                "http://localhost:8086/brain/recovery/dry-run",
                BrainRecoveryDryRunRequest.from(22L, snapshot),
                BrainRecoveryDryRunResponse.class
        )).thenThrow(new RestClientException("connection refused"));

        BrainRecoveryDryRunResult result =
                client.analyze(22L, snapshot);

        assertThat(result.status())
                .isEqualTo(BrainRecoveryDryRunStatus.UNAVAILABLE);
        assertThat(result.available()).isFalse();
        assertThat(result.failureCode())
                .isEqualTo("BRAIN_UNAVAILABLE");
    }

    private RecoverySnapshot snapshot() {
        return new RecoverySnapshot(
                LocalDate.of(2026, 7, 22),
                new RecoveryState(
                        2,
                        1,
                        1,
                        3,
                        0,
                        7
                ),
                true,
                4,
                LocalDateTime.of(2026, 7, 22, 4, 0),
                0,
                8,
                null
        );
    }
}