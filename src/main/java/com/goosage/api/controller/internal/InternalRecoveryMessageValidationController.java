package com.goosage.api.controller.internal;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goosage.api.view.recovery.message.validation.RecoveryMessageValidationListItemResponse;
import com.goosage.app.recovery.message.validation.RecoveryMessageValidationService;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationQuery;
import com.goosage.support.web.ApiResponse;

import com.goosage.support.web.UnauthorizedException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalRecoveryMessageValidationController {

    private static final String INTERNAL_KEY =
            "goosage-dev";

    private final RecoveryMessageValidationService validationService;
    private final ObjectMapper objectMapper;

    public InternalRecoveryMessageValidationController(
            RecoveryMessageValidationService validationService,
            ObjectMapper objectMapper
    ) {
        this.validationService = validationService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(
            "/internal/recovery/message/validations"
    )
    public ApiResponse<
            List<RecoveryMessageValidationListItemResponse>
    > findRecent(
            @RequestHeader(
                    value = "X-INTERNAL-KEY",
                    required = false
            )
            String internalKey,
            @RequestParam
            long userId,
            @RequestParam(required = false)
            String validationResult,
            @RequestParam(required = false)
            Boolean realScenarioCandidate,
            @RequestParam(required = false)
            Boolean virtualUserCandidate,
            @RequestParam(defaultValue = "100")
            int limit
    ) {
        requireInternalKey(internalKey);

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

    private void requireInternalKey(
            String internalKey
    ) {
        if (!INTERNAL_KEY.equals(internalKey)) {
            throw new UnauthorizedException(
                    "bad internal key"
            );
        }
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
}