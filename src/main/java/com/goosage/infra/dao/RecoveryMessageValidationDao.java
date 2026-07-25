package com.goosage.infra.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationCommand;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationItem;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationQuery;
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

    public List<RecoveryMessageValidationItem> findRecent(
            RecoveryMessageValidationQuery query
    ) {
        StringBuilder sql = new StringBuilder(
                """
                SELECT
                    id,
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
                FROM recovery_message_validations
                WHERE user_id = ?
                """
        );

        List<Object> parameters = new ArrayList<>();
        parameters.add(query.userId());

        if (query.validationResult() != null) {
            sql.append(
                    " AND validation_result = ?"
            );
            parameters.add(query.validationResult());
        }

        if (query.realScenarioCandidate() != null) {
            sql.append(
                    " AND real_scenario_candidate = ?"
            );
            parameters.add(
                    query.realScenarioCandidate()
            );
        }

        if (query.virtualUserCandidate() != null) {
            sql.append(
                    " AND virtual_user_candidate = ?"
            );
            parameters.add(
                    query.virtualUserCandidate()
            );
        }

        sql.append(
                """
                 ORDER BY created_at DESC, id DESC
                 LIMIT ?
                """
        );
        parameters.add(query.limit());

        return jdbcTemplate.query(
                sql.toString(),
                (resultSet, rowNumber) ->
                        new RecoveryMessageValidationItem(
                                resultSet.getLong("id"),
                                resultSet.getLong("user_id"),
                                resultSet.getString(
                                        "original_message"
                                ),

                                resultSet.getObject(
                                        "expected_analyzable",
                                        Boolean.class
                                ),
                                resultSet.getString(
                                        "expected_hold_reason"
                                ),
                                resultSet.getString(
                                        "expected_signal_json"
                                ),
                                resultSet.getString(
                                        "expected_pattern_type"
                                ),
                                resultSet.getString(
                                        "expected_next_action_type"
                                ),

                                resultSet.getBoolean(
                                        "actual_analyzable"
                                ),
                                resultSet.getString(
                                        "actual_hold_reason"
                                ),
                                resultSet.getString(
                                        "actual_signal_json"
                                ),
                                resultSet.getString(
                                        "actual_pattern_type"
                                ),
                                resultSet.getString(
                                        "actual_next_action_type"
                                ),
                                resultSet.getString(
                                        "actual_recommended_action"
                                ),

                                resultSet.getString(
                                        "validation_result"
                                ),
                                resultSet.getString(
                                        "mismatch_type"
                                ),
                                resultSet.getString(
                                        "review_memo"
                                ),

                                resultSet.getBoolean(
                                        "real_scenario_candidate"
                                ),
                                resultSet.getBoolean(
                                        "virtual_user_candidate"
                                ),

                                resultSet.getObject(
                                        "created_at",
                                        LocalDateTime.class
                                )
                        ),
                parameters.toArray()
        );
    }
}