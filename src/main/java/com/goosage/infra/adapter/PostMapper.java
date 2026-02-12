package com.goosage.infra.adapter;

import com.goosage.dto.post.PostDto;
import com.goosage.entity.PostEntity;

public final class PostMapper {

    private PostMapper() {}

    public static PostDto toDto(PostEntity e) {
        if (e == null) return null;

        PostDto d = new PostDto();
        d.setId(e.getId());
        d.setTitle(e.getTitle());
        d.setContent(e.getContent());
        d.setCreatedAt(e.getCreatedAt());

        // PostDto에만 있는 확장필드(subject/summary/tags/source)는
        // 현재 posts 테이블에 컬럼이 없으므로 null/기본값 유지(=저장 안 됨)
        return d;
    }

    public static PostEntity toEntity(PostDto d) {
        if (d == null) return null;

        PostEntity e = new PostEntity();
        e.setId(d.getId());
        e.setTitle(d.getTitle());
        e.setContent(d.getContent());

        // createdAt: dto에 값이 있으면 존중, 없으면 Entity 기본값(LocalDateTime.now()) 유지
        if (d.getCreatedAt() != null) {
            e.setCreatedAt(d.getCreatedAt());
        }

        // userId: PostDto에는 없으니 여기서 세팅 불가(서비스에서 따로 세팅해야 함)
        return e;
    }
}
