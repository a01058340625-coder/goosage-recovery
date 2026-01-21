package com.goosage.dto.post;

import java.time.LocalDateTime;

import com.goosage.entity.PostEntity;

/**
 * ✅ [응답 DTO]
 * - 클라이언트에게 보여줄 필드만 선택해서 반환한다.
 * - Entity를 그대로 반환하면: 컬럼 추가/보안/지연로딩 문제 등 실무에서 사고가 난다.
 */
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    // ✅ Entity -> Response 변환을 한 곳에서 통일
    public static PostResponse from(PostEntity e) {
        PostResponse r = new PostResponse();
        r.id = e.getId();
        r.title = e.getTitle();
        r.content = e.getContent();
        r.createdAt = e.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
