package com.goosage.academy.course;

public class CourseCreateRequest {
    private long workspaceId;
    private String title;
    private String description;

    public long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(long workspaceId) { this.workspaceId = workspaceId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
