package com.goosage.dto.post;

import java.time.LocalDateTime;

import com.goosage.entity.PostEntity;

public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static PostResponse from(PostEntity e) {
        PostResponse r = new PostResponse();
        r.id = e.getId();
        r.title = e.getTitle();
        r.content = e.getContent();
        r.createdAt = e.getCreatedAt();
        return r;
    }

    // ✅ Service에서 Entity 없이도 응답 만들 수 있게 잠금
    public static PostResponse fromDto(PostDto d) {
        PostResponse r = new PostResponse();
        r.id = d.getId();
        r.title = d.getTitle();
        r.content = d.getContent();
        r.createdAt = d.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
