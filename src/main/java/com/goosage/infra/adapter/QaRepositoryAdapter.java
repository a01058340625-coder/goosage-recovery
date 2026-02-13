package com.goosage.infra.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.goosage.domain.qa.QaPort;
import com.goosage.entity.QaEntity;
import com.goosage.infra.repository.QaRepository;

@Repository
public class QaRepositoryAdapter implements QaPort {

    private final QaRepository qaRepository;

    public QaRepositoryAdapter(QaRepository qaRepository) {
        this.qaRepository = qaRepository;
    }

    @Override
    public QaEntity save(QaEntity e) {
        return qaRepository.save(e);
    }

    @Override
    public List<QaEntity> findAll() {
        return qaRepository.findAll();
    }

    @Override
    public Optional<QaEntity> findById(long id) {
        return qaRepository.findById(id);
    }
}
