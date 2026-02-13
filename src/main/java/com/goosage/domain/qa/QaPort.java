package com.goosage.domain.qa;

import java.util.List;
import java.util.Optional;

import com.goosage.entity.QaEntity;

public interface QaPort {
    QaEntity save(QaEntity e);
    List<QaEntity> findAll();
    Optional<QaEntity> findById(long id);
}
