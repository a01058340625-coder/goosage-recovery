package com.goosage.infra.adapter;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.goosage.domain.quiz.QuizPort;
import com.goosage.infra.dao.QuizItemDao;
import com.goosage.infra.dao.QuizResultDao;
import com.goosage.infra.dao.StudyEventDao;
import com.goosage.infra.dao.QuizResultDao.QuizResultRow;

@Repository
public class QuizJdbcAdapter implements QuizPort {

    private final QuizResultDao quizResultDao;
    private final QuizItemDao quizItemDao;
    private final StudyEventDao studyEventDao;

    public QuizJdbcAdapter(QuizResultDao quizResultDao, QuizItemDao quizItemDao, StudyEventDao studyEventDao) {
        this.quizResultDao = quizResultDao;
        this.quizItemDao = quizItemDao;
        this.studyEventDao = studyEventDao;
    }

    @Override
    public List<QuizResultRow> findByKnowledgeId(long knowledgeId) {
        return quizResultDao.findByKnowledgeId(knowledgeId);
    }

    @Override
    public QuizResultRow findLatestByKnowledgeId(long knowledgeId) {
        return quizResultDao.findLatestByKnowledgeId(knowledgeId);
    }

    @Override
    public QuizResultRow findLatestByUserAndKnowledgeId(long userId, long knowledgeId) {
        return quizResultDao.findLatestByUserAndKnowledgeId(userId, knowledgeId);
    }

    @Override
    public void saveResultAndRecordEvent(long userId, long knowledgeId, int total, int correct, int percent,
                                        int wrongCount, String detailsJson) {

        quizResultDao.save(userId, knowledgeId, total, correct, percent, wrongCount, detailsJson);
        studyEventDao.recordEvent(userId, "QUIZ_SUBMIT", "KNOWLEDGE", knowledgeId, detailsJson);
    }

    @Override
    public boolean quizItemsExist(long knowledgeId) {
        return quizItemDao.exists(knowledgeId);
    }

    @Override
    public void insertQuizItem(long knowledgeId, int no, String question, String expected) {
        quizItemDao.insert(knowledgeId, no, question, expected);
    }
}
