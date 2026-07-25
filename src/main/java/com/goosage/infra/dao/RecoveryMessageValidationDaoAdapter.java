package com.goosage.infra.dao;

import org.springframework.stereotype.Component;

import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationCommand;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationPort;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationResult;

@Component
public class RecoveryMessageValidationDaoAdapter
        implements RecoveryMessageValidationPort {

    private final RecoveryMessageValidationDao dao;

    public RecoveryMessageValidationDaoAdapter(
            RecoveryMessageValidationDao dao
    ) {
        this.dao = dao;
    }

    @Override
    public RecoveryMessageValidationResult save(
            RecoveryMessageValidationCommand command
    ) {
        return dao.save(command);
    }
}