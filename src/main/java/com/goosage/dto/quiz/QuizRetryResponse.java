package com.goosage.dto.quiz;

import java.util.List;
import com.goosage.dto.quiz.QuizRetryQuestion;

public record QuizRetryResponse(
    long knowledgeId,
    long baseResultId,
    List<QuizRetryQuestion> wrong
) {}
