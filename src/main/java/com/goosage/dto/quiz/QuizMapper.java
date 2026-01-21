package com.goosage.dto.quiz;

import java.util.List;

import com.goosage.dao.QuizResultDao;
import com.goosage.dto.quiz.QuizRetryQuestion;

public class QuizMapper {
    private QuizMapper() {}

    public static QuizRetryResponse toWrongResponse(
            long knowledgeId,
            QuizResultDao.QuizResultRow latest,
            List<QuizRetryQuestion> wrong
    ) {
        long baseResultId = (latest == null) ? 0L : latest.id();
        return new QuizRetryResponse(knowledgeId, baseResultId, wrong);
    }
}
