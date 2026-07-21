package com.goosage.app.recovery.message.brain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.goosage.domain.recovery.RecoverySnapshot;

@Service
public class BrainRecoveryDryRunClient {

    private final RestTemplate restTemplate;

    @Value("${goosage.brain.base-url:http://localhost:8086}")
    private String brainBaseUrl;

    public BrainRecoveryDryRunClient(
            RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    public BrainRecoveryDryRunResult analyze(
            long userId,
            RecoverySnapshot projectedSnapshot
    ) {
        BrainRecoveryDryRunRequest request =
                BrainRecoveryDryRunRequest.from(
                        userId,
                        projectedSnapshot
                );

        String url = brainBaseUrl + "/brain/recovery/dry-run";

        try {
            ResponseEntity<BrainRecoveryDryRunResponse> response =
                    restTemplate.postForEntity(
                            url,
                            request,
                            BrainRecoveryDryRunResponse.class
                    );

            BrainRecoveryDryRunResponse body = response.getBody();

            if (!response.getStatusCode().is2xxSuccessful()
                    || body == null) {
                return BrainRecoveryDryRunResult.unavailable(
                        "BRAIN_EMPTY_RESPONSE"
                );
            }

            return BrainRecoveryDryRunResult.available(body);

        } catch (RestClientException exception) {
            return BrainRecoveryDryRunResult.unavailable(
                    "BRAIN_UNAVAILABLE"
            );
        }
    }
}