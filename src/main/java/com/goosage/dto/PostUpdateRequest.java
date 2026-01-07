package com.goosage.dto;

/**
 * ✅ [수정 요청 DTO]
 * - PUT에서 수정할 값만 담는다.
 */
public class PostUpdateRequest {
    private String title;
    private String content;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
