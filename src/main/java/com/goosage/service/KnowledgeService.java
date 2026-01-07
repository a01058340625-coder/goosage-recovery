package com.goosage.service;

import com.goosage.dto.KnowledgeDto;
import com.goosage.repository.KnowledgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeService {

    private final KnowledgeRepository repository;

    public KnowledgeService(KnowledgeRepository repository) {
        this.repository = repository;
    }

    public List<KnowledgeDto> findAll() {
        return repository.findAll();
    }

    public KnowledgeDto save(KnowledgeDto req) {
        // (천천히 버전) 최소 검증만
        if (req == null) {
            throw new IllegalArgumentException("body가 비었습니다.");
        }
        if (isBlank(req.getSubject())) {
            throw new IllegalArgumentException("subject는 필수입니다.");
        }
        if (isBlank(req.getTitle())) {
            throw new IllegalArgumentException("title은 필수입니다.");
        }
        if (isBlank(req.getContent())) {
            throw new IllegalArgumentException("content는 필수입니다.");
        }
        return repository.save(req);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

