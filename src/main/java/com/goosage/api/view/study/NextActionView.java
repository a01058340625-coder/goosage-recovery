package com.goosage.api.view.study;

import com.goosage.domain.NextActionType;

public class NextActionView {

    private NextActionType type;
    private String label;
    private Long knowledgeId;
    private boolean requiresForge;
    private String reason;

    public NextActionView(
            NextActionType type,
            String label,
            Long knowledgeId,
            boolean requiresForge,
            String reason
    ) {
        this.type = type;
        this.label = label;
        this.knowledgeId = knowledgeId;
        this.requiresForge = requiresForge;
        this.reason = reason;
    }

    public NextActionType getType() { return type; }
    public String getLabel() { return label; }
    public Long getKnowledgeId() { return knowledgeId; }
    public boolean isRequiresForge() { return requiresForge; }
    public String getReason() { return reason; }
}
