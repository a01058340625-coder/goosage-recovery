package com.goosage.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goosage.api.view.recovery.message.validation.RecoveryMessageValidationSaveRequest;
import com.goosage.api.view.recovery.message.validation.RecoveryMessageValidationSaveResponse;
import com.goosage.app.recovery.message.validation.RecoveryMessageValidationService;
import com.goosage.auth.SessionConst;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationCommand;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationResult;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecoveryMessageValidationController {

    private final RecoveryMessageValidationService validationService;
    private final ObjectMapper objectMapper;

    public RecoveryMessageValidationController(
            RecoveryMessageValidationService validationService,
            ObjectMapper objectMapper
    ) {
        this.validationService = validationService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/recovery/message/validations")
    public ApiResponse<RecoveryMessageValidationSaveResponse> save(
            @RequestBody(required = false)
            RecoveryMessageValidationSaveRequest request,
            HttpSession session
    ) {
        long userId = requireUserId(session);

        if (request == null) {
            return ApiResponse.fail(
                    "VALIDATION_REQUEST_REQUIRED"
            );
        }

        RecoveryMessageValidationCommand command =
                new RecoveryMessageValidationCommand(
                        userId,
                        request.originalMessage(),

                        request.expectedAnalyzable(),
                        request.expectedHoldReason(),
                        toJson(request.expectedSignal()),
                        request.expectedPatternType(),
                        request.expectedNextActionType(),

                        request.actualAnalyzable(),
                        request.actualHoldReason(),
                        toJson(request.actualSignal()),
                        request.actualPatternType(),
                        request.actualNextActionType(),
                        request.actualRecommendedAction(),

                        request.validationResult(),
                        request.mismatchType(),
                        request.reviewMemo(),

                        request.realScenarioCandidate(),
                        request.virtualUserCandidate()
                );

        RecoveryMessageValidationResult result =
                validationService.save(command);

        return ApiResponse.ok(
                RecoveryMessageValidationSaveResponse.from(
                        result
                )
        );
    }

    private long requireUserId(HttpSession session) {
        Object value = session.getAttribute(
                SessionConst.LOGIN_USER_ID
        );

        if (value instanceof Long userId) {
            return userId;
        }

        if (value instanceof Integer userId) {
            return userId.longValue();
        }

        throw new IllegalArgumentException(
                "UNAUTHORIZED"
        );
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(
                    value
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "SIGNAL_JSON_SERIALIZATION_FAILED",
                    exception
            );
        }
    }
}