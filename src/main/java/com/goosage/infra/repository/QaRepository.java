package com.goosage.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goosage.entity.QaEntity;

public interface QaRepository extends JpaRepository<QaEntity, Long> {
}
