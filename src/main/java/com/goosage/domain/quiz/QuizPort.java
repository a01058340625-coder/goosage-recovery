package com.goosage.domain.quiz;

import java.util.List;

import com.goosage.infra.dao.QuizResultDao.QuizResultRow;

public interface QuizPort {

    // 결과 조회
    List<QuizResultRow> findByKnowledgeId(long knowledgeId);
    QuizResultRow findLatestByKnowledgeId(long knowledgeId);
    QuizResultRow findLatestByUserAndKnowledgeId(long userId, long knowledgeId);

    // 저장 + 이벤트 기록
    void saveResultAndRecordEvent(
            long userId,
            long knowledgeId,
            int total,
            int correct,
            int percent,
            int wrongCount,
            String detailsJson
    );

    // 퀴즈 아이템(있으면 skip)
    boolean quizItemsExist(long knowledgeId);
    void insertQuizItem(long knowledgeId, int no, String question, String expected);
}
