package com.goosage.dto;

import java.util.List;

public record QuizRetryResponse(
        long knowledgeId,
        long baseResultId,
        int retryCount,
        List<QuizRetryQuestion> questions
) {}
