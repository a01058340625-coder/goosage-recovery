package com.goosage.api.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.goosage.app.study.StudyCoachService;
import com.goosage.domain.study.StudyCoachResult;
import com.goosage.support.web.ApiResponse;

@RestController
@RequestMapping("/internal/study")
public class InternalStudyCoachController {

    private final StudyCoachService studyCoachService;

    @Value("${goosage.internal.key:goosage-dev}")
    private String internalKey;

    public InternalStudyCoachController(StudyCoachService studyCoachService) {
        this.studyCoachService = studyCoachService;
    }

    @GetMapping("/coach")
    public ApiResponse<StudyCoachResult> coach(
            @RequestParam("userId") long userId,
            @RequestHeader(value = "X-INTERNAL-KEY", required = false) String key
    ) {
        if (key == null || !key.equals(internalKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_INTERNAL_KEY");
        }

        StudyCoachResult result = studyCoachService.coach(userId);
        return ApiResponse.ok(result);
    }
}