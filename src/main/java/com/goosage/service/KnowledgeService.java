package com.goosage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.goosage.dto.KnowledgeDto;
import com.goosage.entity.QaEntity;
import com.goosage.repository.KnowledgeRepository;
import com.goosage.repository.QaRepository;

@Service
public class KnowledgeService {

    private final KnowledgeRepository repository;
    private final QaRepository qaRepository;


    public KnowledgeService(KnowledgeRepository repository, QaRepository qaRepository) {
        this.repository = repository;
        this.qaRepository = qaRepository;
    }


    public List<KnowledgeDto> findAll() {
        return repository.findAll();
    }

    public KnowledgeDto save(KnowledgeDto req) {
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

    public KnowledgeDto fromQa(long qaId) {

        // 1) 이미 변환된 Knowledge 있으면 그대로 반환
        return repository.findBySourceAndSourceId("qa", qaId)
                .orElseGet(() -> {

                    // 2) QA 조회 (QaDto가 아니라 QaEntity)
                    QaEntity qa = qaRepository.findById(qaId)
                            .orElseThrow(() -> new IllegalArgumentException("qa not found: " + qaId));

                    // 3) answer 없으면 변환 금지
                    if (isBlank(qa.getAnswer())) {
                        throw new IllegalArgumentException("qa has no answer yet: " + qaId);
                    }

                    // 4) Knowledge 생성 (진짜 데이터로 채움)
                    KnowledgeDto created = new KnowledgeDto();
                    created.setType("QNA");
                    created.setSource("qa");
                    created.setSourceId(qaId);

                    created.setTitle(qa.getQuestion());
                    created.setContent(qa.getAnswer());

                    // tags는 DTO가 List<String>이라 일단 null (v0.22에서 정리)
                    created.setTags(null);

                    return repository.save(created);
                });
    }

}