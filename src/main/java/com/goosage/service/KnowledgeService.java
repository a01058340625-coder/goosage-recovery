package com.goosage.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    public Optional<KnowledgeDto> findById(Long id) {
        return repository.findAll().stream()
                .filter(k -> k.getId() != null && k.getId().equals(id))
                .findFirst();
    }

    public KnowledgeDto mustFindById(Long id) {
        return findAll().stream()
                .filter(k -> k.getId() != null && k.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("knowledge not found: " + id));
    }

    public List<KnowledgeDto> findAll() {
        return repository.findAll();
    }

    public KnowledgeDto save(KnowledgeDto req) {
        if (req == null) throw new IllegalArgumentException("body가 비었습니다.");
        if (isBlank(req.getSubject())) throw new IllegalArgumentException("subject는 필수입니다.");
        if (isBlank(req.getTitle())) throw new IllegalArgumentException("title은 필수입니다.");
        if (isBlank(req.getContent())) throw new IllegalArgumentException("content는 필수입니다.");
        return repository.save(req);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * QA -> Knowledge 변환 (idempotent: 이미 있으면 그대로 반환)
     * - source/sourceId로 중복 생성 방지
     * - Knowledge의 필수 필드(subject/title/content) 채움
     * - qa.answer가 null이어도 content는 ""로 채워서 DB/검증 통과
     * - tags는 QA 테이블이 문자열("a,b") 형태로 이미 확정이므로 그대로 복사
     */
    public KnowledgeDto convertQaToKnowledge(long qaId) {

        // ✅ 이미 변환된 Knowledge 있으면 그대로 반환
        return repository.findBySourceAndSourceId("qa", qaId)
                .orElseGet(() -> {

                    // ✅ QA 조회 (QaDto or QaEntity 중 네 레포 반환 타입에 맞추기)
                	QaEntity qa = qaRepository.findById(qaId)
                	        .orElseThrow(() -> new IllegalArgumentException("qa not found: " + qaId));

                    KnowledgeDto created = new KnowledgeDto();

                    // DB 컬럼에 type이 있으면 채우고, 없으면 무시돼도 됨(DTO에 필드가 있으니 OK)
                    created.setType("QA");

                    // ✅ 출처 고정 (주의: 네 기존 코드가 source를 "qa"로 쓰고 있음 -> 그대로 "qa"로 통일)
                    created.setSource("qa");
                    created.setSourceId(qaId);

                    // ✅ Knowledge 필수값 채우기
                    created.setSubject("QA"); // subject 필수라서 고정값으로라도 넣어야 함
                    created.setTitle(qa.getQuestion());
                    created.setContent(qa.getAnswer() == null ? "" : qa.getAnswer());

                    created.setTags(null);


                    return repository.save(created);
                });
    }
}
