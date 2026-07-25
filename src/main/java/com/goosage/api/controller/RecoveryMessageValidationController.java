package com.goosage.api.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goosage.api.view.recovery.message.validation.RecoveryMessageValidationListItemResponse;
import com.goosage.api.view.recovery.message.validation.RecoveryMessageValidationSaveRequest;
import com.goosage.api.view.recovery.message.validation.RecoveryMessageValidationSaveResponse;
import com.goosage.app.recovery.message.validation.RecoveryMessageValidationService;
import com.goosage.auth.SessionConst;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationCommand;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationQuery;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationResult;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/recovery/message/validations")
    public ApiResponse<
            List<RecoveryMessageValidationListItemResponse>
    > findRecent(
            @RequestParam(required = false)
            String validationResult,
            @RequestParam(required = false)
            Boolean realScenarioCandidate,
            @RequestParam(required = false)
            Boolean virtualUserCandidate,
            @RequestParam(defaultValue = "20")
            int limit,
            HttpSession session
    ) {
        long userId = requireUserId(session);

        RecoveryMessageValidationQuery query =
                new RecoveryMessageValidationQuery(
                        userId,
                        validationResult,
                        realScenarioCandidate,
                        virtualUserCandidate,
                        limit
                );

        List<RecoveryMessageValidationListItemResponse>
                response =
                validationService.findRecent(query)
                        .stream()
                        .map(item ->
                                new RecoveryMessageValidationListItemResponse(
                                        item.id(),
                                        item.userId(),
                                        item.originalMessage(),

                                        item.expectedAnalyzable(),
                                        item.expectedHoldReason(),
                                        fromJson(
                                                item.expectedSignalJson()
                                        ),
                                        item.expectedPatternType(),
                                        item.expectedNextActionType(),

                                        item.actualAnalyzable(),
                                        item.actualHoldReason(),
                                        fromJson(
                                                item.actualSignalJson()
                                        ),
                                        item.actualPatternType(),
                                        item.actualNextActionType(),
                                        item.actualRecommendedAction(),

                                        item.validationResult(),
                                        item.mismatchType(),
                                        item.reviewMemo(),

                                        item.realScenarioCandidate(),
                                        item.virtualUserCandidate(),

                                        item.createdAt()
                                )
                        )
                        .toList();

        return ApiResponse.ok(response);
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

    private Object fromJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(
                    value,
                    Object.class
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "SIGNAL_JSON_DESERIALIZATION_FAILED",
                    exception
            );
        }
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