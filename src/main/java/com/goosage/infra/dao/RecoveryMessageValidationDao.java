package com.goosage.infra.dao;

import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationCommand;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationResult;

@Repository
public class RecoveryMessageValidationDao {

    private final JdbcTemplate jdbcTemplate;

    public RecoveryMessageValidationDao(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RecoveryMessageValidationResult save(
            RecoveryMessageValidationCommand command
    ) {
        String sql = """
            INSERT INTO recovery_message_validations (
                user_id,
                original_message,

                expected_analyzable,
                expected_hold_reason,
                expected_signal_json,
                expected_pattern_type,
                expected_next_action_type,

                actual_analyzable,
                actual_hold_reason,
                actual_signal_json,
                actual_pattern_type,
                actual_next_action_type,
                actual_recommended_action,

                validation_result,
                mismatch_type,
                review_memo,

                real_scenario_candidate,
                virtual_user_candidate,

                created_at
            )
            VALUES (
                ?, ?,
                ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?,
                ?, ?, ?,
                ?, ?,
                NOW()
            )
        """;

        jdbcTemplate.update(
                sql,
                command.userId(),
                command.originalMessage(),

                command.expectedAnalyzable(),
                command.expectedHoldReason(),
                command.expectedSignalJson(),
                command.expectedPatternType(),
                command.expectedNextActionType(),

                command.actualAnalyzable(),
                command.actualHoldReason(),
                command.actualSignalJson(),
                command.actualPatternType(),
                command.actualNextActionType(),
                command.actualRecommendedAction(),

                command.validationResult(),
                command.mismatchType(),
                command.reviewMemo(),

                command.realScenarioCandidate(),
                command.virtualUserCandidate()
        );

        Long id = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
        );

        LocalDateTime createdAt =
                jdbcTemplate.queryForObject(
                        """
                        SELECT created_at
                        FROM recovery_message_validations
                        WHERE id = ?
                        """,
                        LocalDateTime.class,
                        id
                );

        return new RecoveryMessageValidationResult(
                id == null ? 0L : id,
                command.userId(),
                command.validationResult(),
                command.realScenarioCandidate(),
                command.virtualUserCandidate(),
                createdAt
        );
    }
}