package com.goosage.academy.course;

public class CourseItemResponse {
    private long id;
    private long knowledgeId;
    private int orderIndex;

    public CourseItemResponse(long id, long knowledgeId, int orderIndex) {
        this.id = id;
        this.knowledgeId = knowledgeId;
        this.orderIndex = orderIndex;
    }

    public long getId() { return id; }
    public long getKnowledgeId() { return knowledgeId; }
    public int getOrderIndex() { return orderIndex; }
}
