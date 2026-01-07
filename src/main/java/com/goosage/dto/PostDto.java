package com.goosage.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDto {

    private Long id;
    private String subject;     // 예: DB, OS, Network, Math, Physics ...
    private String title;       // 지식 제목
    private String content;     // 원문(노트/정리/문제/해설)
    private String summary;     // 요약(없으면 null 가능)
    private List<String> tags;  // 태그들
    private String source;      // 출처(책/강의/URL/내노트)
    private LocalDateTime createdAt;

    public PostDto() {}

    // --- getters/setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
