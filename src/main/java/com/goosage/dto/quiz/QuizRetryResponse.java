package com.goosage.dto.quiz;

import java.util.List;

public record QuizRetryResponse(
    long knowledgeId,
    long baseResultId,
    List<QuizRetryQuestion> wrong
) {}
