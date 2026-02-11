package com.goosage.infra.repository;

import com.goosage.entity.QaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QaRepository extends JpaRepository<QaEntity, Long> {
}
