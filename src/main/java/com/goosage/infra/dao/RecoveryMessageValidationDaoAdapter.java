package com.goosage.infra.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationCommand;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationItem;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationPort;
import com.goosage.domain.recovery.message.validation.RecoveryMessageValidationQuery;
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

    @Override
    public List<RecoveryMessageValidationItem> findRecent(
            RecoveryMessageValidationQuery query
    ) {
        return dao.findRecent(query);
    }
}