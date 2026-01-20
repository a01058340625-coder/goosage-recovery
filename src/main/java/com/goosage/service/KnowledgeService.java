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
        if (id == null) return Optional.empty();
        return repository.findById(id.longValue());
    }

    public KnowledgeDto mustFindById(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("knowledge not found: " + id));
    }

    public KnowledgeDto mustFindById(Long id) {
        if (id == null) throw new IllegalArgumentException("id is required");
        return mustFindById(id.longValue());
    }




    public List<KnowledgeDto> findAll() {
        return repository.findAll();
    }


    public KnowledgeDto save(KnowledgeDto req) {
        if (req == null) throw new IllegalArgumentException("body가 비었습니다.");

        // ✅ type 기본값
        if (isBlank(req.getType())) req.setType("MANUAL");

        // ✅ 필수값
        if (isBlank(req.getTitle())) throw new IllegalArgumentException("title은 필수입니다.");
        if (req.getContent() == null) req.setContent("");

        // ✅ source/sourceId가 있으면 중복 생성 방지 (idempotent)
        if (!isBlank(req.getSource()) && req.getSourceId() != null) {
            repository.findBySourceAndSourceId(req.getSource(), req.getSourceId())
                    .ifPresent(existing -> {
                        throw new IllegalStateException("DUPLICATE_SOURCE_SOURCEID:" + existing.getId());
                    });
        }

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

        return repository.findBySourceAndSourceId("qa", qaId)
                .orElseGet(() -> {

                    QaEntity qa = qaRepository.findById(qaId)
                            .orElseThrow(() -> new IllegalArgumentException("qa not found: " + qaId));

                    KnowledgeDto created = new KnowledgeDto();
                    created.setType("QA");
                    created.setSource("qa");
                    created.setSourceId(qaId);

                    created.setTitle(qa.getQuestion());
                    created.setContent(qa.getAnswer() == null ? "" : qa.getAnswer());

                    // subject 쓰는 정책이면 유지, 아니면 제거
                    created.setSubject("QA");

                    // ✅ QA tags 문자열이 있다면 여기서 변환 (예: "a,b,c")
                    // QaEntity에 getTags()가 있다고 가정. 없으면 이 블록은 주석처리.
                    if (qa.getTags() != null && !qa.getTags().trim().isEmpty()) {
                        List<String> tags = java.util.Arrays.stream(qa.getTags().split(","))
                                .map(String::trim)
                                .filter(s -> !s.isBlank())
                                .distinct()
                                .toList();
                        created.setTags(tags);
                    } else {
                        created.setTags(List.of());
                    }

                    // ✅ 여기서 save()를 다시 태워도 됨(정책 1곳)
                    return save(created);
                });
    }

}
