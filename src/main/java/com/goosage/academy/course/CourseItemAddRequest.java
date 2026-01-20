package com.goosage.academy.course;

public class CourseItemAddRequest {
    private long knowledgeId;
    private int orderIndex;

    public long getKnowledgeId() { return knowledgeId; }
    public void setKnowledgeId(long knowledgeId) { this.knowledgeId = knowledgeId; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
}
