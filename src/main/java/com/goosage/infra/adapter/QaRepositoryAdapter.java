package com.goosage.infra.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.goosage.domain.qa.QaPort;
import com.goosage.domain.qa.QaView;
import com.goosage.infra.repository.QaRepository;

@Component
public class QaRepositoryAdapter implements QaPort {

    private final QaRepository qaRepository;

    public QaRepositoryAdapter(QaRepository qaRepository) {
        this.qaRepository = qaRepository;
    }

    @Override
    public Optional<QaView> findById(long id) {
        return qaRepository.findById(id)
                .map(q -> new QaView(
                        q.getId(),
                        q.getQuestion(),
                        q.getAnswer(),
                        q.getTags() // null OK
                ));
    }
}
