package com.goosage.app;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.goosage.domain.knowledge.KnowledgeCrudPort;
import com.goosage.domain.qa.QaPort;
import com.goosage.domain.qa.QaView;
import com.goosage.dto.KnowledgeDto;
import com.goosage.support.web.ConflictException;

@Service
public class KnowledgeService {

    private final KnowledgeCrudPort repository;
    private final QaPort qaPort;

    public KnowledgeService(KnowledgeCrudPort repository, QaPort qaPort) {
        this.repository = repository;
        this.qaPort = qaPort;
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

        if (isBlank(req.getType())) req.setType("MANUAL");

        if (isBlank(req.getTitle())) throw new IllegalArgumentException("title은 필수입니다.");
        if (req.getContent() == null) req.setContent("");

        if (!isBlank(req.getSource()) && req.getSourceId() != null) {
            repository.findBySourceAndSourceId(req.getSource(), req.getSourceId())
                    .ifPresent(existing -> {
                        throw new ConflictException("DUPLICATE_SOURCE_SOURCEID:" + existing.getId());
                    });
        }

        return repository.save(req);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public KnowledgeDto convertQaToKnowledge(long qaId) {

        return repository.findBySourceAndSourceId("qa", qaId)
                .orElseGet(() -> {

                    QaView qa = qaPort.findById(qaId)
                            .orElseThrow(() -> new IllegalArgumentException("qa not found: " + qaId));

                    KnowledgeDto created = new KnowledgeDto();
                    created.setType("QA");
                    created.setSource("qa");
                    created.setSourceId(qaId);

                    created.setTitle(qa.question());
                    created.setContent(qa.answer() == null ? "" : qa.answer());

                    created.setSubject("QA");

                    String tagsCsv = qa.tagsCsv();
                    if (tagsCsv != null && !tagsCsv.trim().isEmpty()) {
                        List<String> tags = java.util.Arrays.stream(tagsCsv.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isBlank())
                                .distinct()
                                .toList();
                        created.setTags(tags);
                    } else {
                        created.setTags(List.of());
                    }

                    return save(created);
                });
    }
}
