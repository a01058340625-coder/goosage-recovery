package com.goosage.service.study.action;

public record NextActionDto(
        NextActionType type,
        String label,
        Long knowledgeId,
        boolean requiresForge
) {}
