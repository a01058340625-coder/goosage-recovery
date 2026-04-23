package com.goosage.api.controller.internal;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BrainTriggerService {

    private final RestTemplate restTemplate;

    @Value("${goosage.brain.base-url:http://localhost:8086}")
    private String brainBaseUrl;

    public BrainTriggerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void triggerRecoverySnapshot(
            Long userId,
            int recentEventCount3d,
            int streakDays,
            int daysSinceLastEvent,
            int urgeLogs,
            int betAttempts,
            int betBlockedCount,
            int recoveryActionCount,
            int relapseSignalCount
    ) {
        String url = brainBaseUrl + "/brain/recovery/snapshot";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("recentEventCount3d", recentEventCount3d);
        body.put("streakDays", streakDays);
        body.put("daysSinceLastEvent", daysSinceLastEvent);
        body.put("urgeLogs", urgeLogs);
        body.put("betAttempts", betAttempts);
        body.put("betBlockedCount", betBlockedCount);
        body.put("recoveryActionCount", recoveryActionCount);
        body.put("relapseSignalCount", relapseSignalCount);

        restTemplate.postForEntity(
                url,
                new HttpEntity<>(body, headers),
                String.class
        );
    }
}