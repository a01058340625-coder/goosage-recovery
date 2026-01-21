package com.goosage.dto.knowledge;

import java.util.List;

import com.goosage.dto.KnowledgeDto;

public final class KnowledgeMapper {

    private KnowledgeMapper() {}

    public static KnowledgeResponse toResponse(KnowledgeDto k) {
        if (k == null) return null;

        KnowledgeResponse r = new KnowledgeResponse();
        r.setId(k.getId());
        r.setTitle(k.getTitle());
        r.setContent(k.getContent());
        r.setTags(k.getTags());
        r.setSource(k.getSource());
        r.setCreatedAt(k.getCreatedAt());
        return r;
    }

    public static List<KnowledgeResponse> toResponseList(List<KnowledgeDto> list) {
        if (list == null) return List.of();
        return list.stream().map(KnowledgeMapper::toResponse).toList();
    }

    public static KnowledgeDto toDto(KnowledgeCreateRequest req) {
        if (req == null) return null;

        KnowledgeDto dto = new KnowledgeDto();
        dto.setTitle(req.getTitle());
        dto.setContent(req.getContent());
        dto.setSource(req.getSource());
        dto.setSourceId(req.getSourceId());
        dto.setTags(req.getTags());
        return dto;
    }
}
