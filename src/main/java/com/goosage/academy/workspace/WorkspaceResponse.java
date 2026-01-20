package com.goosage.academy.workspace;

import java.time.LocalDateTime;

public class WorkspaceResponse {
    private long id;
    private String name;
    private String planType;
    private LocalDateTime createdAt;

    public WorkspaceResponse(long id, String name, String planType, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.planType = planType;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getPlanType() { return planType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
