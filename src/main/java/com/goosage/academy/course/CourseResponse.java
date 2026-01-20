package com.goosage.academy.course;

import java.time.LocalDateTime;
import java.util.List;

public class CourseResponse {
    private long id;
    private long workspaceId;
    private String title;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;

    private List<CourseItemResponse> items;

    public CourseResponse(long id, long workspaceId, String title, String description,
                          boolean active, LocalDateTime createdAt, List<CourseItemResponse> items) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.title = title;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.items = items;
    }

    public long getId() { return id; }
    public long getWorkspaceId() { return workspaceId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<CourseItemResponse> getItems() { return items; }
}
