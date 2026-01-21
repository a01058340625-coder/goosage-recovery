package com.goosage.dto.post;

/**
 * ✅ [요청 DTO]
 * - 클라이언트가 POST로 보낼 값만 담는다.
 * - Entity를 직접 받지 않는 이유: 보안/구조/확장성 때문.
 */
public class PostCreateRequest {
    private String title;
    private String content;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
