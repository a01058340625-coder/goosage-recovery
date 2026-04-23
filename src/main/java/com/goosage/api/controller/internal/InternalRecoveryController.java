package com.goosage.api.controller.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.goosage.app.RecoveryEventService;
import com.goosage.domain.EventType;
import com.goosage.domain.recovery.RecoverySnapshot;
import com.goosage.domain.recovery.RecoverySnapshotService;
import com.goosage.support.web.ApiResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/internal/recovery")
public class InternalRecoveryController {

    private static final String INTERNAL_KEY = "goosage-dev";

    private final RecoveryEventService recoveryEventService;
    private final RecoverySnapshotService recoverySnapshotService;
    private final BrainTriggerService brainTriggerService;

    public InternalRecoveryController(
            RecoveryEventService recoveryEventService,
            RecoverySnapshotService recoverySnapshotService,
            BrainTriggerService brainTriggerService
    ) {
        this.recoveryEventService = recoveryEventService;
        this.recoverySnapshotService = recoverySnapshotService;
        this.brainTriggerService = brainTriggerService;
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> recordEvent(
            @RequestHeader(value = "X-INTERNAL-KEY", required = false) String key,
            @RequestHeader(value = "X-BRAIN-ACTION", required = false) String brainAction,
            @RequestBody Map<String, Object> body
    ) {

        System.out.println("[INTERNAL] hit /internal/recovery/events");
        System.out.println("[INTERNAL] key=" + key);
        System.out.println("[INTERNAL] brainAction=" + brainAction);

        if (!INTERNAL_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "bad internal key");
        }

        Long userId = toLong(body.get("userId"));
        String typeStr = (String) body.get("type");
        Long knowledgeId = toLongNullable(body.get("knowledgeId"));

        if (userId == null || typeStr == null || typeStr.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId/type required");
        }

        boolean isBrainAction = "true".equalsIgnoreCase(brainAction);
        String brainActionType = toStringNullable(body.get("brainActionType"));
        String brainPatternType = toStringNullable(body.get("brainPatternType"));

        recoveryEventService.record(
                userId,
                EventType.valueOf(typeStr),
                knowledgeId,
                isBrainAction,
                brainActionType,
                brainPatternType
        );

        if (!isBrainAction) {
            try {
                RecoverySnapshot snapshot = recoverySnapshotService.snapshot(
                        userId,
                        LocalDate.now(),
                        LocalDateTime.now()
                );

                brainTriggerService.triggerRecoverySnapshot(
                        userId,
                        snapshot.recentEventCount3d(),
                        snapshot.streakDays(),
                        snapshot.daysSinceLastEvent(),
                        snapshot.state().urgeLogs(),
                        snapshot.state().betAttempts(),
                        snapshot.state().betBlockedCount(),
                        snapshot.state().recoveryActionCount(),
                        snapshot.state().relapseSignalCount()
                );

                System.out.println("[BRAIN_TRIGGER][RECOVERY] success userId=" + userId);

            } catch (Exception e) {
                System.out.println("[BRAIN_TRIGGER][RECOVERY] fail userId=" + userId + " error=" + e.getMessage());
            }
        } else {
            System.out.println("[BRAIN_TRIGGER][RECOVERY] skipped brain action userId=" + userId);
        }

        return ApiResponse.ok(null);
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        return Long.valueOf(v.toString());
    }

    private Long toLongNullable(Object v) {
        if (v == null) return null;
        String s = v.toString();
        if (s.isBlank() || "null".equalsIgnoreCase(s)) return null;
        return Long.valueOf(s);
    }

    private String toStringNullable(Object v) {
        if (v == null) return null;
        String s = v.toString();
        return s.isBlank() ? null : s;
    }
}