package com.goosage.domain.recovery.message.validation;

public interface RecoveryMessageValidationPort {

    RecoveryMessageValidationResult save(
            RecoveryMessageValidationCommand command
    );
}