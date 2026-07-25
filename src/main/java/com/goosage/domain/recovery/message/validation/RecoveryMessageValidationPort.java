package com.goosage.domain.recovery.message.validation;

import java.util.List;

public interface RecoveryMessageValidationPort {

    RecoveryMessageValidationResult save(
            RecoveryMessageValidationCommand command
    );

    List<RecoveryMessageValidationItem> findRecent(
            RecoveryMessageValidationQuery query
    );
}