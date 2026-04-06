package com.goosage.api.internal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.recovery.RecoveryCoachService;
import com.goosage.domain.recovery.RecoveryCoachResult;

@RestController
public class InternalRecoveryCoachController {

    private final RecoveryCoachService recoveryCoachService;

    public InternalRecoveryCoachController(RecoveryCoachService recoveryCoachService) {
        this.recoveryCoachService = recoveryCoachService;
    }

    @GetMapping("/internal/recovery/coach")
    public RecoveryCoachResult coach(
            @RequestHeader("X-INTERNAL-KEY") String internalKey,
            @RequestParam("userId") long userId
    ) {
        return recoveryCoachService.coach(userId);
    }
}